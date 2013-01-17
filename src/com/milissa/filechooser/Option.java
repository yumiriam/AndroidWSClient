package com.milissa.filechooser;

public class Option implements Comparable<Option> {

	private String name;
	private String data;
	private String path;

	public Option(String name,String data,String path) {
		this.name = name;
		this.data = data;
		this.path = path;
	}
	
	public String getName() {
		return name;
	}
	
	public String getData() {
		return data;
	}
	
	public String getPath() {
		return path;
	}
	
	@Override
	public int compareTo(Option o) {
		if(this.name != null)
			return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
		else
			throw new IllegalArgumentException();
	}

}
