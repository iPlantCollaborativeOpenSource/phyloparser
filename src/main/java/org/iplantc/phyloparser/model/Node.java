package org.iplantc.phyloparser.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Node {

	private String name;
	private Double branchLength;
	private List<Node> children = new LinkedList<Node>();
	private Node parent;
	private Collection<Annotation> annotations = new LinkedList<Annotation>();
	
	public Node() {
	}
	
	public Node(String name, Double branchLength) {
		setName(name);
		setBranchLength(branchLength);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Double getBranchLength() {
		return branchLength;
	}

	public void setBranchLength(Double branchLength) {
		this.branchLength = branchLength;
	}

	public List<Node> getChildren() {
		return children ;
	}
	
	public void setChildren(List<Node> children) {
		this.children = children;
	}

	public Collection<Node> getAllNodes() {
		HashSet<Node> nodeSet = new HashSet<Node>();
		for (Node child : children) {
			nodeSet.addAll(child.getAllNodes());
		}
		nodeSet.add(this);
		return nodeSet;
	}

	public Node getParent() {
		return parent;
	}
	
	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(name == null ? "" : name);
		if (children.size() > 0) {
			sb.append('(');
			for (Node child : children) {
				sb.append(child.toString());
				sb.append(',');
			}
			sb.delete(sb.length() - 1, sb.length());
			sb.append(')');
		}
		return sb.toString();
	}

	public Collection<Annotation> getAnnotations() {
		return annotations;
	}
	
	public void setAnnotations(Collection<Annotation> annotations) {
		this.annotations = annotations;
	}
}
