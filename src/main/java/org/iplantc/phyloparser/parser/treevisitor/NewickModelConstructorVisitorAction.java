package org.iplantc.phyloparser.parser.treevisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.TreeVisitorAction;
import org.iplantc.phyloparser.model.Annotation;
import org.iplantc.phyloparser.model.Node;
import org.iplantc.phyloparser.parser.antlr.NewickParser;

public class NewickModelConstructorVisitorAction implements TreeVisitorAction {

	private List<Node> roots = new LinkedList<Node>();
	private Node root;
	private Stack<Node> pendingStack = new Stack<Node>();
	
	public Object post(Object arg0) {
		CommonTree tree = (CommonTree)arg0;
		
		switch (tree.getType()) {
		case NewickParser.ANON:
		case NewickParser.ID:
		case NewickParser.QUOTED_ID:
			Node currentNode = pendingStack.pop();
			if (pendingStack.size() > 0) {
				Node parent = pendingStack.peek();
				parent.getChildren().add(currentNode);
				currentNode.setParent(parent);
			}
			break;
		case NewickParser.TREE:
			roots.add(root);
			root = null;
			break;
		}
		return arg0;
	}

	public Object pre(Object arg0) {
		CommonTree tree = (CommonTree)arg0;
		Node node;
		
		switch (tree.getType()) {
		case NewickParser.LENGTH:
			pendingStack.peek().setBranchLength(Double.valueOf(tree.getChild(0).getText()));
			break;
		case NewickParser.NHX_ANNOTATION:
			String text = tree.getText();
			pendingStack.peek().getAnnotations().add(new Annotation(text.substring(1, text.length() - 1)));
			break;
		case NewickParser.ANON:
			node = new Node();
			if (root == null) {
				root = node;
			}
			pendingStack.push(node);
			break;
		case NewickParser.FLOAT:
			break;
		case NewickParser.QUOTED_ID:
			node = new Node();
			if (root == null) {
				root = node;
			}
			text = tree.getText();
			text = text.substring(1, text.length() - 1);
			text = text.replace("''", "'");
			node.setName(text);			
			pendingStack.push(node);
			break;
		case NewickParser.ID:
			node = new Node();
			if (root == null) {
				root = node;
			}
			node.setName(tree.getText());			
			pendingStack.push(node);
			break;
		case NewickParser.TREE:
			break;
		}
		return tree;
	}

	public List<Node> getRoots() {
		return roots;
	}

}
