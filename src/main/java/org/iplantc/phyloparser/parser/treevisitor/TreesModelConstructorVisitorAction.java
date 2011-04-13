package org.iplantc.phyloparser.parser.treevisitor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.TreeVisitorAction;
import org.iplantc.phyloparser.model.Node;
import org.iplantc.phyloparser.model.Tree;
import org.iplantc.phyloparser.model.statement.Comment;
import org.iplantc.phyloparser.model.statement.Statement;
import org.iplantc.phyloparser.model.statement.TreeStatement;
import org.iplantc.phyloparser.model.statement.UnrecognizedStatement;
import org.iplantc.phyloparser.parser.antlr.NexusTreesParser;

public class TreesModelConstructorVisitorAction implements TreeVisitorAction {
	private int currentTreeRooted;
	private Node root;
	private String currentTreeName;
	private Stack<Node> pendingStack = new Stack<Node>();
	private int state = IN_TREES;
	private Map<String, String> translateMap = new HashMap<String, String>();
	private CommonTokenStream tokens;
	private List<Statement> statements = new LinkedList<Statement>();
	
	private static final int IN_TREES = 1;
	private static final int IN_TREES_TREE = 2;
	private static final int IN_TREES_TRANSLATE = 3;
	
	public Object post(Object arg0) {
		CommonTree tree = (CommonTree)arg0;
		
		switch (tree.getType()) {
		case NexusTreesParser.ANON:
		case NexusTreesParser.WORD:
		case NexusTreesParser.QUOTED_WORD:
		case NexusTreesParser.SINGLE_QUOTED_WORD:
			if (state == IN_TREES_TREE) {
				Node currentNode = pendingStack.pop();
				if (pendingStack.size() > 0) {
					Node parent = pendingStack.peek();
					parent.getChildren().add(currentNode);
					currentNode.setParent(parent);
				}
			}
			break;
		case NexusTreesParser.PTREE:
			Tree newTree = new Tree(root);
			newTree.setName(currentTreeName);
			newTree.setRootType(currentTreeRooted);
			statements.add(new TreeStatement(newTree));
			root = null;
			break;
		case NexusTreesParser.TREE:
		case NexusTreesParser.TRANSLATE:
			state = IN_TREES;
			break;
		}
		return arg0;
	}

	public Object pre(Object arg0) {
		CommonTree tree = (CommonTree)arg0;
		Node node;
		
		switch (tree.getType()) {
		case NexusTreesParser.LENGTH:
			pendingStack.peek().setBranchLength(Double.valueOf(tree.getChild(0).getText()));
			break;
		case NexusTreesParser.PAIR:
			if (state == IN_TREES_TRANSLATE) {
				translateMap.put(tree.getChild(0).getText(), tree.getChild(1).getText());				
			}
			break;
		case NexusTreesParser.ANON:
			node = new Node();
			if (root == null) {
				root = node;
			}
			pendingStack.push(node);
			break;
		case NexusTreesParser.FLOAT:
		case NexusTreesParser.PINTEGER:
			break;
		case NexusTreesParser.PTREE:
			currentTreeRooted = Tree.UNKNOWN_ROOTED;
			break;
		case NexusTreesParser.ROOTED:
			currentTreeRooted = Tree.ROOTED;
			break;
		case NexusTreesParser.UNROOTED:
			currentTreeRooted = Tree.UNROOTED;
			break;
		case NexusTreesParser.SINGLE_QUOTED_WORD:
			if (state == IN_TREES_TREE) {
				node = new Node();
				if (root == null) {
					root = node;
				}
				String text = tree.getText();
				text = text.substring(1, text.length() - 1);
				text = text.replace("''", "'");
				node.setName(text);
				pendingStack.push(node);
			}
			break;
		case NexusTreesParser.QUOTED_WORD:
			if (state == IN_TREES_TREE) {
				node = new Node();
				if (root == null) {
					root = node;
				}
				String text = tree.getText();
				text = text.substring(1, text.length() - 1);
				node.setName(text);
				pendingStack.push(node);
			}
			break;
		case NexusTreesParser.WORD:
			if (state == IN_TREES_TREE) {
				node = new Node();
				if (root == null) {
					root = node;
				}
				String text = tree.getText();
				if (translateMap.get(text) != null) {
					text = translateMap.get(text);
				}
				node.setName(text);			
				pendingStack.push(node);
			}
			break;
		case NexusTreesParser.TREE:
			state = IN_TREES_TREE;
			break;
		case NexusTreesParser.TRANSLATE:
			state = IN_TREES_TRANSLATE;
			break;
		case NexusTreesParser.TREENAME:
			currentTreeName = stripQuotes(tree.getText());
			break;
		case NexusTreesParser.UNRECOGNIZED:
			getStatements().add(new UnrecognizedStatement(tokens.toString(tree.getTokenStartIndex(), tree.getTokenStopIndex())));
			break;
		case NexusTreesParser.COMMENT:
			getStatements().add(new Comment(tree.getText().substring(1, tree.getText().length() - 1)));
			break;
		}
		return tree;
	}

	private String stripQuotes(String quotedString) {
		if (quotedString.startsWith("\"")) {
			return quotedString.substring(1, quotedString.length() - 1);
		}
		if (quotedString.startsWith("'")) {
			return quotedString.substring(1, quotedString.length() - 1).replace("''", "'");
		}
		return quotedString;
	}

	public void setTokens(CommonTokenStream tokens) {
		this.tokens = tokens;
	}

	public List<Statement> getStatements() {
		return statements;
	}
}
