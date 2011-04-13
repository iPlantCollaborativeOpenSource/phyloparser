package org.iplantc.phyloparser.model.block;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.phyloparser.model.Tree;
import org.iplantc.phyloparser.model.statement.TreeStatement;

public class TreesBlock extends AbstractRecognizedBlock {

	public String getType() {
		return "TREES";
	}

	public List<Tree> getTrees() {
		ArrayList<Tree> trees = new ArrayList<Tree>();
		for (TreeStatement treeStmt : findAllStatementsWithType(TreeStatement.class)) {
			trees.add(treeStmt.getTree());
		}
		return trees;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[trees=");
		builder.append(getTrees());
		builder.append("]");
		return builder.toString();
	}
}
