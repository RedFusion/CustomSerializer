package ru.menkin.ea.model;

import java.lang.reflect.*;
import java.util.*;

public class Office {
	public Worker worker;
	public int costPerMonth;
	public Address address;

	public Office(Worker worker, int costPerMonth, Address address) {
		this.worker = worker;
		this.address = address;
		this.costPerMonth = costPerMonth;
	}

	public Office(Map<String, Object> data) throws Exception {
		for (Field field : Office.class.getDeclaredFields()) {
			field.set(this, data.get(field.getName()));
		}
	}

	public Worker getWorker() {
		return worker;
	}
	public void setWorker(Worker worker) {
		this.worker = worker;
	}
	
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	
	public int getCostPerMonth() {
		return costPerMonth;
	}
	public void setCostPerMonth(int costPerMonth) {
		this.costPerMonth = costPerMonth;
	}
}
