package org.iplantc.phyloparser.model.statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatrixStatement implements Statement {
	private Map<String,List<Object>> characterMatrix = new HashMap<String, List<Object>>();
	private List<String> taxaLabels = new ArrayList<String>();
	
	public MatrixStatement(Map<String, List<Object>> characterMatrix) {
		this.characterMatrix = characterMatrix;
		taxaLabels.addAll(characterMatrix.keySet());
	}

	public MatrixStatement(Map<String, List<Object>> characterMatrix, List<String> taxaLabels) {
		this.characterMatrix = characterMatrix;
		this.taxaLabels = taxaLabels;
	}

	public void setCharacterMatrix(Map<String,List<Object>> characterMatrix) {
		this.characterMatrix = characterMatrix;
	}

	public Map<String,List<Object>> getCharacterMatrix() {
		return characterMatrix;
	}

	public void setTaxaLabels(List<String> taxaLabels) {
		this.taxaLabels = taxaLabels;
	}

	public List<String> getTaxaLabels() {
		return taxaLabels;
	}

}
