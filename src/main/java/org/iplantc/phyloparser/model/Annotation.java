package org.iplantc.phyloparser.model;

public class Annotation {

	private String content;
	
	public Annotation() {
		
	}
	
	public Annotation(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
