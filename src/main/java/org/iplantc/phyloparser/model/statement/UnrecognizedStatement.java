package org.iplantc.phyloparser.model.statement;

public class UnrecognizedStatement implements Statement {

	private String content;
	
	public UnrecognizedStatement(String stmt) {
		this.setContent(stmt);
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

}
