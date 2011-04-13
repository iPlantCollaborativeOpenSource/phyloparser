package org.iplantc.phyloparser.model.block;

import java.util.List;

import org.iplantc.phyloparser.model.statement.Statement;
import org.iplantc.phyloparser.model.statement.UnrecognizedStatement;

public interface RecognizedBlock extends Block {
	public abstract List<UnrecognizedStatement> getUnrecognizedStatements();
	public abstract List<Statement> getStatements();
	public abstract <T extends Statement> T findStatementWithType(Class<T> type);
	public abstract <T extends Statement> List<T> findAllStatementsWithType(Class<T> type);
}
