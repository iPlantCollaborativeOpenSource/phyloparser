package org.iplantc.phyloparser.parser;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.iplantc.phyloparser.exception.ParserException;
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

public class TestNexusParser extends TestCase {
	private NexusParser nexusParser;
	
	public void setUp() {
		nexusParser = new NexusParser();
	}

	public void testEmptyFile() throws Throwable {
		assertEquals(0, nexusParser.parse("#NEXUS\n").getBlocks().size());		
	}
	
	public void testOneEmptyUnknownBlock() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN blah;\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		UnknownBlock block = (UnknownBlock)fileData.getBlocks().get(0);
		assertEquals("blah", block.getType());
		assertEquals("\n", block.getContent());
	}
	
	public void testOneFilledUnknownBlock() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN blah;\nSTATEMENT foo bar baz;\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		UnknownBlock block = (UnknownBlock)fileData.getBlocks().get(0);
		assertEquals("blah", block.getType());
		assertEquals("\nSTATEMENT foo bar baz;\n", block.getContent());	
	}
	
	public void testTwoUnknownBlocks() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN poo;\nEND;\nBEGIN blah;\nSTATEMENT foo bar baz;\nEND;");
		assertEquals(2, fileData.getBlocks().size());
		UnknownBlock block = (UnknownBlock)fileData.getBlocks().get(0);
		assertEquals("poo", block.getType());
		assertEquals("\n", block.getContent());
		block = (UnknownBlock)fileData.getBlocks().get(1);
		assertEquals("blah", block.getType());
		assertEquals("\nSTATEMENT foo bar baz;\n", block.getContent());	
	}
	
	public void testProvenance() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\n[Juicy provenance here]\n");
		assertEquals(0, fileData.getBlocks().size());
		assertEquals(1, fileData.getProvenance().size());
		assertEquals("Juicy provenance here", fileData.getProvenance().get(0));
	}
	
	public void testCommentWithinBlock() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN blah;\n[Comment]\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		assertEquals(0, fileData.getProvenance().size());
		UnknownBlock block = (UnknownBlock)fileData.getBlocks().get(0);
		assertEquals("blah", block.getType());
		assertEquals("\n[Comment]\n", block.getContent());		
	}
	
	public void testCommentWithinStatement() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN blah;\nSTATEMENT foo bar [Comment]baz;\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		UnknownBlock block = (UnknownBlock)fileData.getBlocks().get(0);
		assertEquals("blah", block.getType());
		assertEquals("\nSTATEMENT foo bar [Comment]baz;\n", block.getContent());
	}
	
	public void testNestedProvenanceComment() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\n[Juicy provenance [and a nested comment] here]\n");
		assertEquals(0, fileData.getBlocks().size());
		assertEquals(1, fileData.getProvenance().size());
		assertEquals("Juicy provenance [and a nested comment] here", fileData.getProvenance().get(0));		
	}
	
	public void testOneEmptyTreeBlock() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TREES;\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		TreesBlock block = (TreesBlock)fileData.getBlocks().get(0);
		assertEquals(0, block.getTrees().size());
		assertEquals(0, block.getUnrecognizedStatements().size());
	}
	
	public void testOneTreeBlockWithOneTree() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TREES;\nTREE mytree = A;\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		TreesBlock block = (TreesBlock)fileData.getBlocks().get(0);
		assertEquals(1, block.getTrees().size());
		Tree tree = block.getTrees().get(0);
		assertEquals("mytree", tree.getName());
		Node node = tree.getRoot();
		assertEquals("A", node.getName());
		assertEquals(0, node.getChildren().size());
	}
	
	public void testOneTreeBlockWithOneTreeWithoutName() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TREES;\nTREE = A;\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		TreesBlock block = (TreesBlock)fileData.getBlocks().get(0);
		assertEquals(1, block.getTrees().size());
		Tree tree = block.getTrees().get(0);
		assertEquals(null, tree.getName());
		Node node = tree.getRoot();
		assertEquals("A", node.getName());
		assertEquals(0, node.getChildren().size());
	}
	
	public void testTwoTaxa() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TREES;\nTREE foo = (A,B);\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		TreesBlock block = (TreesBlock)fileData.getBlocks().get(0);
		Node tree = block.getTrees().get(0).getRoot();
		assertNull(tree.getName());
		assertEquals("A", tree.getChildren().get(0).getName());
		assertEquals("B", tree.getChildren().get(1).getName());
	}
	
	public void testWithBranchLengths() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TREES;\nTREE foo = (A:0.3,B:0.5):0.2538;\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		TreesBlock block = (TreesBlock)fileData.getBlocks().get(0);
		Node tree = block.getTrees().get(0).getRoot();
		assertNull(tree.getName());
		assertEquals(0.2538, tree.getBranchLength());
		assertEquals("A", tree.getChildren().get(0).getName());
		assertEquals(0.3, tree.getChildren().get(0).getBranchLength());
		assertEquals("B", tree.getChildren().get(1).getName());
		assertEquals(0.5, tree.getChildren().get(1).getBranchLength());
	}

	public void testWithInternalNodeLabel() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TREES;\nTREE foo = (A,B)C;\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		TreesBlock block = (TreesBlock)fileData.getBlocks().get(0);
		Node tree = block.getTrees().get(0).getRoot();
		assertEquals("C", tree.getName());
		assertEquals("A", tree.getChildren().get(0).getName());
		assertEquals("B", tree.getChildren().get(1).getName());
		assertEquals("C", tree.getChildren().get(0).getParent().getName());
		assertEquals("C", tree.getChildren().get(1).getParent().getName());
	}

	public void testQuotedLabels() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TREES;\nTREE foo = (A,'B C')'D''s thing';\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		TreesBlock block = (TreesBlock)fileData.getBlocks().get(0);
		Node tree = block.getTrees().get(0).getRoot();
		assertEquals("A", tree.getChildren().get(0).getName());
		assertEquals("B C", tree.getChildren().get(1).getName());
		assertEquals("D's thing", tree.getName());
	}
	
	public void testFailedInput1() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TAXA;\n     Dimensions NTax=4;\n     TaxLabels fish frog snake mouse;\nEND;\n\nBEGIN CHARACTERS;\n     Dimensions NChar=20;\n     Format DataType=DNA;\n     Matrix\n       fish   ACATA GAGGG TACCT CTAAG\n       frog   ACATA GAGGG TACCT CTAAG\n       snake  ACATA GAGGG TACCT CTAAG\n       mouse  ACATA GAGGG TACCT CTAAG;\nEND;\n\nBEGIN TREES;\n     Tree best=(fish, (frog, (snake, mouse)));\nEND;\n");
		assertEquals(3, fileData.getBlocks().size());
		assertTrue(fileData.getBlocks().get(0) instanceof TaxaBlock);
		assertTrue(fileData.getBlocks().get(1) instanceof UnknownBlock);
		TreesBlock block = (TreesBlock)fileData.getBlocks().get(2);
		Node tree = block.getTrees().get(0).getRoot();
		assertNull(tree.getName());
		assertEquals("fish", tree.getChildren().get(0).getName());
		assertNull(tree.getChildren().get(1).getName());
		assertEquals("frog", tree.getChildren().get(1).getChildren().get(0).getName());
		assertNull(tree.getChildren().get(1).getChildren().get(1).getName());
		assertEquals("snake", tree.getChildren().get(1).getChildren().get(1).getChildren().get(0).getName());
		assertEquals("mouse", tree.getChildren().get(1).getChildren().get(1).getChildren().get(1).getName());
	}
	
	public void testTranslatedTree() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TREES;\nTRANSLATE 1 foo, 2 bar, 3 baz;\nTREE foo = (1,(2,3));\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		TreesBlock block = (TreesBlock)fileData.getBlocks().get(0);
		Node tree = block.getTrees().get(0).getRoot();
		assertNull(tree.getName());
		assertEquals("foo", tree.getChildren().get(0).getName());
		assertNull(tree.getChildren().get(1).getName());
		assertEquals("bar", tree.getChildren().get(1).getChildren().get(0).getName());
		assertEquals("baz", tree.getChildren().get(1).getChildren().get(1).getName());
	}
	
	public void testCommentsWithinTreeBlock() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TREES;\n[Comment]\nTREE mytree = A;\n[Comment 2]\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		TreesBlock block = (TreesBlock)fileData.getBlocks().get(0);
		assertEquals(1, block.getTrees().size());
		Tree tree = block.getTrees().get(0);
		assertEquals("mytree", tree.getName());
		Node node = tree.getRoot();
		assertEquals("A", node.getName());
		assertEquals(0, node.getChildren().size());
		assertEquals(3, block.getStatements().size());
		Comment comment = (Comment) block.getStatements().get(0);
		assertEquals("Comment", comment.getContent());
		comment = (Comment) block.getStatements().get(2);
		assertEquals("Comment 2", comment.getContent());
	}
	
	public void testOneEmptyTaxaBlock() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TAXA;\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		TaxaBlock block = (TaxaBlock)fileData.getBlocks().get(0);
		assertEquals(0, block.getTaxaLabels().size());		
		assertEquals(0, block.getUnrecognizedStatements().size());
	}
	
	public void testOneTaxonTaxaBlock() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TAXA;\nDIMENSIONS NTAX=1;\nTAXLABELS kabam;\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		TaxaBlock block = (TaxaBlock)fileData.getBlocks().get(0);
		assertEquals(1, block.getTaxaLabels().size());
		assertEquals("kabam", block.getTaxaLabels().get(0));
	}
	
	public void testManyTaxaTaxaBlock() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TAXA;\nDIMENSIONS NTAX=5;\nTAXLABELS kabam wham crash whiff crunch;\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		TaxaBlock block = (TaxaBlock)fileData.getBlocks().get(0);
		assertEquals(5, block.getTaxaLabels().size());
		assertEquals("kabam", block.getTaxaLabels().get(0));
		assertEquals("wham", block.getTaxaLabels().get(1));
		assertEquals("crash", block.getTaxaLabels().get(2));
		assertEquals("whiff", block.getTaxaLabels().get(3));
		assertEquals("crunch", block.getTaxaLabels().get(4));
	}
	
	public void testCommentsWithinTaxaBlock() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TAXA;\n[Check this out]\nDIMENSIONS NTAX=1;\n[My beautiful comment]\nTAXLABELS kabam;\n[And that's all]\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		TaxaBlock block = (TaxaBlock)fileData.getBlocks().get(0);
		assertEquals(1, block.getTaxaLabels().size());
		assertEquals("kabam", block.getTaxaLabels().get(0));
		assertEquals(5, block.getStatements().size());
		Comment comment = (Comment) block.getStatements().get(0);
		assertEquals("Check this out", comment.getContent());
		comment = (Comment) block.getStatements().get(2);
		assertEquals("My beautiful comment", comment.getContent());
		comment = (Comment) block.getStatements().get(4);
		assertEquals("And that's all", comment.getContent());
	}
	
	public void testOneEmptyCharactersBlock() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN CHARACTERS;\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		assertTrue(fileData.getBlocks().get(0) instanceof UnknownBlock);
	}
	
	public void testCharactersBlockFormatContinuousStatement() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN CHARACTERS;\nDIMENSIONS NCHAR=1;\nFORMAT DATATYPE=CONTINUOUS;\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		CharactersBlock block = (CharactersBlock)fileData.getBlocks().get(0);
		assertEquals(CharactersBlock.CONTINUOUS, block.getDataType());
		assertEquals(1, block.getCharacterLabels().size());		
		assertEquals(0, block.getUnrecognizedStatements().size());
	}
	
	public void testCharacterBlockBasicMatrix() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN CHARACTERS;\nDIMENSIONS NCHAR=3;\nFORMAT DATATYPE=CONTINUOUS;\nMATRIX\nTaxa_1\t0.5 0.6 0.7\nTaxa_2\t0.3 0.4 0.5\nTaxa_3\t0.8 0.9 0.2E-1;\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		CharactersBlock block = (CharactersBlock)fileData.getBlocks().get(0);
		assertEquals(CharactersBlock.CONTINUOUS, block.getDataType());
		assertEquals(3, block.getCharacterLabels().size());		
		assertEquals(3, block.getCharacterMatrix().get("Taxa_1").size());
		assertEquals(3, block.getCharacterMatrix().get("Taxa_2").size());
		assertEquals(3, block.getCharacterMatrix().get("Taxa_3").size());
		assertEquals(0.5, block.getCharacterMatrix().get("Taxa_1").get(0));
		assertEquals(0.6, block.getCharacterMatrix().get("Taxa_1").get(1));
		assertEquals(0.7, block.getCharacterMatrix().get("Taxa_1").get(2));
		assertEquals(0.3, block.getCharacterMatrix().get("Taxa_2").get(0));
		assertEquals(0.4, block.getCharacterMatrix().get("Taxa_2").get(1));
		assertEquals(0.5, block.getCharacterMatrix().get("Taxa_2").get(2));
		assertEquals(0.8, block.getCharacterMatrix().get("Taxa_3").get(0));
		assertEquals(0.9, block.getCharacterMatrix().get("Taxa_3").get(1));
		assertEquals(0.02, block.getCharacterMatrix().get("Taxa_3").get(2));
	}
	
	public void testCharactersBlockCharstateLabels() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN CHARACTERS;\nDIMENSIONS NCHAR=3;\nFORMAT DATATYPE=CONTINUOUS;\nCHARSTATELABELS 1 size_of_head, 2 size_of_tail, 3 size_of_belly_button;\nEND;");	
		assertEquals(1, fileData.getBlocks().size());
		CharactersBlock block = (CharactersBlock)fileData.getBlocks().get(0);
		assertEquals(CharactersBlock.CONTINUOUS, block.getDataType());
		assertEquals(3, block.getCharacterLabels().size());
		assertEquals("size_of_head", block.getCharacterLabels().get(0));
		assertEquals("size_of_tail", block.getCharacterLabels().get(1));
		assertEquals("size_of_belly_button", block.getCharacterLabels().get(2));		
	}
	
	public void testCharactersBlockCharLabels() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN CHARACTERS;\nDIMENSIONS NCHAR=3;\nFORMAT DATATYPE=CONTINUOUS;\nCHARLABELS size_of_head _ size_of_belly_button;\nEND;");	
		assertEquals(1, fileData.getBlocks().size());
		CharactersBlock block = (CharactersBlock)fileData.getBlocks().get(0);
		assertEquals(CharactersBlock.CONTINUOUS, block.getDataType());
		assertEquals(3, block.getCharacterLabels().size());
		assertEquals("size_of_head", block.getCharacterLabels().get(0));
		assertEquals(null, block.getCharacterLabels().get(1));
		assertEquals("size_of_belly_button", block.getCharacterLabels().get(2));		
	}
	
	public void testNotContinuousCharactersBlock() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN CHARACTERS;\nDIMENSIONS NCHAR=4;\nFORMAT DATATYPE=DNA;\nMATRIX whump ACTG\nwhip CTGA\nbickybicky TGAC;\nEND;");	
		assertEquals(1, fileData.getBlocks().size());
		assertTrue(fileData.getBlocks().get(0) instanceof UnknownBlock);
	}
	
	public void testSourceFormat() throws Throwable {
		FileData data = nexusParser.parse("#NEXUS\n");
		assertEquals(SourceFormat.NEXUS, data.getSourceFormat());
	}
	
	public void testCommentsInsideTree() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TREES;\nTREE foo = (A[0.98],B[0.94])[0.99];\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		TreesBlock block = (TreesBlock)fileData.getBlocks().get(0);
		Node tree = block.getTrees().get(0).getRoot();
		assertNull(tree.getName());
		assertEquals("A", tree.getChildren().get(0).getName());
		assertEquals("B", tree.getChildren().get(1).getName());		
	}
	
	public void testRootedUnrooted() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TREES;\nTREE foo = [&R] (A,B);\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		TreesBlock block = (TreesBlock)fileData.getBlocks().get(0);
		Tree tree = block.getTrees().get(0);
		assertEquals(Tree.ROOTED, tree.getRootType());

		fileData = nexusParser.parse("#NEXUS\nBEGIN TREES;\nTREE foo = [&U] (A,B);\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		block = (TreesBlock)fileData.getBlocks().get(0);
		tree = block.getTrees().get(0);
		assertEquals(Tree.UNROOTED, tree.getRootType());

		fileData = nexusParser.parse("#NEXUS\nBEGIN TREES;\nTREE foo = (A,B);\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		block = (TreesBlock)fileData.getBlocks().get(0);
		tree = block.getTrees().get(0);
		assertEquals(Tree.UNKNOWN_ROOTED, tree.getRootType());
	}
	
	public void testFloatAsNodeLabel() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TREES;\nTREE foo = (A,B)0.05;\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		TreesBlock block = (TreesBlock)fileData.getBlocks().get(0);
		Node tree = block.getTrees().get(0).getRoot();
		assertEquals("0.05", tree.getName());
		assertEquals("A", tree.getChildren().get(0).getName());
		assertEquals("B", tree.getChildren().get(1).getName());
	}
	
	public void testCommaAtEndOfTranslateList() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TREES;\nTRANSLATE 1 foo, 2 bar, 3 baz,;\nTREE foo = (1,(2,3));\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		TreesBlock block = (TreesBlock)fileData.getBlocks().get(0);
		Node tree = block.getTrees().get(0).getRoot();
		assertNull(tree.getName());
		assertEquals("foo", tree.getChildren().get(0).getName());
		assertNull(tree.getChildren().get(1).getName());
		assertEquals("bar", tree.getChildren().get(1).getChildren().get(0).getName());
		assertEquals("baz", tree.getChildren().get(1).getChildren().get(1).getName());		
	}
	
	public void testDoubleQuotedLabelInTree() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TREES;\nTREE foo = (A,\"TAXA B\");\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		TreesBlock block = (TreesBlock)fileData.getBlocks().get(0);
		Node tree = block.getTrees().get(0).getRoot();
		assertNull(tree.getName());
		assertEquals("A", tree.getChildren().get(0).getName());
		assertEquals("TAXA B", tree.getChildren().get(1).getName());		
	}

	public void testDoubleQuotedTreeLabel() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TREES;\nTREE \"my big fat greek tree\" = (A,\"TAXA B\");\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		TreesBlock block = (TreesBlock)fileData.getBlocks().get(0);
		Tree tree = block.getTrees().get(0);
		assertEquals("my big fat greek tree", tree.getName());
		Node rootNode = tree.getRoot();
		assertNull(rootNode.getName());
		assertEquals("A", rootNode.getChildren().get(0).getName());
		assertEquals("TAXA B", rootNode.getChildren().get(1).getName());
	}
	
	public void testTwoTreesWithDifferentQuotedSameTaxonNames() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TREES;\ntree bush_inode_labels_quoted1 = (\"inodeEFGH\");\ntree bush_inode_labels_quoted2 = ('inodeEFGH');\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		TreesBlock block = (TreesBlock)fileData.getBlocks().get(0);
		Tree tree1 = block.getTrees().get(0);
		Tree tree2 = block.getTrees().get(1);
		assertEquals("bush_inode_labels_quoted1", tree1.getName());
		assertEquals("bush_inode_labels_quoted2", tree2.getName());
		Node rootNode1 = tree1.getRoot();
		assertEquals(1, rootNode1.getChildren().size());
		assertNull(rootNode1.getName());
		assertEquals("inodeEFGH", rootNode1.getChildren().get(0).getName());
		Node rootNode2 = tree2.getRoot();
		assertEquals(1, rootNode2.getChildren().size());
		assertNull(rootNode2.getName());
		assertEquals("inodeEFGH", rootNode2.getChildren().get(0).getName());
	}
	
	public void testCharactersDimensionsWithNtax() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN CHARACTERS;\nDIMENSIONS NCHAR=1 NTAX=1;\nFORMAT DATATYPE=CONTINUOUS;\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		CharactersBlock block = (CharactersBlock)fileData.getBlocks().get(0);
		assertEquals(CharactersBlock.CONTINUOUS, block.getDataType());
		assertEquals(1, block.getCharacterLabels().size());		
	}
	
	public void testUnrecognizedStatementInTaxaBlock() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TAXA;\nTITLE I am a taxa block!;\nEND;");		
		assertEquals(1, fileData.getBlocks().size());
		TaxaBlock block = (TaxaBlock)fileData.getBlocks().get(0);
		assertEquals(1, block.getUnrecognizedStatements().size());
		assertEquals("TITLE I am a taxa block!;", block.getUnrecognizedStatements().get(0).getContent());
	}
	
	public void testMultipleUnrecognizedStatementInTaxaBlock() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TAXA;\nTITLE I am a taxa block!;\nCOLOR puce;\nEND;");		
		assertEquals(1, fileData.getBlocks().size());
		TaxaBlock block = (TaxaBlock)fileData.getBlocks().get(0);
		assertEquals(2, block.getUnrecognizedStatements().size());
		assertEquals("TITLE I am a taxa block!;", block.getUnrecognizedStatements().get(0).getContent());
		assertEquals("COLOR puce;", block.getUnrecognizedStatements().get(1).getContent());
	}

	public void testUnrecognizedStatementInTreesBlock() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TREES;\nTITLE I am a taxa block!;\nEND;");		
		assertEquals(1, fileData.getBlocks().size());
		TreesBlock block = (TreesBlock)fileData.getBlocks().get(0);
		assertEquals(1, block.getUnrecognizedStatements().size());
		assertEquals("TITLE I am a taxa block!;", block.getUnrecognizedStatements().get(0).getContent());
	}
	
	public void testMultipleUnrecognizedStatementInTreesBlock() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TREES;\nTITLE I am a taxa block!;\nCOLOR puce;\nEND;");		
		assertEquals(1, fileData.getBlocks().size());
		TreesBlock block = (TreesBlock)fileData.getBlocks().get(0);
		assertEquals(2, block.getUnrecognizedStatements().size());
		assertEquals("TITLE I am a taxa block!;", block.getUnrecognizedStatements().get(0).getContent());
		assertEquals("COLOR puce;", block.getUnrecognizedStatements().get(1).getContent());
	}

	public void testUnrecognizedStatementInCharactersBlock() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN CHARACTERS;\nDIMENSIONS NCHAR=1;\nFORMAT DATATYPE=CONTINUOUS;\nTITLE I am a taxa block!;\nEND;");		
		assertEquals(1, fileData.getBlocks().size());
		CharactersBlock block = (CharactersBlock)fileData.getBlocks().get(0);
		assertEquals(1, block.getUnrecognizedStatements().size());
		assertEquals("TITLE I am a taxa block!;", block.getUnrecognizedStatements().get(0).getContent());
	}
	
	public void testMultipleUnrecognizedStatementInCharactersBlock() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN CHARACTERS;\nDIMENSIONS NCHAR=1;\nFORMAT DATATYPE=CONTINUOUS;\nTITLE I am a taxa block!;\nCOLOR puce;\nEND;");		
		assertEquals(1, fileData.getBlocks().size());
		CharactersBlock block = (CharactersBlock)fileData.getBlocks().get(0);
		assertEquals(2, block.getUnrecognizedStatements().size());
		assertEquals("TITLE I am a taxa block!;", block.getUnrecognizedStatements().get(0).getContent());
		assertEquals("COLOR puce;", block.getUnrecognizedStatements().get(1).getContent());
	}

	public void testTaxaStatementOrderPreservation() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TAXA;\nSTATEMENT1 statement1;\nDIMENSIONS NTAX=1;\nSTATEMENT2 statement2;\nTAXLABELS kabam;\nSTATEMENT3 statement3;\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		TaxaBlock block = (TaxaBlock)fileData.getBlocks().get(0);
		assertEquals(5, block.getStatements().size());
		assertEquals("STATEMENT1 statement1;", ((UnrecognizedStatement)(block.getStatements().get(0))).getContent());
		assertEquals("STATEMENT2 statement2;", ((UnrecognizedStatement)(block.getStatements().get(2))).getContent());
		assertEquals("STATEMENT3 statement3;", ((UnrecognizedStatement)(block.getStatements().get(4))).getContent());
		assertEquals(1, ((DimensionsTaxaStatement)block.getStatements().get(1)).getTaxaLabels().size());
		assertEquals(1, ((TaxlabelsStatement)block.getStatements().get(3)).getTaxaLabels().size());
	}
	
	public void testTreesStatementOrderPreservation() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TREES;\nSTATEMENT1 statement1;\nTRANSLATE 1 foo, 2 bar, 3 baz;\nSTATEMENT2 statement2;\nTREE foo = (1,(2,3));\nTREE foo2 = ((1,2),3);\n;STATEMENT3 statement3;\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		TreesBlock block = (TreesBlock)fileData.getBlocks().get(0);
		assertEquals(5, block.getStatements().size());
		assertEquals("STATEMENT1 statement1;", ((UnrecognizedStatement)(block.getStatements().get(0))).getContent());
		assertEquals("STATEMENT2 statement2;", ((UnrecognizedStatement)(block.getStatements().get(1))).getContent());
		assertEquals("STATEMENT3 statement3;", ((UnrecognizedStatement)(block.getStatements().get(4))).getContent());
		assertEquals("foo", ((TreeStatement)block.getStatements().get(2)).getTree().getName());
		assertEquals("foo2", ((TreeStatement)block.getStatements().get(3)).getTree().getName());
	}
	
	public void testCharactersStatementOrderPreservation() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN CHARACTERS;\nSTATEMENT1 statement1;\nDIMENSIONS NCHAR=1;\nSTATEMENT2 statement2;\nFORMAT DATATYPE=CONTINUOUS;\nSTATEMENT3 statement3;\nCHARSTATELABELS 1 size_of_head;\nSTATEMENT4 statement4;\nMATRIX Taxa_1 0.5;\nSTATEMENT5 statement5;\nEND;");	
		assertEquals(1, fileData.getBlocks().size());
		CharactersBlock block = (CharactersBlock)fileData.getBlocks().get(0);
		assertEquals(9, block.getStatements().size());
		assertEquals("STATEMENT1 statement1;", ((UnrecognizedStatement)(block.getStatements().get(0))).getContent());
		assertEquals("STATEMENT2 statement2;", ((UnrecognizedStatement)(block.getStatements().get(2))).getContent());
		assertEquals("STATEMENT3 statement3;", ((UnrecognizedStatement)(block.getStatements().get(4))).getContent());
		assertEquals("STATEMENT4 statement4;", ((UnrecognizedStatement)(block.getStatements().get(6))).getContent());
		assertEquals("STATEMENT5 statement5;", ((UnrecognizedStatement)(block.getStatements().get(8))).getContent());
		assertEquals(1, ((DimensionsCharStatement)block.getStatements().get(1)).getCharacterLabels().size());
		assertEquals(FormatStatement.CONTINUOUS, ((FormatStatement)block.getStatements().get(3)).getDataType());
		assertEquals("size_of_head", ((CharStateLabelsStatement)block.getStatements().get(5)).getCharacterLabels().get(0));
		assertEquals(1, ((MatrixStatement)block.getStatements().get(7)).getCharacterMatrix().get("Taxa_1").size());
		assertEquals(0.5, ((MatrixStatement)block.getStatements().get(7)).getCharacterMatrix().get("Taxa_1").get(0));
	}
	
	public void testCollapseTaxlabelsStatements() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN TAXA;\nDIMENSIONS NTAX=2;\nTAXLABELS kabam;\nTAXLABELS kablooey;\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		TaxaBlock block = (TaxaBlock)fileData.getBlocks().get(0);
		assertEquals(2, block.getTaxaLabels().size());
		assertEquals("kabam", block.getTaxaLabels().get(0));	
		assertEquals("kablooey", block.getTaxaLabels().get(1));
		assertEquals(2, block.getStatements().size());
	}
	
	public void testCollapseCharstateLabelsStatements() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN CHARACTERS;\nDIMENSIONS NCHAR=3;\nFORMAT DATATYPE=CONTINUOUS;\nCHARSTATELABELS 1 size_of_head, 2 size_of_tail;\nCHARSTATELABELS 3 size_of_belly_button;\nEND;");	
		assertEquals(1, fileData.getBlocks().size());
		CharactersBlock block = (CharactersBlock)fileData.getBlocks().get(0);
		assertEquals(CharactersBlock.CONTINUOUS, block.getDataType());
		assertEquals(3, block.getCharacterLabels().size());
		assertEquals("size_of_head", block.getCharacterLabels().get(0));
		assertEquals("size_of_tail", block.getCharacterLabels().get(1));
		assertEquals("size_of_belly_button", block.getCharacterLabels().get(2));		
		assertEquals(3, block.getStatements().size());
	}
	
	public void testCollapseCharLabelsStatements() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN CHARACTERS;\nDIMENSIONS NCHAR=3;\nFORMAT DATATYPE=CONTINUOUS;\nCHARLABELS size_of_head size_of_tail;\nCHARLABELS size_of_belly_button;\nEND;");	
		assertEquals(1, fileData.getBlocks().size());
		CharactersBlock block = (CharactersBlock)fileData.getBlocks().get(0);
		assertEquals(CharactersBlock.CONTINUOUS, block.getDataType());
		assertEquals(3, block.getCharacterLabels().size());
		assertEquals("size_of_head", block.getCharacterLabels().get(0));
		assertEquals("size_of_tail", block.getCharacterLabels().get(1));
		assertEquals("size_of_belly_button", block.getCharacterLabels().get(2));		
		assertEquals(3, block.getStatements().size());
	}
	
	public void testCollapseMatrixStatements() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN CHARACTERS;\nDIMENSIONS NCHAR=3;\nFORMAT DATATYPE=CONTINUOUS;\nMATRIX\nTaxa_1\t0.5 0.6 0.7\nTaxa_2\t0.3 0.4 0.5;\nMATRIX Taxa_3\t0.8 0.9 0.2E-1;\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		CharactersBlock block = (CharactersBlock)fileData.getBlocks().get(0);
		assertEquals(CharactersBlock.CONTINUOUS, block.getDataType());
		assertEquals(3, block.getCharacterLabels().size());		
		assertEquals(3, block.getCharacterMatrix().get("Taxa_1").size());
		assertEquals(3, block.getCharacterMatrix().get("Taxa_2").size());
		assertEquals(3, block.getCharacterMatrix().get("Taxa_3").size());
		assertEquals(0.5, block.getCharacterMatrix().get("Taxa_1").get(0));
		assertEquals(0.6, block.getCharacterMatrix().get("Taxa_1").get(1));
		assertEquals(0.7, block.getCharacterMatrix().get("Taxa_1").get(2));
		assertEquals(0.3, block.getCharacterMatrix().get("Taxa_2").get(0));
		assertEquals(0.4, block.getCharacterMatrix().get("Taxa_2").get(1));
		assertEquals(0.5, block.getCharacterMatrix().get("Taxa_2").get(2));
		assertEquals(0.8, block.getCharacterMatrix().get("Taxa_3").get(0));
		assertEquals(0.9, block.getCharacterMatrix().get("Taxa_3").get(1));
		assertEquals(0.02, block.getCharacterMatrix().get("Taxa_3").get(2));		
		assertEquals(3, block.getStatements().size());
	}
	
	public void testCommentsBeforeBlocks() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\n[Juicy provenance here]\n[And another comment]\n");
		assertEquals(1, fileData.getBlocks().size());
		assertEquals(1, fileData.getProvenance().size());
		assertEquals("Juicy provenance here", fileData.getProvenance().get(0));
		Comment comment = (Comment) fileData.getBlocks().get(0);
		assertEquals("And another comment", comment.getContent());
	}
	
	public void testCommentsBetweenBlocks() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\n[Juicy provenance here]\n[And another comment]\nBEGIN poo;\nEND;\n[More commentary]\nBEGIN blah;\nSTATEMENT foo bar baz;\nEND;\n[One more thing...]\n");
		assertEquals(1, fileData.getProvenance().size());
		assertEquals("Juicy provenance here", fileData.getProvenance().get(0));
		assertEquals(5, fileData.getBlocks().size());
		Comment comment = (Comment)fileData.getBlocks().get(0);
		assertEquals("And another comment", comment.getContent());
		UnknownBlock block = (UnknownBlock)fileData.getBlocks().get(1);
		assertEquals("poo", block.getType());
		assertEquals("\n", block.getContent());
		comment = (Comment)fileData.getBlocks().get(2);
		assertEquals("More commentary", comment.getContent());
		block = (UnknownBlock)fileData.getBlocks().get(3);
		assertEquals("blah", block.getType());
		assertEquals("\nSTATEMENT foo bar baz;\n", block.getContent());		
		comment = (Comment)fileData.getBlocks().get(4);
		assertEquals("One more thing...", comment.getContent());
	}
	
	public void testCommentsWithinCharactersBlock() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN CHARACTERS;\n[Comment 1]\nDIMENSIONS NCHAR=3;\n[Comment 2]\nFORMAT DATATYPE=CONTINUOUS;\n[Comment 3]\nMATRIX\nTaxa_1\t0.5 0.6 0.7\nTaxa_2\t0.3 0.4 0.5\nTaxa_3\t0.8 0.9 0.2E-1;\n[Comment 4]\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		CharactersBlock block = (CharactersBlock)fileData.getBlocks().get(0);
		assertEquals(7, block.getStatements().size());
		Comment comment = (Comment)block.getStatements().get(0);
		assertEquals("Comment 1", comment.getContent());		
		comment = (Comment)block.getStatements().get(2);
		assertEquals("Comment 2", comment.getContent());		
		comment = (Comment)block.getStatements().get(4);
		assertEquals("Comment 3", comment.getContent());		
		comment = (Comment)block.getStatements().get(6);
		assertEquals("Comment 4", comment.getContent());		
	}
	
	public void testCharactersBlockTaxaOrderPreservation() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN CHARACTERS;\nDIMENSIONS NCHAR=3;\nFORMAT DATATYPE=CONTINUOUS;\nMATRIX\nTaxa_1\t0.5 0.6 0.7\nTaxfffa_2\t0.3 0.4 0.5\nTaxa_3\t0.8 0.9 0.2E-1;\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		CharactersBlock block = (CharactersBlock)fileData.getBlocks().get(0);
		assertEquals(CharactersBlock.CONTINUOUS, block.getDataType());
		assertEquals(3, block.getCharacterLabels().size());
		assertEquals(3, block.getTaxaLabels().size());
		assertEquals("Taxa_1", block.getTaxaLabels().get(0));
		assertEquals("Taxfffa_2", block.getTaxaLabels().get(1));
		assertEquals("Taxa_3", block.getTaxaLabels().get(2));
	}
	
	public void testCharacterBlockWithUnknownValue() throws Throwable {
		FileData fileData = nexusParser.parse("#NEXUS\nBEGIN CHARACTERS;\nDIMENSIONS NCHAR=3;\nFORMAT DATATYPE=CONTINUOUS;\nMATRIX\nTaxa_1\t0.5 0.6 0.7\nTaxa_2\t0.3 ? 0.5\nTaxa_3\t0.8 0.9 0.2E-1;\nEND;");
		assertEquals(1, fileData.getBlocks().size());
		CharactersBlock block = (CharactersBlock)fileData.getBlocks().get(0);
		assertEquals(CharactersBlock.CONTINUOUS, block.getDataType());
		assertEquals(3, block.getCharacterLabels().size());		
		assertEquals(3, block.getCharacterMatrix().get("Taxa_1").size());
		assertEquals(3, block.getCharacterMatrix().get("Taxa_2").size());
		assertEquals(3, block.getCharacterMatrix().get("Taxa_3").size());
		assertEquals(0.5, block.getCharacterMatrix().get("Taxa_1").get(0));
		assertEquals(0.6, block.getCharacterMatrix().get("Taxa_1").get(1));
		assertEquals(0.7, block.getCharacterMatrix().get("Taxa_1").get(2));
		assertEquals(0.3, block.getCharacterMatrix().get("Taxa_2").get(0));
		assertEquals("?", block.getCharacterMatrix().get("Taxa_2").get(1));
		assertEquals(0.5, block.getCharacterMatrix().get("Taxa_2").get(2));
		assertEquals(0.8, block.getCharacterMatrix().get("Taxa_3").get(0));
		assertEquals(0.9, block.getCharacterMatrix().get("Taxa_3").get(1));
		assertEquals(0.02, block.getCharacterMatrix().get("Taxa_3").get(2));
	}
	
	public void testExtraSemicolonAtBlockLevel() throws IOException {
		try {
			nexusParser.parse("#NEXUS\n\n;\nBEGIN TAXA;\nEND;\n");
			fail("Extra semicolon in file should not parse");
		} catch (ParserException e) {
			// This should happen
		}
	}

	public void testMissingLastSemicolon() throws IOException {
		try {
			nexusParser.parse("#NEXUS\n\n        DIMENSIONS  NChar=3 NTax=4;\n        FORMAT DataType=CONTINUOUS;\n        CHARLABELS a b c;\n        MATRIX\n                fish   -0.5565 0.1813  0.5321\n                frog    2.5552  0.1814  -0.1971\n                snake   2.4797  0.1815  -1.3068\n                mouse   2.5262 0.20815  -1.1068\n[\n        this is invalid because the final statement\n        of this block does not have a semicolon as\n        a terminating character.\n]\n");
			fail("Missing semicolon on last statement should not parse");
		} catch (ParserException e) {
			// This should happen
		}
	}
	
	public void testNullCharacter() throws IOException, ParserException {
		FileData fileData = nexusParser.parse("#NEXUS\n\n[Testing null inside \u0000 comment]\n\nBEGIN TAXA;\nEND;\n");
		assertEquals(1, fileData.getBlocks().size());
		assertEquals(1, fileData.getProvenance().size());
		assertEquals("Testing null inside  comment", fileData.getProvenance().get(0));
	}

	public void testNewickParsedAsNexus() throws IOException {
		try {
			nexusParser.parse("(A:0.3,B:0.5):0.2538;");
			fail("Newick file shouldn't parse as a Nexus file (?!)");
		} catch (ParserException e) {
			// This should happen
		}
	}
	
	public void testFormatSubcommands() throws IOException, ParserException {
	    String[] files = new String[] { "acer.nex", "arctostap.nex", "aquilegia.nex" };
	    for (String file : files) {
	        InputStream stream = getClass().getClassLoader().getResourceAsStream(file);
	        nexusParser.parse(IOUtils.toString(stream));
	    }
	}
}
