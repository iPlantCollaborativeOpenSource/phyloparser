package org.iplantc.phyloparser.model.block;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.iplantc.phyloparser.model.statement.DimensionsCharStatement;
import org.iplantc.phyloparser.model.statement.FormatStatement;
import org.iplantc.phyloparser.model.statement.MatrixStatement;

public class CharactersBlock extends AbstractRecognizedBlock {
	public static final int UNKNOWN = 0;
	public static final int CONTINUOUS = 1;

	public String getType() {
		return "CHARACTERS";
	}

	public List<String> getCharacterLabels() {
		DimensionsCharStatement dimensionsCharStmt = findStatementWithType(DimensionsCharStatement.class);
		if (dimensionsCharStmt != null) {
			return dimensionsCharStmt.getCharacterLabels();
		} else {
			return Collections.emptyList();
		}
	}

	public int getDataType() {
		FormatStatement formatStmt = findStatementWithType(FormatStatement.class);
		if (formatStmt != null) {
			return formatStmt.getDataType();
		} else {
			return UNKNOWN;
		}
	}

	public Map<String,List<Object>> getCharacterMatrix() {
		MatrixStatement matrixStmt = findStatementWithType(MatrixStatement.class);
		if (matrixStmt != null) {
			return matrixStmt.getCharacterMatrix();
		} else {
			return Collections.emptyMap();
		}
	}

	public List<String> getTaxaLabels() {
		MatrixStatement matrixStmt = findStatementWithType(MatrixStatement.class);
		if (matrixStmt != null) {
			return matrixStmt.getTaxaLabels();
		} else {
			return Collections.emptyList();
		}
	}

}
