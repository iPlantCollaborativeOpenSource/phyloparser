package org.iplantc.phyloparser.model.block;


public class UnknownBlock implements Block {
	private String type;
	private String content;
	
	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

}
