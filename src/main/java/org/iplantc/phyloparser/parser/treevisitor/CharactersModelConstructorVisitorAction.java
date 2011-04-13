package org.iplantc.phyloparser.parser.treevisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.TreeVisitorAction;
import org.iplantc.phyloparser.model.block.CharactersBlock;
import org.iplantc.phyloparser.model.statement.CharStateLabelsStatement;
import org.iplantc.phyloparser.model.statement.Comment;
import org.iplantc.phyloparser.model.statement.DimensionsCharStatement;
import org.iplantc.phyloparser.model.statement.FormatStatement;
import org.iplantc.phyloparser.model.statement.MatrixStatement;
import org.iplantc.phyloparser.model.statement.Statement;
import org.iplantc.phyloparser.model.statement.UnrecognizedStatement;
import org.iplantc.phyloparser.parser.antlr.NexusCharactersParser;

public class CharactersModelConstructorVisitorAction implements
		TreeVisitorAction {
	private int state = IGNORE;
	private int dataType = CharactersBlock.UNKNOWN;
	private String currentTaxa;
	private List<Object> currentTaxaCharacters;
	private List<String> charLabels = new ArrayList<String>();
	private boolean charlabelsSeen = false;
	private boolean matrixSeen = false;
	private Map<String, List<Object>> characterMatrix = new HashMap<String, List<Object>>();
	private List<String> taxaLabels = new ArrayList<String>();
	private CommonTokenStream tokens;
	private List<Statement> statements = new LinkedList<Statement>();
	
	private static final int IGNORE = 0;
	private static final int IN_DATATYPE = 1;
	private static final int IN_MATRIX = 2;

	public Object post(Object t) {
		CommonTree tree = (CommonTree)t;
		
		switch (tree.getType()) {
		case NexusCharactersParser.MATRIX:
			if (currentTaxa != null) {
				characterMatrix.put(currentTaxa, currentTaxaCharacters);
			}
			if (!matrixSeen) {
				statements.add(new MatrixStatement(characterMatrix, taxaLabels));
				matrixSeen = true;
			}
			// fall through
		case NexusCharactersParser.DATATYPE:
			state = IGNORE;
			break;
		case NexusCharactersParser.FORMAT:
			statements.add(new FormatStatement(dataType));
			break;
		}
		
		return t;
	}

	public Object pre(Object t) {
		CommonTree tree = (CommonTree)t;

		String text = null;
		
		switch (tree.getType()) {
		case NexusCharactersParser.DIMENSIONS:
			statements.add(new DimensionsCharStatement(charLabels));
			break;
		case NexusCharactersParser.DATATYPE:
			state = IN_DATATYPE;
			break;
		case NexusCharactersParser.MATRIX:
			state = IN_MATRIX;
			break;
		case NexusCharactersParser.NCHAR:
			setNchar(Integer.valueOf(tree.getText()));
			break;
		case NexusCharactersParser.FLOAT:
			if (state == IN_MATRIX) {
				currentTaxaCharacters.add(Double.valueOf(tree.getText()));
			}
			break;
		case NexusCharactersParser.QUESTION:
			if (state == IN_MATRIX) {
				currentTaxaCharacters.add(tree.getText());
			}
			break;
		case NexusCharactersParser.CHARSTATELABELS:
		case NexusCharactersParser.CHARLABELS:
			if (!charlabelsSeen) {
				statements.add(new CharStateLabelsStatement(charLabels));
				charlabelsSeen = true;
			}
			break;
		case NexusCharactersParser.CHARLABEL:
			if (!"_".equals(tree.getChild(1).getText())) {
				int index = Integer.valueOf(tree.getChild(0).getText()) - 1;
				charLabels.set(index, tree.getChild(1).getText());	
			}
			break;
		case NexusCharactersParser.SINGLE_QUOTED_WORD:
			text = tree.getText();
			text = text.substring(1, text.length() - 1);
			text = text.replace("''", "'");
			// fall through
		case NexusCharactersParser.QUOTED_WORD:
			if (text == null) {
				text = tree.getText();
				text = text.substring(1, text.length() - 1);
			}
			// fall through
		case NexusCharactersParser.WORD:
			if (text == null) {
				text = tree.getText();
			}
			if (state == IN_DATATYPE) {
				if ("continuous".equalsIgnoreCase(text)) {
					dataType = CharactersBlock.CONTINUOUS;
				}
			}
			if (state == IN_MATRIX) {
				if (currentTaxa != null) {
					characterMatrix.put(currentTaxa, currentTaxaCharacters);
				}
				currentTaxa = tree.getText();
				currentTaxaCharacters = characterMatrix.get(currentTaxa);
				if (currentTaxaCharacters == null) {
					currentTaxaCharacters = new LinkedList<Object>();
					taxaLabels.add(currentTaxa);
				}
			}
			break;
		case NexusCharactersParser.UNRECOGNIZED:
			statements.add(new UnrecognizedStatement(tokens.toString(tree.getTokenStartIndex(), tree.getTokenStopIndex())));
			break;
		case NexusCharactersParser.COMMENT:
			getStatements().add(new Comment(tree.getText().substring(1, tree.getText().length() - 1)));
			break;
		}
		
		return t;
	}

	private void setNchar(int nchar) {
		for (int i = 0; i < nchar; i++) {
			charLabels.add(null);
		}
	}

	public void setTokens(CommonTokenStream tokens) {
		this.tokens = tokens;
	}

	public List<Statement> getStatements() {
		return statements;
	}
}
