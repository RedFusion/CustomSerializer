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
                System.out.println(addTab(index) + field.getName() + ": " + field.get(obj).toString());
            }
        }
    }

	public void deserialize(String filePath){
        byte[] byteArray = readFromFile(filePath);
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

    // prepare the (fill) byte array to write to the file
    private void fill(Object instance, ArrayList<Byte> byteSequence) throws UnsupportedEncodingException {
        byte[] className = instance.getClass().getCanonicalName().getBytes(UTF8);
        byteSequence.add(new Integer(className.length).byteValue());
        for (byte b : className) {
            byteSequence.add(b);
        }
    }

    // examine our object tree (graph)
    private void exploreGraph(Object instance, ArrayList<Byte> byteSequence) throws IllegalAccessException, UnsupportedEncodingException {
        fill(instance, byteSequence);

        for (Field field : instance.getClass().getDeclaredFields()) {
            //easier ways found - to determine whether the field is a class or primitive. The method is not suitable isPrimitive
            if (field.getType().getName().startsWith("ru")) {
                // If it is - repeat the procedure
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

    /**
     * @param byteIterator array index
     * @param byteArray byte array
     * @param instance the root element of the subtree
     * @return the object and an iterator (need to continue the search for the remaining elements)
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
                        valuesForFields.put(field.getName(), Integer.parseInt(new String(chars)));
                        break;
                    case ("boolean"):
                        valuesForFields.put(field.getName(), Boolean.parseBoolean(new String(chars)));
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

    // write to file our sequence of bytes
    private void writeToFile(ArrayList<Byte> byteSequence) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(FILE_PATH);
        for (Byte b : byteSequence) {
            fileOutputStream.write(b);
        }
        fileOutputStream.close();
    }

    private byte[] readFromFile(String filePath) {
        File file = new File(filePath);
        byte[] byteArray = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(byteArray);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    // For clarity indents to show the entire structure (ala Python)
    private String addTab(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append("\t");
        }
        return sb.toString();
    }
}
