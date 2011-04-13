package org.iplantc.phyloparser.parser.treevisitor;

import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.TreeVisitorAction;
import org.iplantc.phyloparser.model.statement.Comment;
import org.iplantc.phyloparser.model.statement.DimensionsTaxaStatement;
import org.iplantc.phyloparser.model.statement.Statement;
import org.iplantc.phyloparser.model.statement.TaxlabelsStatement;
import org.iplantc.phyloparser.model.statement.UnrecognizedStatement;
import org.iplantc.phyloparser.parser.antlr.NexusTaxaParser;

public class TaxaModelConstructorVisitorAction implements TreeVisitorAction {
	private List<String> taxaLabels = new LinkedList<String>();
	private List<Statement> statements = new LinkedList<Statement>();
	boolean taxlabelsSeen = false;
	private CommonTokenStream tokens;
	
	private int state = IGNORE;
	
	private static final int IGNORE = 0;
	private static final int IN_TAXLABELS = 1;
	
	public Object post(Object t) {
		CommonTree tree = (CommonTree)t;
		
		switch (tree.getType()) {
		case NexusTaxaParser.TAXLABELS:
			state = IGNORE;
			break;
		}
		return t;
	}

	public Object pre(Object t) {
		CommonTree tree = (CommonTree)t;
		
		switch (tree.getType()) {
		case NexusTaxaParser.DIMENSIONS:
			DimensionsTaxaStatement dimensionsStmt = new DimensionsTaxaStatement();
			dimensionsStmt.setTaxaLabels(taxaLabels);
			getStatements().add(dimensionsStmt);
			break;
		case NexusTaxaParser.TAXLABELS:
			if (!taxlabelsSeen) {
				TaxlabelsStatement taxlabelsStmt = new TaxlabelsStatement();
				taxlabelsStmt.setTaxaLabels(taxaLabels);
				getStatements().add(taxlabelsStmt);
				taxlabelsSeen = true;
			}
			state = IN_TAXLABELS;
			break;
		case NexusTaxaParser.SINGLE_QUOTED_WORD:
			if (state == IN_TAXLABELS) {
				String text = tree.getText();
				text = text.substring(1, text.length() - 1);
				text = text.replace("''", "'");
				taxaLabels.add(text);
			}
			break;
		case NexusTaxaParser.QUOTED_WORD:
			if (state == IN_TAXLABELS) {
				String text = tree.getText();
				text = text.substring(1, text.length() - 1);
				taxaLabels.add(text);				
			}
			break;
		case NexusTaxaParser.WORD:
			if (state == IN_TAXLABELS) {
				taxaLabels.add(tree.getText());
			}
			break;
		case NexusTaxaParser.UNRECOGNIZED:
			getStatements().add(new UnrecognizedStatement(tokens.toString(tree.getTokenStartIndex(), tree.getTokenStopIndex())));
			break;
		case NexusTaxaParser.COMMENT:
			getStatements().add(new Comment(tree.getText().substring(1, tree.getText().length() - 1)));
			break;
		}
			
		return t;
	}

	public void setTokens(CommonTokenStream tokens) {
		this.tokens = tokens;
	}

	public List<Statement> getStatements() {
		return statements;
	}

}
