package ru.menkin.ea;

import ru.menkin.ea.model.*;
import ru.menkin.ea.serializer.CustomSerializer;
import static ru.menkin.ea.serializer.CustomSerializer.FILE_PATH;

public class Main {
	public static void main(String[] args) {
		// create a nested structure of objects
		Flat flat = new Flat(4);
		Worker worker = new Worker("Victor", 200000, false, flat);
		Address address = new Address("Ekaterinburg", 615208);
		Office office = new Office(worker, 1500000, address);

		CustomSerializer serializer = new CustomSerializer();
		serializer.serialize(office);

		serializer.deserialize(FILE_PATH);
	}
}
