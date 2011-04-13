package org.iplantc.phyloparser.model.statement;

public class FormatStatement implements Statement {
	public static final int UNKNOWN = 0;
	public static final int CONTINUOUS = 1;

	private int dataType;

	public FormatStatement(int dataType) {
		this.dataType = dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public int getDataType() {
		return dataType;
	}
	
}
