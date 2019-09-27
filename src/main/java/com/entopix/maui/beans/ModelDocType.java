package com.entopix.maui.beans;

public enum ModelDocType {
	
	FULLTEXTS("fulltexts"),
	ABSTRACTS("abstracts");
	
	String name;
	
	ModelDocType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
