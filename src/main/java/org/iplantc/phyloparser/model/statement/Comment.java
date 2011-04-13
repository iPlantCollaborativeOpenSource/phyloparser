package org.iplantc.phyloparser.model.statement;

import org.iplantc.phyloparser.model.block.Block;

public class Comment implements Block, Statement {
	private String content;

	public Comment() {
	}
	
	public Comment(String content) {
		this.content = content;
	}

	public String getType() {
		return "COMMENT";
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

}
