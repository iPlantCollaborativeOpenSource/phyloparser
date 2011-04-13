package org.iplantc.phyloparser.model;

import java.util.Collection;
import java.util.Collections;

public class Tree {
	private Node root;
	private String name;
	private int rootType;
	
	public static final int UNKNOWN_ROOTED = 0;
	public static final int ROOTED = 1;
	public static final int UNROOTED = 2;

	public Tree(Node root) {
		setRoot(root);
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	public Node getRoot() {
		return root;
	}
	
	public Collection<Node> getNodes() {
		if (root == null) {
			return Collections.emptySet();
		}
		return root.getAllNodes();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name == null ? "" : name + ": " + root.toString();
	}

	public void setRootType(int rootType) {
		this.rootType = rootType;
	}

	public int getRootType() {
		return rootType;
	}
}
