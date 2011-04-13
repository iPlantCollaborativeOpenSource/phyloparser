package org.iplantc.phyloparser.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Matrix {
	public static final int UNKNOWN = 0;
	public static final int CONTINUOUS = 1;

	private int dataType;
	private List<String> characterLabels = new ArrayList<String>();
	private Map<String,List<Object>> characterMatrix = new HashMap<String,List<Object>>();
	private List<String> taxaLabels = new ArrayList<String>();

	public int getDataType() {
		return dataType;
	}
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	public List<String> getCharacterLabels() {
		return characterLabels;
	}
	public void setCharacterLabels(List<String> characterLabels) {
		this.characterLabels = characterLabels;
	}
	public Map<String, List<Object>> getCharacterMatrix() {
		return characterMatrix;
	}
	public void setCharacterMatrix(Map<String, List<Object>> characterMatrix) {
		this.characterMatrix = characterMatrix;
	}
	public List<String> getTaxaLabels() {
		return taxaLabels;
	}
	public void setTaxaLabels(List<String> taxaLabels) {
		this.taxaLabels = taxaLabels;
	}
	
}
