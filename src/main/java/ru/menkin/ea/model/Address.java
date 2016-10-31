package ru.menkin.ea.model;

import java.lang.reflect.*;
import java.util.*;

public class Address {
	public String location;
	public int zipCode;

	public Address(Map<String, Object> data) throws Exception {
		for (Field field : Address.class.getDeclaredFields()) {
			field.set(this, data.get(field.getName()));
		}
	}

	public Address(String location, int zipCode) {
		this.location = location;
		this.zipCode = zipCode;
	}

	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public int getZipCode() {
		return zipCode;
	}
	public void setZipCode(int zipCode) {
		this.zipCode = zipCode;
	}
}
