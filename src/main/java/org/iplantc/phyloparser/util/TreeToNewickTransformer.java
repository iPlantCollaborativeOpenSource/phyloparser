package org.iplantc.phyloparser.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.iplantc.phyloparser.model.Annotation;
import org.iplantc.phyloparser.model.Node;
import org.iplantc.phyloparser.model.Tree;

public class TreeToNewickTransformer {
	public Collection<String> transform(Collection<Tree> trees) {
		List<String> newickStrings = new LinkedList<String>();
		for (Tree tree : trees) {
			newickStrings.add(transform(tree));
		}
		return newickStrings;
	}
	
	public String transform(Tree tree) {
		StringBuilder newick = new StringBuilder();
		traverse(tree.getRoot(), newick);
		return newick.toString();
	}
	
	private void traverse(Node current, StringBuilder newick) {
		if (current != null) {
			if (!current.getChildren().isEmpty()) {
				newick.append("(");
				Iterator<Node> itr = current.getChildren().iterator();
				while (itr.hasNext()) {
					Node child = itr.next();
					// recurse if the child has children
					if (!child.getChildren().isEmpty()) {
						traverse(child, newick);
					} else {
						newick.append(quote(child.getName() == null ? "" : child.getName())); 
						if (child.getBranchLength() != null) {
							newick.append(":" + child.getBranchLength());
						}
						if (child.getAnnotations() != null) {
							for (Annotation annotation : child.getAnnotations()) {
								newick.append("[" + annotation.getContent() + "]");
							}
						}
					}
					// if there are more 'daughters' to process, delimit w/ a comma
					if (itr.hasNext()) {
						newick.append(",");
					}					
				}
				newick.append(")");	
			}
			newick.append(current.getName() == null ? "" : current.getName()); 
			if (current.getBranchLength() != null) {
				newick.append(":" + current.getBranchLength());
			}
			if (current.getAnnotations() != null) {
				for (Annotation annotation : current.getAnnotations()) {
					newick.append("[" + annotation.getContent() + "]");
				}
			}
		}
	}

	private String quote(String taxonLabel) {
		if (taxonLabel.indexOf(' ') != -1) {
			return "'" + taxonLabel.replace("'", "''") + "'";
		} else {
			return taxonLabel;
		}
	}
}
