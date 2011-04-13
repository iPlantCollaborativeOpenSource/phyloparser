package org.iplantc.phyloparser.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iplantc.phyloparser.exception.GeneratorException;
import org.iplantc.phyloparser.model.FileData;
import org.iplantc.phyloparser.model.Node;
import org.iplantc.phyloparser.model.SourceFormat;
import org.iplantc.phyloparser.model.Tree;
import org.iplantc.phyloparser.model.block.CharactersBlock;
import org.iplantc.phyloparser.model.block.TaxaBlock;
import org.iplantc.phyloparser.model.block.TreesBlock;
import org.iplantc.phyloparser.model.block.UnknownBlock;
import org.iplantc.phyloparser.model.statement.CharStateLabelsStatement;
import org.iplantc.phyloparser.model.statement.Comment;
import org.iplantc.phyloparser.model.statement.DimensionsCharStatement;
import org.iplantc.phyloparser.model.statement.DimensionsTaxaStatement;
import org.iplantc.phyloparser.model.statement.FormatStatement;
import org.iplantc.phyloparser.model.statement.MatrixStatement;
import org.iplantc.phyloparser.model.statement.TaxlabelsStatement;
import org.iplantc.phyloparser.model.statement.TreeStatement;
import org.iplantc.phyloparser.model.statement.UnrecognizedStatement;

import junit.framework.TestCase;

public class TestNexusGenerator extends TestCase {
	public void testEmptyFile() throws Throwable {
		FileData fd = new FileData();
		NexusGenerator ng = new NexusGenerator();

		String nexusFile = ng.generate(fd);

		assertEquals("#NEXUS\n", nexusFile);
	}

	public void testProvenanceComment() throws Throwable {
		FileData fd = new FileData();
		fd.getProvenance().add("This is my provenance comment");
		NexusGenerator ng = new NexusGenerator();

		String nexusFile = ng.generate(fd);

		assertEquals("#NEXUS\n\n[\nThis is my provenance comment\n]\n", nexusFile);
	}

	public void testMultipleProvenanceComments() throws Throwable {
		FileData fd = new FileData();
		fd.getProvenance().add("This is my provenance comment");
		fd.getProvenance().add("This is my second provenance comment");
		NexusGenerator ng = new NexusGenerator();

		String nexusFile = ng.generate(fd);

		assertEquals("#NEXUS\n\n[\nThis is my provenance comment\nThis is my second provenance comment\n]\n", nexusFile);
	}

	public void testOneUnknownBlock() throws Throwable {
		FileData fd = new FileData();
		UnknownBlock unkBlock = new UnknownBlock();
		unkBlock.setType("MYBLOCK");
		unkBlock.setContent("\n  TITLE This is my block;\n  CONTENT And this is the content;\n");
		fd.getBlocks().add(unkBlock);

		NexusGenerator ng = new NexusGenerator();
		String nexusFile = ng.generate(fd);

		assertEquals("#NEXUS\n\nBEGIN MYBLOCK;\n  TITLE This is my block;\n  CONTENT And this is the content;\nEND;\n", nexusFile);
	}

	public void testThreeUnknownBlocks() throws Throwable {
		FileData fd = new FileData();
		UnknownBlock unkBlock = new UnknownBlock();
		unkBlock.setType("BLOCK1");
		unkBlock.setContent("\n  CONTENT block1;\n");
		fd.getBlocks().add(unkBlock);
		unkBlock = new UnknownBlock();
		unkBlock.setType("BLOCK2");
		unkBlock.setContent("\n  CONTENT block2;\n");
		fd.getBlocks().add(unkBlock);
		unkBlock = new UnknownBlock();
		unkBlock.setType("BLOCK3");
		unkBlock.setContent("\n  CONTENT block3;\n");
		fd.getBlocks().add(unkBlock);

		NexusGenerator ng = new NexusGenerator();
		String nexusFile = ng.generate(fd);

		assertEquals("#NEXUS\n\nBEGIN BLOCK1;\n  CONTENT block1;\nEND;\n\nBEGIN BLOCK2;\n  CONTENT block2;\nEND;\n\nBEGIN BLOCK3;\n  CONTENT block3;\nEND;\n", nexusFile);
	}

	public void testTaxaBlock() throws Throwable {
		FileData fd = new FileData();
		TaxaBlock taxaBlock = new TaxaBlock();
		List<String> taxaLabels = new ArrayList<String>();
		taxaLabels.add("Taxon_1");
		taxaLabels.add("Taxon_2");
		taxaLabels.add("Taxon_3");
		DimensionsTaxaStatement dimensionsTaxaStatement = new DimensionsTaxaStatement();
		dimensionsTaxaStatement.setTaxaLabels(taxaLabels);
		TaxlabelsStatement taxlabelsStatement = new TaxlabelsStatement();
		taxlabelsStatement.setTaxaLabels(taxaLabels);
		taxaBlock.getStatements().add(dimensionsTaxaStatement);
		taxaBlock.getStatements().add(taxlabelsStatement);
		fd.getBlocks().add(taxaBlock);

		NexusGenerator ng = new NexusGenerator();
		String nexusFile = ng.generate(fd);

		assertEquals("#NEXUS\n\nBEGIN TAXA;\n  DIMENSIONS NTAX=3;\n  TAXLABELS Taxon_1 Taxon_2 Taxon_3;\nEND;\n", nexusFile);
	}

	public void testTaxaBlockWithSpacedTaxonLabels() throws Throwable {
		FileData fd = new FileData();
		TaxaBlock taxaBlock = new TaxaBlock();
		List<String> taxaLabels = new ArrayList<String>();
		taxaLabels.add("Taxon 1");
		taxaLabels.add("Taxon 2");
		taxaLabels.add("Wilson's Taxon 3");
		DimensionsTaxaStatement dimensionsTaxaStatement = new DimensionsTaxaStatement();
		dimensionsTaxaStatement.setTaxaLabels(taxaLabels);
		TaxlabelsStatement taxlabelsStatement = new TaxlabelsStatement();
		taxlabelsStatement.setTaxaLabels(taxaLabels);
		taxaBlock.getStatements().add(dimensionsTaxaStatement);
		taxaBlock.getStatements().add(taxlabelsStatement);
		fd.getBlocks().add(taxaBlock);

		NexusGenerator ng = new NexusGenerator();
		String nexusFile = ng.generate(fd);

		assertEquals("#NEXUS\n\nBEGIN TAXA;\n  DIMENSIONS NTAX=3;\n  TAXLABELS 'Taxon 1' 'Taxon 2' 'Wilson''s Taxon 3';\nEND;\n", nexusFile);
	}

	public void testTaxaBlockWithUnrecognizedStatement() throws Throwable {
		FileData fd = new FileData();
		TaxaBlock taxaBlock = new TaxaBlock();
		List<String> taxaLabels = new ArrayList<String>();
		taxaLabels.add("Taxon_1");
		taxaLabels.add("Taxon_2");
		taxaLabels.add("Taxon_3");
		DimensionsTaxaStatement dimensionsTaxaStatement = new DimensionsTaxaStatement();
		dimensionsTaxaStatement.setTaxaLabels(taxaLabels);
		UnrecognizedStatement unrecognizedStatement = new UnrecognizedStatement("TITLE This is my taxa block;");
		TaxlabelsStatement taxlabelsStatement = new TaxlabelsStatement();
		taxlabelsStatement.setTaxaLabels(taxaLabels);
		taxaBlock.getStatements().add(dimensionsTaxaStatement);
		taxaBlock.getStatements().add(unrecognizedStatement);
		taxaBlock.getStatements().add(taxlabelsStatement);
		fd.getBlocks().add(taxaBlock);

		NexusGenerator ng = new NexusGenerator();
		String nexusFile = ng.generate(fd);

		assertEquals("#NEXUS\n\nBEGIN TAXA;\n  DIMENSIONS NTAX=3;\n  TITLE This is my taxa block;\n  TAXLABELS Taxon_1 Taxon_2 Taxon_3;\nEND;\n", nexusFile);
	}

	public void testEmptyTreesBlock() throws Throwable {
		FileData fd = new FileData();
		TreesBlock treesBlock = new TreesBlock();
		fd.getBlocks().add(treesBlock);

		NexusGenerator ng = new NexusGenerator();
		String nexusFile = ng.generate(fd);

		assertEquals("#NEXUS\n\nBEGIN TREES;\nEND;\n", nexusFile);
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

		NexusGenerator ng = new NexusGenerator();
		String nexusFile = ng.generate(fd);

		assertEquals("#NEXUS\n\nBEGIN TREES;\n  TREE myTree = (foo,bar);\nEND;\n", nexusFile);
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

		NexusGenerator ng = new NexusGenerator();
		String nexusFile = ng.generate(fd);

		assertEquals("#NEXUS\n\nBEGIN TREES;\n  TREE 'my Tree' = ('foo 1','bar 2');\nEND;\n", nexusFile);
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

		NexusGenerator ng = new NexusGenerator();
		String nexusFile = ng.generate(fd);

		assertEquals("#NEXUS\n\nBEGIN TREES;\n  TREE myTree = (foo,bar);\n  TREE myTree2 = (baz,whump);\n  TREE myTree3 = (ding:0.3,dong:0.4);\nEND;\n", nexusFile);
	}

	public void testTreesBlockWithUnrecognizedStatement() throws GeneratorException {
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
		tb.getStatements().add(new UnrecognizedStatement("TITLE This is a tree;"));
		tb.getStatements().add(new TreeStatement(tree));

		NexusGenerator ng = new NexusGenerator();
		String nexusFile = ng.generate(fd);

		assertEquals("#NEXUS\n\nBEGIN TREES;\n  TITLE This is a tree;\n  TREE myTree = (foo,bar);\nEND;\n", nexusFile);
	}

	public void testEmptyCharactersBlock() throws GeneratorException {
		FileData fd = new FileData();
		CharactersBlock charsBlock = new CharactersBlock();
		fd.getBlocks().add(charsBlock);

		NexusGenerator ng = new NexusGenerator();
		String nexusFile = ng.generate(fd);

		assertEquals("#NEXUS\n\nBEGIN CHARACTERS;\nEND;\n", nexusFile);
	}

	public void testBasicCharactersBlock() throws GeneratorException {
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

		NexusGenerator ng = new NexusGenerator();
		String nexusFile = ng.generate(fd);

		assertEquals("#NEXUS\n\nBEGIN CHARACTERS;\n  DIMENSIONS NCHAR=1 NTAX=1;\n  FORMAT DATATYPE=CONTINUOUS;\n  CHARLABELS char1;\n  MATRIX\ntaxon1  0.1\n;\nEND;\n", nexusFile);
	}

	public void testSeveralCharactersSeveralTaxaCharactersBlock() throws GeneratorException {
		FileData fd = new FileData();
		fd.setSourceFormat(SourceFormat.NEXUS);
		CharactersBlock charBlock = new CharactersBlock();
		List<String> chars = new ArrayList<String>();
		chars.add("char 1");
		chars.add("char 2");
		chars.add("char 3");
		charBlock.getStatements().add(new DimensionsCharStatement(chars));
		charBlock.getStatements().add(new FormatStatement(FormatStatement.CONTINUOUS));
		charBlock.getStatements().add(new CharStateLabelsStatement(chars));
		List<Object> taxonValues = new ArrayList<Object>();
		Map<String, List<Object>> taxonValueMap = new HashMap<String, List<Object>>();
		taxonValues.add(0.1);
		taxonValues.add(0.2);
		taxonValues.add(0.3);
		taxonValueMap.put("taxon 1", taxonValues);
		taxonValues = new ArrayList<Object>();
		taxonValues.add(0.4);
		taxonValues.add(0.5);
		taxonValues.add(0.6);
		taxonValueMap.put("taxon 2", taxonValues);
		taxonValues = new ArrayList<Object>();
		taxonValues.add(0.7);
		taxonValues.add(null);
		taxonValues.add("UNK");
		taxonValueMap.put("taxon 3", taxonValues);
		charBlock.getStatements().add(new MatrixStatement(taxonValueMap));
		fd.getBlocks().add(charBlock);

		NexusGenerator ng = new NexusGenerator();
		String nexusFile = ng.generate(fd);

		System.out.println(nexusFile);
		assertEquals("#NEXUS\n\nBEGIN CHARACTERS;\n  DIMENSIONS NCHAR=3 NTAX=3;\n  FORMAT DATATYPE=CONTINUOUS;\n  CHARLABELS 'char 1' 'char 2' 'char 3';\n  MATRIX\n'taxon 1'  0.1 0.2 0.3\n'taxon 2'  0.4 0.5 0.6\n'taxon 3'  0.7 ? UNK\n;\nEND;\n", nexusFile);
	}

	public void testUnrecognizedStatementInCharactersBlock() throws GeneratorException {
		FileData fd = new FileData();
		fd.setSourceFormat(SourceFormat.NEXUS);
		CharactersBlock charBlock = new CharactersBlock();
		List<String> chars = new ArrayList<String>();
		chars.add("char 1");
		chars.add("char 2");
		chars.add("char 3");
		charBlock.getStatements().add(new DimensionsCharStatement(chars));
		charBlock.getStatements().add(new FormatStatement(FormatStatement.CONTINUOUS));
		charBlock.getStatements().add(new UnrecognizedStatement("TITLE And here are the wonderful character labels;"));
		charBlock.getStatements().add(new CharStateLabelsStatement(chars));
		List<Object> taxonValues = new ArrayList<Object>();
		Map<String, List<Object>> taxonValueMap = new HashMap<String, List<Object>>();
		taxonValues.add(0.1);
		taxonValues.add(0.2);
		taxonValues.add(0.3);
		taxonValueMap.put("taxon 1", taxonValues);
		taxonValues = new ArrayList<Object>();
		taxonValues.add(0.4);
		taxonValues.add(0.5);
		taxonValues.add(0.6);
		taxonValueMap.put("taxon 2", taxonValues);
		taxonValues = new ArrayList<Object>();
		taxonValues.add(0.7);
		taxonValues.add(null);
		taxonValues.add(0.9);
		taxonValueMap.put("taxon 3", taxonValues);
		charBlock.getStatements().add(new MatrixStatement(taxonValueMap));
		fd.getBlocks().add(charBlock);

		NexusGenerator ng = new NexusGenerator();
		String nexusFile = ng.generate(fd);

		assertEquals("#NEXUS\n\nBEGIN CHARACTERS;\n  DIMENSIONS NCHAR=3 NTAX=3;\n  FORMAT DATATYPE=CONTINUOUS;\n  TITLE And here are the wonderful character labels;\n  CHARLABELS 'char 1' 'char 2' 'char 3';\n  MATRIX\n'taxon 1'  0.1 0.2 0.3\n'taxon 2'  0.4 0.5 0.6\n'taxon 3'  0.7 ? 0.9\n;\nEND;\n", nexusFile);
	}

	public void testFirstDataColumnLineup() throws GeneratorException {
		FileData fd = new FileData();
		fd.setSourceFormat(SourceFormat.NEXUS);
		CharactersBlock charBlock = new CharactersBlock();
		List<String> chars = new ArrayList<String>();
		chars.add("char 1");
		chars.add("char 2");
		chars.add("char 3");
		charBlock.getStatements().add(new DimensionsCharStatement(chars));
		charBlock.getStatements().add(new FormatStatement(FormatStatement.CONTINUOUS));
		charBlock.getStatements().add(new CharStateLabelsStatement(chars));
		List<Object> taxonValues = new ArrayList<Object>();
		Map<String, List<Object>> taxonValueMap = new HashMap<String, List<Object>>();
		List<String> taxaLabels = new ArrayList<String>();
		taxaLabels.add("short taxon");
		taxaLabels.add("much longer taxon");
		taxaLabels.add("outrageous ginormously huge taxon");
		taxonValues.add(0.1);
		taxonValues.add(0.2);
		taxonValues.add(0.3);
		taxonValueMap.put("short taxon", taxonValues);
		taxonValues = new ArrayList<Object>();
		taxonValues.add(0.4);
		taxonValues.add(0.5);
		taxonValues.add(0.6);
		taxonValueMap.put("much longer taxon", taxonValues);
		taxonValues = new ArrayList<Object>();
		taxonValues.add(0.7);
		taxonValues.add(null);
		taxonValues.add(0.9);
		taxonValueMap.put("outrageous ginormously huge taxon", taxonValues);
		charBlock.getStatements().add(new MatrixStatement(taxonValueMap, taxaLabels));
		fd.getBlocks().add(charBlock);

		NexusGenerator ng = new NexusGenerator();
		String nexusFile = ng.generate(fd);

		assertEquals("#NEXUS\n\nBEGIN CHARACTERS;\n  DIMENSIONS NCHAR=3 NTAX=3;\n  FORMAT DATATYPE=CONTINUOUS;\n  CHARLABELS 'char 1' 'char 2' 'char 3';\n  MATRIX\n" +
				"'short taxon'                        0.1 0.2 0.3\n" +
				"'much longer taxon'                  0.4 0.5 0.6\n" +
				"'outrageous ginormously huge taxon'  0.7 ? 0.9\n;\nEND;\n", nexusFile);
	}

	public void testCommentOutsideOfBlocks() throws GeneratorException {
		FileData fd = new FileData();
		Comment comment = new Comment("This is my comment");
		Comment comment2 = new Comment("This is another comment");
		fd.getBlocks().add(comment);
		fd.getBlocks().add(comment2);

		NexusGenerator ng = new NexusGenerator();
		String nexusFile = ng.generate(fd);

		assertEquals("#NEXUS\n\n[This is my comment]\n\n[This is another comment]\n", nexusFile);
	}

	public void testCommentsInsideTaxaBlock() throws GeneratorException {
		FileData fd = new FileData();
		TaxaBlock taxaBlock = new TaxaBlock();
		List<String> taxaLabels = new ArrayList<String>();
		taxaLabels.add("Taxon_1");
		taxaLabels.add("Taxon_2");
		taxaLabels.add("Taxon_3");
		DimensionsTaxaStatement dimensionsTaxaStatement = new DimensionsTaxaStatement();
		dimensionsTaxaStatement.setTaxaLabels(taxaLabels);
		TaxlabelsStatement taxlabelsStatement = new TaxlabelsStatement();
		taxlabelsStatement.setTaxaLabels(taxaLabels);
		taxaBlock.getStatements().add(new Comment("This is my dimensions statement"));
		taxaBlock.getStatements().add(dimensionsTaxaStatement);
		taxaBlock.getStatements().add(new Comment("This is my taxlabels statement"));
		taxaBlock.getStatements().add(taxlabelsStatement);
		fd.getBlocks().add(taxaBlock);

		NexusGenerator ng = new NexusGenerator();
		String nexusFile = ng.generate(fd);

		assertEquals("#NEXUS\n\nBEGIN TAXA;\n  [This is my dimensions statement]\n  DIMENSIONS NTAX=3;\n  [This is my taxlabels statement]\n  TAXLABELS Taxon_1 Taxon_2 Taxon_3;\nEND;\n", nexusFile);
	}

	public void testCommentsInsideTreesBlock() throws GeneratorException {
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
		tb.getStatements().add(new Comment("This is before the tree"));
		tb.getStatements().add(new TreeStatement(tree));
		tb.getStatements().add(new Comment("This is after the tree"));

		NexusGenerator ng = new NexusGenerator();
		String nexusFile = ng.generate(fd);

		assertEquals("#NEXUS\n\nBEGIN TREES;\n  [This is before the tree]\n  TREE myTree = (foo,bar);\n  [This is after the tree]\nEND;\n", nexusFile);
	}

	public void testCommentsInsideCharactersBlock() throws GeneratorException {
		FileData fd = new FileData();
		CharactersBlock charBlock = new CharactersBlock();
		List<String> chars = new ArrayList<String>();
		chars.add("char1");
		charBlock.getStatements().add(new DimensionsCharStatement(chars));
		charBlock.getStatements().add(new FormatStatement(FormatStatement.CONTINUOUS));
		charBlock.getStatements().add(new CharStateLabelsStatement(chars));
		charBlock.getStatements().add(new Comment("TODO: add more data..."));
		List<Object> taxonValues = new ArrayList<Object>();
		taxonValues.add(0.1);
		Map<String, List<Object>> taxonValueMap = new HashMap<String, List<Object>>();
		taxonValueMap.put("taxon1", taxonValues);
		charBlock.getStatements().add(new MatrixStatement(taxonValueMap));
		fd.getBlocks().add(charBlock);

		NexusGenerator ng = new NexusGenerator();
		String nexusFile = ng.generate(fd);

		assertEquals("#NEXUS\n\nBEGIN CHARACTERS;\n  DIMENSIONS NCHAR=1 NTAX=1;\n  FORMAT DATATYPE=CONTINUOUS;\n  CHARLABELS char1;\n  [TODO: add more data...]\n  MATRIX\ntaxon1  0.1\n;\nEND;\n", nexusFile);
	}

	public void testUnknownBlockNewlineCorrection() throws Throwable {
		FileData fd = new FileData();
		UnknownBlock unkBlock = new UnknownBlock();
		unkBlock.setType("MYBLOCK");
		unkBlock.setContent("\n  TITLE This is my block;\r  CONTENT And this is the content;\r\n");
		fd.getBlocks().add(unkBlock);

		NexusGenerator ng = new NexusGenerator();
		String nexusFile = ng.generate(fd);

		assertEquals("#NEXUS\n\nBEGIN MYBLOCK;\n  TITLE This is my block;\n  CONTENT And this is the content;\nEND;\n", nexusFile);
	}

	public void testCommentNewlineCorrection() throws Throwable {
		FileData fd = new FileData();
		Comment comment = new Comment("This is my comment\r");
		Comment comment2 = new Comment("This is another comment\r\n");
		Comment comment3 = new Comment("This is a third comment\n");
		fd.getBlocks().add(comment);
		fd.getBlocks().add(comment2);
		fd.getBlocks().add(comment3);

		NexusGenerator ng = new NexusGenerator();
		String nexusFile = ng.generate(fd);

		assertEquals("#NEXUS\n\n[This is my comment\n]\n\n[This is another comment\n]\n\n[This is a third comment\n]\n", nexusFile);
	}
	
	public void testUnknownStatementNewlineCorrection() throws Throwable {
		FileData fd = new FileData();
		TaxaBlock taxa = new TaxaBlock();
		taxa.getStatements().add(new UnrecognizedStatement("STATEMENT with a\rnewline\r\n;"));
		TreesBlock trees = new TreesBlock();
		trees.getStatements().add(new UnrecognizedStatement("STATEMENT with a\rnewline\r\n;"));
		CharactersBlock chars = new CharactersBlock();
		chars.getStatements().add(new UnrecognizedStatement("STATEMENT with a\rnewline\r\n;"));
		
		fd.getBlocks().add(taxa);
		fd.getBlocks().add(trees);
		fd.getBlocks().add(chars);
		
		NexusGenerator ng = new NexusGenerator();
		String nexusFile = ng.generate(fd);

		assertEquals("#NEXUS\n\nBEGIN TAXA;\n  STATEMENT with a\nnewline\n;\nEND;\n\nBEGIN TREES;\n  STATEMENT with a\nnewline\n;\nEND;\n\nBEGIN CHARACTERS;\n  STATEMENT with a\nnewline\n;\nEND;\n", nexusFile);		
	}

	public void testCommentWithinBlockNewlineCorrection() throws Throwable {
		FileData fd = new FileData();
		TaxaBlock taxa = new TaxaBlock();
		taxa.getStatements().add(new Comment("Comment with a\rnewline\r\n"));
		TreesBlock trees = new TreesBlock();
		trees.getStatements().add(new Comment("Comment with a\rnewline\r\n"));
		CharactersBlock chars = new CharactersBlock();
		chars.getStatements().add(new Comment("Comment with a\rnewline\r\n"));
		
		fd.getBlocks().add(taxa);
		fd.getBlocks().add(trees);
		fd.getBlocks().add(chars);
		
		NexusGenerator ng = new NexusGenerator();
		String nexusFile = ng.generate(fd);

		assertEquals("#NEXUS\n\nBEGIN TAXA;\n  [Comment with a\nnewline\n]\nEND;\n\nBEGIN TREES;\n  [Comment with a\nnewline\n]\nEND;\n\nBEGIN CHARACTERS;\n  [Comment with a\nnewline\n]\nEND;\n", nexusFile);		
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

		NexusGenerator ng = new NexusGenerator();
		String nexusFile = ng.generate(fd);

		assertEquals("#NEXUS\n\nBEGIN TREES;\n  TREE myTree = (foo,bar):0.427;\nEND;\n", nexusFile);
	}
}
