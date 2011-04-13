package org.iplantc.phyloparser.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iplantc.phyloparser.exception.GeneratorException;
import org.iplantc.phyloparser.model.Annotation;
import org.iplantc.phyloparser.model.FileData;
import org.iplantc.phyloparser.model.Node;
import org.iplantc.phyloparser.model.Tree;
import org.iplantc.phyloparser.model.block.CharactersBlock;
import org.iplantc.phyloparser.model.block.TreesBlock;
import org.iplantc.phyloparser.model.block.UnknownBlock;
import org.iplantc.phyloparser.model.statement.CharStateLabelsStatement;
import org.iplantc.phyloparser.model.statement.DimensionsCharStatement;
import org.iplantc.phyloparser.model.statement.FormatStatement;
import org.iplantc.phyloparser.model.statement.MatrixStatement;
import org.iplantc.phyloparser.model.statement.TreeStatement;

import junit.framework.TestCase;

public class TestNewickGenerator extends TestCase {
	public void testEmptyFile() throws Throwable {
		FileData fd = new FileData();
		NewickGenerator ng = new NewickGenerator();
		
		String newickFile = ng.generate(fd);
		
		assertEquals("", newickFile);
	}

	public void testOneUnknownBlock() throws Throwable {
		FileData fd = new FileData();
		UnknownBlock unkBlock = new UnknownBlock();
		unkBlock.setType("MYBLOCK");
		unkBlock.setContent("\n  TITLE This is my block;\n  CONTENT And this is the content;\n");
		fd.getBlocks().add(unkBlock);
		
		NewickGenerator ng = new NewickGenerator();
		String newickFile = ng.generate(fd);
		
		assertEquals("", newickFile);		
	}
	
	public void testEmptyTreesBlock() throws Throwable {
		FileData fd = new FileData();
		TreesBlock treesBlock = new TreesBlock();
		fd.getBlocks().add(treesBlock);
		
		NewickGenerator ng = new NewickGenerator();
		String newickFile = ng.generate(fd);
		
		assertEquals("", newickFile);
	}

	public void testTreesBlockWithSingleTree() throws Throwable {
		FileData fd = new FileData();
		Node rootNode = new Node();
		Node node1 = new Node("foo", null);
		Node node2 = new Node("bar", null);
		node1.setParent(rootNode);
		node2.setParent(rootNode);
		rootNode.getChildren().add(node1);
		rootNode.getChildren().add(node2);
		Tree tree = new Tree(rootNode);
		tree.setName("myTree");
		TreesBlock tb = new TreesBlock();
		fd.getBlocks().add(tb);
		tb.getStatements().add(new TreeStatement(tree));
		
		NewickGenerator ng = new NewickGenerator();
		String newickFile = ng.generate(fd);
		
		assertEquals("(foo,bar);\n", newickFile);		
	}

	public void testTreesBlockWithSingleTreeAndQuotedLabels() throws Throwable {
		FileData fd = new FileData();
		Node rootNode = new Node();
		Node node1 = new Node("foo 1", null);
		Node node2 = new Node("bar 2", null);
		node1.setParent(rootNode);
		node2.setParent(rootNode);
		rootNode.getChildren().add(node1);
		rootNode.getChildren().add(node2);
		Tree tree = new Tree(rootNode);
		tree.setName("my Tree");
		TreesBlock tb = new TreesBlock();
		fd.getBlocks().add(tb);
		tb.getStatements().add(new TreeStatement(tree));
		
		NewickGenerator ng = new NewickGenerator();
		String newickFile = ng.generate(fd);
		
		assertEquals("('foo 1','bar 2');\n", newickFile);		
	}
	
	public void testTreesBlockWithThreeTrees() throws Throwable {
		FileData fd = new FileData();
		Node rootNode = new Node();
		Node node1 = new Node("foo", null);
		Node node2 = new Node("bar", null);
		node1.setParent(rootNode);
		node2.setParent(rootNode);
		rootNode.getChildren().add(node1);
		rootNode.getChildren().add(node2);
		Tree tree = new Tree(rootNode);
		tree.setName("myTree");
		TreesBlock tb = new TreesBlock();
		fd.getBlocks().add(tb);
		tb.getStatements().add(new TreeStatement(tree));

		rootNode = new Node();
		node1 = new Node("baz", null);
		node2 = new Node("whump", null);
		node1.setParent(rootNode);
		node2.setParent(rootNode);
		rootNode.getChildren().add(node1);
		rootNode.getChildren().add(node2);
		tree = new Tree(rootNode);
		tree.setName("myTree2");
		tb.getStatements().add(new TreeStatement(tree));

		rootNode = new Node();
		node1 = new Node("ding", 0.3);
		node2 = new Node("dong", 0.4);
		node1.setParent(rootNode);
		node2.setParent(rootNode);
		rootNode.getChildren().add(node1);
		rootNode.getChildren().add(node2);
		tree = new Tree(rootNode);
		tree.setName("myTree3");
		tb.getStatements().add(new TreeStatement(tree));

		NewickGenerator ng = new NewickGenerator();
		String newickFile = ng.generate(fd);
		
		assertEquals("(foo,bar);\n(baz,whump);\n(ding:0.3,dong:0.4);\n", newickFile);		
	}

	public void testThreeTreesBlocks() throws Throwable {
		FileData fd = new FileData();
		Node rootNode = new Node();
		Node node1 = new Node("foo", null);
		Node node2 = new Node("bar", null);
		node1.setParent(rootNode);
		node2.setParent(rootNode);
		rootNode.getChildren().add(node1);
		rootNode.getChildren().add(node2);
		Tree tree = new Tree(rootNode);
		tree.setName("myTree");
		TreesBlock tb = new TreesBlock();
		fd.getBlocks().add(tb);
		tb.getStatements().add(new TreeStatement(tree));

		rootNode = new Node();
		node1 = new Node("baz", null);
		node2 = new Node("whump", null);
		node1.setParent(rootNode);
		node2.setParent(rootNode);
		rootNode.getChildren().add(node1);
		rootNode.getChildren().add(node2);
		tree = new Tree(rootNode);
		tree.setName("myTree2");
		tb = new TreesBlock();
		fd.getBlocks().add(tb);
		tb.getStatements().add(new TreeStatement(tree));

		rootNode = new Node();
		node1 = new Node("ding", 0.3);
		node2 = new Node("dong", 0.4);
		node1.setParent(rootNode);
		node2.setParent(rootNode);
		rootNode.getChildren().add(node1);
		rootNode.getChildren().add(node2);
		tree = new Tree(rootNode);
		tree.setName("myTree3");
		tb = new TreesBlock();
		fd.getBlocks().add(tb);
		tb.getStatements().add(new TreeStatement(tree));

		NewickGenerator ng = new NewickGenerator();
		String newickFile = ng.generate(fd);
		
		assertEquals("(foo,bar);\n(baz,whump);\n(ding:0.3,dong:0.4);\n", newickFile);		
	}
	
	public void testIgnoreCharactersBlock() throws GeneratorException {
		FileData fd = new FileData();
		CharactersBlock charBlock = new CharactersBlock();
		List<String> chars = new ArrayList<String>();
		chars.add("char1");
		charBlock.getStatements().add(new DimensionsCharStatement(chars));
		charBlock.getStatements().add(new FormatStatement(FormatStatement.CONTINUOUS));
		charBlock.getStatements().add(new CharStateLabelsStatement(chars));
		List<Object> taxonValues = new ArrayList<Object>();
		taxonValues.add(0.1);
		Map<String, List<Object>> taxonValueMap = new HashMap<String, List<Object>>();
		taxonValueMap.put("taxon1", taxonValues);
		charBlock.getStatements().add(new MatrixStatement(taxonValueMap));
		fd.getBlocks().add(charBlock);
		
		NewickGenerator ng = new NewickGenerator();
		String newickFile = ng.generate(fd);
		
		assertEquals("", newickFile);
	}

	public void testTreesBlockWithSingleTreeWithRootBranchLength() throws Throwable {
		FileData fd = new FileData();
		Node rootNode = new Node();
		rootNode.setBranchLength(0.427);
		Node node1 = new Node("foo", null);
		Node node2 = new Node("bar", null);
		node1.setParent(rootNode);
		node2.setParent(rootNode);
		rootNode.getChildren().add(node1);
		rootNode.getChildren().add(node2);
		Tree tree = new Tree(rootNode);
		tree.setName("myTree");
		TreesBlock tb = new TreesBlock();
		fd.getBlocks().add(tb);
		tb.getStatements().add(new TreeStatement(tree));
		
		NewickGenerator ng = new NewickGenerator();
		String newickFile = ng.generate(fd);
		
		assertEquals("(foo,bar):0.427;\n", newickFile);		
	}
	
	public void testTreeWithNhxAnnotation() throws Throwable {
		FileData fd = new FileData();
		Node rootNode = new Node();
		Node node1 = new Node("foo", null);
		Node node2 = new Node("bar", null);
		node1.setParent(rootNode);
		node2.setParent(rootNode);
		node2.getAnnotations().add(new Annotation("&&NHX:foobarbaz=1"));
		rootNode.getChildren().add(node1);
		rootNode.getChildren().add(node2);
		Tree tree = new Tree(rootNode);
		tree.setName("myTree");
		TreesBlock tb = new TreesBlock();
		fd.getBlocks().add(tb);
		tb.getStatements().add(new TreeStatement(tree));
		
		NewickGenerator ng = new NewickGenerator();
		String newickFile = ng.generate(fd);
		
		assertEquals("(foo,bar[&&NHX:foobarbaz=1]);\n", newickFile);		
	}
}
