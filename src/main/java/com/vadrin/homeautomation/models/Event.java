package com.vadrin.homeautomation.models;

import java.util.Map;

public class Event {

	private String name;
	private Map<String, String> info;

	public Event(String name) {
		super();
		this.name = name;
	}

	public Event(String name, Map<String, String> info) {
		super();
		this.name = name;
		this.info = info;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Event() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Map<String, String> getInfo() {
		return info;
	}

	public void setInfo(Map<String, String> info) {
		this.info = info;
	}

}
