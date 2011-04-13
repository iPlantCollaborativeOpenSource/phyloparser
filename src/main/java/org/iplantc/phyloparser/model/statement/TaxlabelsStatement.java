package org.iplantc.phyloparser.model.statement;

import java.util.LinkedList;
import java.util.List;


public class TaxlabelsStatement implements Statement {

	private List<String> taxaLabels = new LinkedList<String>();

	public void setTaxaLabels(List<String> taxaLabels) {
		this.taxaLabels = taxaLabels;
	}

	public List<String> getTaxaLabels() {
		return taxaLabels;
	}

}
