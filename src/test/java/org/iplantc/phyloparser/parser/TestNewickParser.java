package org.iplantc.phyloparser.parser;

import java.io.IOException;
import java.util.Collection;

import org.iplantc.phyloparser.exception.ParserException;
import org.iplantc.phyloparser.model.Annotation;
import org.iplantc.phyloparser.model.FileData;
import org.iplantc.phyloparser.model.Node;
import org.iplantc.phyloparser.model.SourceFormat;
import org.iplantc.phyloparser.model.block.TreesBlock;

import junit.framework.TestCase;

public class TestNewickParser extends TestCase {
	private NewickParser newickParser;
	
	public void setUp() {
		newickParser = new NewickParser();
	}
	
	public void testOneTaxa() throws Throwable {
		Node tree = getRootNodeFromFileData(newickParser.parse("A;"));
		assertEquals("A", tree.getName());
		assertEquals(0, tree.getChildren().size());
	}
	
	public void testTwoTaxa() throws Throwable {
		Node tree = getRootNodeFromFileData(newickParser.parse("(A,B);"));
		assertNull(tree.getName());
		assertEquals("A", tree.getChildren().get(0).getName());
		assertEquals("B", tree.getChildren().get(1).getName());
	}
	
	public void testWithBranchLengths() throws Throwable {
		Node tree = getRootNodeFromFileData(newickParser.parse("(A:0.3,B:0.5):0.2538;"));
		assertNull(tree.getName());
		assertEquals(0.2538, tree.getBranchLength());
		assertEquals("A", tree.getChildren().get(0).getName());
		assertEquals(0.3, tree.getChildren().get(0).getBranchLength());
		assertEquals("B", tree.getChildren().get(1).getName());
		assertEquals(0.5, tree.getChildren().get(1).getBranchLength());
	}

	public void testWithInternalNodeLabel() throws Throwable {
		Node tree = getRootNodeFromFileData(newickParser.parse("(A,B)C;"));
		assertEquals("C", tree.getName());
		assertEquals("A", tree.getChildren().get(0).getName());
		assertEquals("B", tree.getChildren().get(1).getName());
		assertEquals("C", tree.getChildren().get(0).getParent().getName());
		assertEquals("C", tree.getChildren().get(1).getParent().getName());
	}

	public void testQuotedLabels() throws Throwable {
		Node tree = getRootNodeFromFileData(newickParser.parse("(A,'B C')'D''s thing';"));
		assertEquals("A", tree.getChildren().get(0).getName());
		assertEquals("B C", tree.getChildren().get(1).getName());
		assertEquals("D's thing", tree.getName());
	}
	
	public void testSourceFormat() throws Throwable {
		FileData data = newickParser.parse("A;");
		assertEquals(SourceFormat.NEWICK, data.getSourceFormat());
	}

	public void testFloatAsNodeLabel() throws Throwable {
		Node tree = getRootNodeFromFileData(newickParser.parse("(A,B)0.05;"));
		assertEquals("0.05", tree.getName());
		assertEquals("A", tree.getChildren().get(0).getName());
		assertEquals("B", tree.getChildren().get(1).getName());
		assertEquals("0.05", tree.getChildren().get(0).getParent().getName());
		assertEquals("0.05", tree.getChildren().get(1).getParent().getName());
	}

	public void testNullCharacter() throws Throwable {
		Node tree = getRootNodeFromFileData(newickParser.parse("(A,B\u0000);"));
		assertEquals("A", tree.getChildren().get(0).getName());
		assertEquals("B", tree.getChildren().get(1).getName());
	}
	
	public void testNhxAnnotations() throws Throwable {
		Node tree = getRootNodeFromFileData(newickParser.parse("(A,B)[&&NHX:S=foo:E=1.1.1.1];"));
		assertNull(tree.getName());
		assertEquals("A", tree.getChildren().get(0).getName());
		assertEquals("B", tree.getChildren().get(1).getName());
		Collection<Annotation> annotations = tree.getAnnotations();
		assertEquals(1, annotations.size());
		assertEquals("&&NHX:S=foo:E=1.1.1.1", annotations.iterator().next().getContent());
	}
	
	public void testNexusParsedAsNewick() throws IOException {
		try {
			FileData tree = newickParser.parse("#NEXUS\n\nBEGIN TAXA;\nEND;\n");
			fail("Nexus file shouldn't parse as a Newick file (?!)");
		} catch (ParserException e) {
			//assertEquals("line 3:0 missing ';' at 'BEGIN'\nline 3:6 extraneous input 'TAXA' expecting ';'\n", e.getMessage());
		}
	}
	
	private Node getRootNodeFromFileData(FileData fileData) {
		return ((TreesBlock)fileData.getBlocks().get(0)).getTrees().get(0).getRoot();
	}
}
