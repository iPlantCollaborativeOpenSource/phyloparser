package org.iplantc.phyloparser.model.statement;

import org.iplantc.phyloparser.model.Tree;

public class TreeStatement implements Statement {
	private Tree tree;

	public TreeStatement(Tree newTree) {
		this.tree = newTree;
	}

	public void setTree(Tree tree) {
		this.tree = tree;
	}

	public Tree getTree() {
		return tree;
	}
}
