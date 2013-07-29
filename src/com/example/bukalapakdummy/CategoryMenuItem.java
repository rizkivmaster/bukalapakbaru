package com.example.bukalapakdummy;

public class CategoryMenuItem {
	private String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	private String name;
	public CategoryMenuItem(String i, String n) {
		id = i;
		name = n;
	}
	
	
}
