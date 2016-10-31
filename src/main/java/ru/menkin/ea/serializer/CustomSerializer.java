package ru.menkin.ea.serializer;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public class CustomSerializer {
	public static final String FILE_PATH = "D:\\Result.txt";
    public static final String UTF8 = "UTF-8";
    private static final int START_POINT = 0;

    public void serialize(Object serializable) {
        ArrayList<Byte> byteSequence = new ArrayList<Byte>();
        try {
            exploreGraph(serializable, byteSequence);
            writeToFile(byteSequence);
            showSerialization(byteSequence);
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }

        System.out.println("\nSerialization compete\n");
    }

    public void showSerialization(ArrayList<Byte> byteSequence) throws IOException {
        System.out.println("Byte sequence");
        System.out.println(byteSequence);
        System.out.println("Result file");
        FileInputStream fis = new FileInputStream(new File(FILE_PATH));
        while (fis.available() > 0) {
            System.out.print((char) fis.read());
        }
        fis.close();
    }

	public void printGraph(Map<String, Object> result) throws Exception {
        for(Map.Entry<String, Object> map : result.entrySet()){
            if (map.getValue().getClass().getName().startsWith("ru")) {
                int index = 1;
                printFields(map.getValue(), index);
            } else {
                System.out.println(map.getKey() + " " + map.getValue());
            }
        }
    }

    public void printFields(Object obj, int index) throws Exception {
        Class<?> objClass = obj.getClass();
        System.out.println(addTab(index++) + objClass.getName());

        Field[] fields = objClass.getFields();
        for(Field field : fields) {
            if (field.getType().getName().startsWith("ru")) {
                printFields(field.get(obj), index);
            } else {
                String name = field.getName();
                Object value = field.get(obj);

                System.out.println(addTab(index) + name + ": " + value.toString());
            }
        }
    }

    //для наглядности делаем отступы, чтобы показать всю структуру (аля Питон)
    private String addTab(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append("\t");
        }
        return sb.toString();
    }

	public void deserialize(String filePath){
		File file = new File(filePath);
        byte[] byteArray = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(byteArray);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int byteIterator = byteArray[START_POINT];
        char[] charArray = new char[byteIterator];
        for (int i = 0; i < byteIterator; i++) {
            charArray[i] = (char) byteArray[i + 1];
        }
        try {
            Class<?> clazz = Class.forName(String.valueOf(charArray));
            Constructor constructor = clazz.getConstructor(new Class[]{Map.class});

            Result result = buildTree(byteIterator, byteArray, clazz);
            Object obj = constructor.newInstance(result.getMap());

            System.out.println(obj.getClass().getName());
            printGraph(result.getMap());
            System.out.println("Deserialization compete");
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

    //подготавливаем(заполняем) байтовый массив для записи в файл
    private void fill(Object instance, ArrayList<Byte> byteSequence) throws UnsupportedEncodingException {
        byte[] className = instance.getClass().getCanonicalName().getBytes(UTF8);
        byteSequence.add(new Integer(className.length).byteValue());
        for (byte b : className) {
            byteSequence.add(b);
        }
    }

    //исследуем наше дерево объектов(граф)
    private void exploreGraph(Object instance, ArrayList<Byte> byteSequence) throws IllegalAccessException, UnsupportedEncodingException {
        fill(instance, byteSequence);

        for (Field field : instance.getClass().getDeclaredFields()) {
            ///проще способа не нашел - определяем является ли поле классом или примитивом. Метод isPrimitive не подходит
            if (field.getType().getName().startsWith("ru")) {
                //если является - повторяем процедуру
                exploreGraph(field.get(instance), byteSequence);
            } else {
                Object obj = field.get(instance);
                int length = String.valueOf(obj).getBytes(UTF8).length;
                byteSequence.add(new Integer(length).byteValue());
                for (byte b : obj.toString().getBytes(UTF8)) {
                    byteSequence.add(b);
                }
            }
        }
    }

    //пишем в файл нашу последовательность байт
    private void writeToFile(ArrayList<Byte> byteSequence) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(FILE_PATH);
        for (Byte b : byteSequence) {
            fileOutputStream.write(b);
        }
        fileOutputStream.close();
    }

    /**
     * @param byteIterator индекс массива
     * @param byteArray массив байт
     * @param instance корневой элемент поддерева
     * @return Возвращаем объект и итератор(нужен, чтобы продолжить поиск по оставшимся элементам)
     */
    private Result buildTree(int byteIterator, byte[] byteArray, Class instance) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        //LinkedHashMap иначе сбивается порядок
        Map<String, Object> valuesForFields = new LinkedHashMap<>();
        for (Field field : instance.getDeclaredFields()) {
            if (field.getType().getName().startsWith("ru")) {
                int length = byteArray[++byteIterator];
                char[] chars = new char[length];
                for (int i = 0; i < length; i++) {
                    chars[i] = (char) byteArray[++byteIterator];
                }
                Class<?> clazz = Class.forName(String.valueOf(chars));
                Constructor constructor = clazz.getConstructor(new Class[]{Map.class});
                Result res = buildTree(byteIterator, byteArray, clazz);
                Object obj = constructor.newInstance(res.getMap());
                byteIterator = res.getByteIterator();
                valuesForFields.put(field.getName(), obj);
            } else {
                int length = byteArray[++byteIterator];
                char[] chars = new char[length];
                for (int i = 0; i < length; i++) {
                    chars[i] = (char) byteArray[++byteIterator];
                }
                switch (field.getType().getName()) {
                    case ("int"):
                        String s1 = new String(chars);
                        int arg1 = Integer.parseInt(s1);
                        valuesForFields.put(field.getName(), arg1);
                        break;
                    case ("boolean"):
                        String s3 = new String(chars);
                        boolean arg4 = Boolean.parseBoolean(s3);
                        valuesForFields.put(field.getName(), arg4);
                        break;
                    case ("java.lang.String"):
                        valuesForFields.put(field.getName(), new String(chars));
                        break;
                }
            }
        }
        Result result = new Result(valuesForFields, byteIterator);
        return result;
    }
}
