package org.iplantc.phyloparser.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iplantc.phyloparser.model.FileData;
import org.iplantc.phyloparser.model.Matrix;
import org.iplantc.phyloparser.model.Node;
import org.iplantc.phyloparser.model.SourceFormat;
import org.iplantc.phyloparser.model.Tree;
import org.iplantc.phyloparser.model.block.CharactersBlock;
import org.iplantc.phyloparser.model.block.TreesBlock;
import org.iplantc.phyloparser.model.statement.CharStateLabelsStatement;
import org.iplantc.phyloparser.model.statement.DimensionsCharStatement;
import org.iplantc.phyloparser.model.statement.FormatStatement;
import org.iplantc.phyloparser.model.statement.MatrixStatement;
import org.iplantc.phyloparser.model.statement.TreeStatement;

import junit.framework.TestCase;

public class TestFileDataBuilder extends TestCase {
	private FileDataBuilder fdb;
	private List<Tree> trees;
	
	public void setUp() {
		fdb = new FileDataBuilder();
		trees = new ArrayList<Tree>();
	}
	
	public void testNoTrees() {
		FileData fd = fdb.buildFileDataFromTrees(trees);
		assertEquals(SourceFormat.UNKNOWN, fd.getSourceFormat());
		assertEquals(0, fd.getBlocks().size());
	}
	
	public void testOneTree() {
		Node rootNode = new Node();
		Node node1 = new Node("foo", null);
		Node node2 = new Node("bar", null);
		node1.setParent(rootNode);
		node2.setParent(rootNode);
		rootNode.getChildren().add(node1);
		rootNode.getChildren().add(node2);
		Tree tree = new Tree(rootNode);
		tree.setName("myTree");
		trees.add(tree);
		
		FileData fd = fdb.buildFileDataFromTrees(trees);
		assertEquals(SourceFormat.UNKNOWN, fd.getSourceFormat());
		assertEquals(1, fd.getBlocks().size());
		TreesBlock treesBlock = (TreesBlock) fd.getBlocks().get(0);
		assertEquals(1, treesBlock.getStatements().size());
		TreeStatement treeStatement = (TreeStatement) treesBlock.getStatements().get(0);
		assertEquals(tree, treeStatement.getTree());
	}
	
	public void testMultipleTrees() {
		Node rootNode = new Node();
		Node node1 = new Node("foo", null);
		Node node2 = new Node("bar", null);
		node1.setParent(rootNode);
		node2.setParent(rootNode);
		rootNode.getChildren().add(node1);
		rootNode.getChildren().add(node2);
		Tree tree = new Tree(rootNode);
		tree.setName("myTree");
		trees.add(tree);
		
		Node rootNode2 = new Node();
		Node node12 = new Node("foo", null);
		Node node22 = new Node("bar", null);
		node12.setParent(rootNode);
		node22.setParent(rootNode);
		rootNode2.getChildren().add(node12);
		rootNode2.getChildren().add(node22);
		Tree tree2 = new Tree(rootNode2);
		tree2.setName("myTree");
		trees.add(tree2);

		FileData fd = fdb.buildFileDataFromTrees(trees);
		assertEquals(SourceFormat.UNKNOWN, fd.getSourceFormat());
		assertEquals(1, fd.getBlocks().size());
		TreesBlock treesBlock = (TreesBlock) fd.getBlocks().get(0);
		assertEquals(2, treesBlock.getStatements().size());
		TreeStatement treeStatement = (TreeStatement) treesBlock.getStatements().get(0);
		assertEquals(tree, treeStatement.getTree());
		treeStatement = (TreeStatement) treesBlock.getStatements().get(1);
		assertEquals(tree2, treeStatement.getTree());		
	}

	public void testNoMatrix() {
		FileData fd = fdb.buildFileDataFromMatrix((Matrix)null);
		assertEquals(0, fd.getBlocks().size());
	}

	public void testMatrix() {
		Matrix matrix = new Matrix();
		List<String> chars = new ArrayList<String>();
		chars.add("char1");
		matrix.setCharacterLabels(chars);
		List<String> taxa = new ArrayList<String>();
		taxa.add("taxon1");
		matrix.setTaxaLabels(taxa);
		matrix.setDataType(Matrix.CONTINUOUS);
		List<Object> taxonValues = new ArrayList<Object>();
		taxonValues.add(0.1);
		Map<String, List<Object>> taxonValueMap = new HashMap<String, List<Object>>();
		taxonValueMap.put("taxon1", taxonValues);
		matrix.setCharacterMatrix(taxonValueMap);
		
		FileData fd = fdb.buildFileDataFromMatrix(matrix);
		assertEquals(1, fd.getBlocks().size());
		CharactersBlock charsBlock = (CharactersBlock) fd.getBlocks().get(0);
		assertEquals(4, charsBlock.getStatements().size());
		DimensionsCharStatement statement1 = (DimensionsCharStatement)charsBlock.getStatements().get(0);
		assertEquals(chars, statement1.getCharacterLabels());
		FormatStatement statement2 = (FormatStatement)charsBlock.getStatements().get(1);
		assertEquals(FormatStatement.CONTINUOUS, statement2.getDataType());
		CharStateLabelsStatement statement3 = (CharStateLabelsStatement)charsBlock.getStatements().get(2);
		assertEquals(chars, statement3.getCharacterLabels());
		MatrixStatement statement4 = (MatrixStatement)charsBlock.getStatements().get(3);
		assertEquals(taxa, statement4.getTaxaLabels());
		assertEquals(taxonValueMap, statement4.getCharacterMatrix());
	}
}
