package ru.menkin.ea.model;

import java.lang.reflect.*;
import java.util.*;

public class Worker {
	public String name;
	public int salary;
	public boolean highEducation;
	public Flat flat;

	public Worker(Map<String, Object> data) throws Exception {
		for (Field field : Worker.class.getDeclaredFields()) {
			field.set(this, data.get(field.getName()));
		}
	}

	public Worker(String name, int salary, boolean highEducation, Flat flat) {
		this.name = name;
		this.salary = salary;
		this.highEducation = highEducation;
		this.flat = flat;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSalary() {
		return salary;
	}
	public void setSalary(int salary) {
		this.salary = salary;
	}
	public boolean isHighEducation() {
		return highEducation;
	}
	public void setHighEducation(boolean highEducation) {
		this.highEducation = highEducation;
	}
	public Flat getFlat() {
		return flat;
	}
	public void setFlat(Flat flat) {
		this.flat = flat;
	}
}
