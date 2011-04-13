package org.iplantc.phyloparser.model.block;

import java.util.Collections;
import java.util.List;

import org.iplantc.phyloparser.model.statement.DimensionsTaxaStatement;
import org.iplantc.phyloparser.model.statement.TaxlabelsStatement;

public class TaxaBlock extends AbstractRecognizedBlock {
	public String getType() {
		return "TAXA";
	}

	public List<String> getTaxaLabels() {
		TaxlabelsStatement statement = (TaxlabelsStatement)findStatementWithType(TaxlabelsStatement.class);
		if (statement == null) {
			DimensionsTaxaStatement ntaxStatement = (DimensionsTaxaStatement)findStatementWithType(DimensionsTaxaStatement.class);
			if (ntaxStatement == null) {
				return Collections.emptyList();				
			} else {
				return ntaxStatement.getTaxaLabels();
			}
		} else {
			return statement.getTaxaLabels();			
		}
	}

}
