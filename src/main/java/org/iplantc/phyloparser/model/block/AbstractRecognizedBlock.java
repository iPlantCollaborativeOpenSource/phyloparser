package org.iplantc.phyloparser.model.block;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.iplantc.phyloparser.model.statement.Statement;
import org.iplantc.phyloparser.model.statement.UnrecognizedStatement;

public abstract class AbstractRecognizedBlock implements RecognizedBlock {

	protected List<Statement> statements = new LinkedList<Statement>();
	
	public List<Statement> getStatements() {
		return statements;
	}

	public List<UnrecognizedStatement> getUnrecognizedStatements() {
		return findAllStatementsWithType(UnrecognizedStatement.class);
	}

	public void setStatements(List<Statement> statements) {
		this.statements = statements;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Statement> T findStatementWithType(Class<T> type) {
		for (Statement stmt: statements) {
			if (type.isAssignableFrom(stmt.getClass())) {
				return (T)stmt;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Statement> List<T> findAllStatementsWithType(Class<T> type) {
		ArrayList<T> result = new ArrayList<T>(); 
		for (Statement stmt: statements) {
			if (type.isAssignableFrom(stmt.getClass())) {
				result.add((T)stmt);
			}
		}
		return result;
	}

}
