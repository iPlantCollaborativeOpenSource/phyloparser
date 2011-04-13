package org.iplantc.phyloparser.parser.antlr;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RuleReturnScope;
import org.antlr.runtime.tree.Tree;

import junit.framework.TestCase;

public class TestPhylipSequenceParser extends TestCase {
	public void testBasePhylip() throws Throwable {
		Tree tree = antlrTreeFromPhylip("1 1\nMyTaxa    A\n");
		assertEquals("NTAX", tree.getChild(0).getText());
		assertEquals("1", tree.getChild(0).getChild(0).getText());
		assertEquals("NCHAR", tree.getChild(1).getText());
		assertEquals("1", tree.getChild(1).getChild(0).getText());
		assertEquals("SEQUENCES", tree.getChild(2).getText());
		assertEquals("SEQUENCE", tree.getChild(2).getChild(0).getText());
		assertEquals("MyTaxa", tree.getChild(2).getChild(0).getChild(0).getText());
		assertEquals("A", tree.getChild(2).getChild(0).getChild(1).getText());
	}
	
	public void testHandlingGarbageAtEndOfFirstLine() throws Throwable {
		Tree tree = antlrTreeFromPhylip("1 1 BAD BAD bad BAD 111 bad!!!\nMyTaxa    A\n");
		assertEquals("NTAX", tree.getChild(0).getText());
		assertEquals("1", tree.getChild(0).getChild(0).getText());
		assertEquals("NCHAR", tree.getChild(1).getText());
		assertEquals("1", tree.getChild(1).getChild(0).getText());
		assertEquals("SEQUENCES", tree.getChild(2).getText());
		assertEquals("SEQUENCE", tree.getChild(2).getChild(0).getText());
		assertEquals("MyTaxa", tree.getChild(2).getChild(0).getChild(0).getText());
		assertEquals("A", tree.getChild(2).getChild(0).getChild(1).getText());		
	}
	
	public void testLowerCaseSequenceChars() throws Throwable {
		Tree tree = antlrTreeFromPhylip("1 1\nMyTaxa    a\n");
		assertEquals("NTAX", tree.getChild(0).getText());
		assertEquals("1", tree.getChild(0).getChild(0).getText());
		assertEquals("NCHAR", tree.getChild(1).getText());
		assertEquals("1", tree.getChild(1).getChild(0).getText());
		assertEquals("SEQUENCES", tree.getChild(2).getText());
		assertEquals("SEQUENCE", tree.getChild(2).getChild(0).getText());
		assertEquals("MyTaxa", tree.getChild(2).getChild(0).getChild(0).getText());
		assertEquals("a", tree.getChild(2).getChild(0).getChild(1).getText());		
	}
	
	public void testSpacesInSequenceString() throws Throwable {
		Tree tree = antlrTreeFromPhylip("1 2\nMyTaxa    A B\n");
		assertEquals("NTAX", tree.getChild(0).getText());
		assertEquals("1", tree.getChild(0).getChild(0).getText());
		assertEquals("NCHAR", tree.getChild(1).getText());
		assertEquals("2", tree.getChild(1).getChild(0).getText());
		assertEquals("SEQUENCES", tree.getChild(2).getText());
		assertEquals("SEQUENCE", tree.getChild(2).getChild(0).getText());
		assertEquals("MyTaxa", tree.getChild(2).getChild(0).getChild(0).getText());
		assertEquals("A", tree.getChild(2).getChild(0).getChild(1).getText());		
		assertEquals("B", tree.getChild(2).getChild(0).getChild(2).getText());		
	}
	
	public void testTenCharacterTaxonParsing() throws Throwable {
		Tree tree = antlrTreeFromPhylip("1 1\nMyTaxaTen1A\n");
		assertEquals("NTAX", tree.getChild(0).getText());
		assertEquals("1", tree.getChild(0).getChild(0).getText());
		assertEquals("NCHAR", tree.getChild(1).getText());
		assertEquals("1", tree.getChild(1).getChild(0).getText());
		assertEquals("SEQUENCES", tree.getChild(2).getText());
		assertEquals("SEQUENCE", tree.getChild(2).getChild(0).getText());
		assertEquals("MyTaxaTen1", tree.getChild(2).getChild(0).getChild(0).getText());
		assertEquals("A", tree.getChild(2).getChild(0).getChild(1).getText());		

		tree = antlrTreeFromPhylip("1 1\nMyTaxaTen1 A\n");
		assertEquals("NTAX", tree.getChild(0).getText());
		assertEquals("1", tree.getChild(0).getChild(0).getText());
		assertEquals("NCHAR", tree.getChild(1).getText());
		assertEquals("1", tree.getChild(1).getChild(0).getText());
		assertEquals("SEQUENCES", tree.getChild(2).getText());
		assertEquals("SEQUENCE", tree.getChild(2).getChild(0).getText());
		assertEquals("MyTaxaTen1", tree.getChild(2).getChild(0).getChild(0).getText());
		assertEquals("A", tree.getChild(2).getChild(0).getChild(1).getText());		
	}
	
	public void testInterleavedFile() throws Throwable {
		Tree tree = antlrTreeFromPhylip("1 2\nMyTaxa    A\n\nB\n");
		assertEquals("NTAX", tree.getChild(0).getText());
		assertEquals("1", tree.getChild(0).getChild(0).getText());
		assertEquals("NCHAR", tree.getChild(1).getText());
		assertEquals("2", tree.getChild(1).getChild(0).getText());
		assertEquals("SEQUENCES", tree.getChild(2).getText());
		assertEquals("SEQUENCE", tree.getChild(2).getChild(0).getText());
		assertEquals("MyTaxa", tree.getChild(2).getChild(0).getChild(0).getText());
		assertEquals("A", tree.getChild(2).getChild(0).getChild(1).getText());		
		assertEquals("INTERLEAVE", tree.getChild(2).getChild(1).getText());
		assertEquals("B", tree.getChild(2).getChild(1).getChild(0).getText());
	}
	
	public void testTooLongTaxon() throws Throwable {
		Tree tree = antlrTreeFromPhylip("1 4\nMyTaxonIsTooLong ABCD\n");
		assertEquals("NTAX", tree.getChild(0).getText());
		assertEquals("1", tree.getChild(0).getChild(0).getText());
		assertEquals("NCHAR", tree.getChild(1).getText());
		assertEquals("4", tree.getChild(1).getChild(0).getText());
		assertEquals("SEQUENCES", tree.getChild(2).getText());
		assertEquals("SEQUENCE", tree.getChild(2).getChild(0).getText());
		assertEquals("MyTaxonIsT", tree.getChild(2).getChild(0).getChild(0).getText());
		assertEquals("o", tree.getChild(2).getChild(0).getChild(1).getText());
		assertEquals("o", tree.getChild(2).getChild(0).getChild(2).getText());
		assertEquals("L", tree.getChild(2).getChild(0).getChild(3).getText());
		assertEquals("o", tree.getChild(2).getChild(0).getChild(4).getText());
		assertEquals("n", tree.getChild(2).getChild(0).getChild(5).getText());
		assertEquals("g", tree.getChild(2).getChild(0).getChild(6).getText());
		assertEquals("A", tree.getChild(2).getChild(0).getChild(7).getText());
		assertEquals("B", tree.getChild(2).getChild(0).getChild(8).getText());
		assertEquals("C", tree.getChild(2).getChild(0).getChild(9).getText());
		assertEquals("D", tree.getChild(2).getChild(0).getChild(10).getText());
	}
	
	public void testIndentedInterleave() throws Throwable {
		Tree tree = antlrTreeFromPhylip("1 2\nMyTaxa    A\n\n          B\n");
		assertEquals("NTAX", tree.getChild(0).getText());
		assertEquals("1", tree.getChild(0).getChild(0).getText());
		assertEquals("NCHAR", tree.getChild(1).getText());
		assertEquals("2", tree.getChild(1).getChild(0).getText());
		assertEquals("SEQUENCES", tree.getChild(2).getText());
		assertEquals("SEQUENCE", tree.getChild(2).getChild(0).getText());
		assertEquals("MyTaxa", tree.getChild(2).getChild(0).getChild(0).getText());
		assertEquals("A", tree.getChild(2).getChild(0).getChild(1).getText());		
		assertEquals("INTERLEAVE", tree.getChild(2).getChild(1).getText());
		assertEquals("B", tree.getChild(2).getChild(1).getChild(0).getText());		
	}
	
	public void testSpaceBeforeNtax() throws Throwable {
		Tree tree = antlrTreeFromPhylip(" 1 1\nMyTaxa    A\n");
		assertEquals("NTAX", tree.getChild(0).getText());
		assertEquals("1", tree.getChild(0).getChild(0).getText());
		assertEquals("NCHAR", tree.getChild(1).getText());
		assertEquals("1", tree.getChild(1).getChild(0).getText());
		assertEquals("SEQUENCES", tree.getChild(2).getText());
		assertEquals("SEQUENCE", tree.getChild(2).getChild(0).getText());
		assertEquals("MyTaxa", tree.getChild(2).getChild(0).getChild(0).getText());
		assertEquals("A", tree.getChild(2).getChild(0).getChild(1).getText());		
	}

// We aren't going to support this right now.  The insanity must stop somewhere.
/*	public void testSplitSequentialData() throws Throwable {
		Tree tree = antlrTreeFromPhylip("1 10\nMyTaxa    ABCDE\nFGHIJ\n");
		assertEquals("NTAX", tree.getChild(0).getText());
		assertEquals("1", tree.getChild(0).getChild(0).getText());
		assertEquals("NCHAR", tree.getChild(1).getText());
		assertEquals("1", tree.getChild(1).getChild(0).getText());
		assertEquals("SEQUENCES", tree.getChild(2).getText());
		assertEquals("SEQUENCE", tree.getChild(2).getChild(0).getText());
		assertEquals("MyTaxa", tree.getChild(2).getChild(0).getChild(0).getText());
		assertEquals("A", tree.getChild(2).getChild(0).getChild(1).getText());				
		assertEquals("B", tree.getChild(2).getChild(0).getChild(2).getText());				
		assertEquals("C", tree.getChild(2).getChild(0).getChild(3).getText());				
		assertEquals("D", tree.getChild(2).getChild(0).getChild(4).getText());				
		assertEquals("E", tree.getChild(2).getChild(0).getChild(5).getText());				
		assertEquals("F", tree.getChild(2).getChild(0).getChild(6).getText());				
		assertEquals("G", tree.getChild(2).getChild(0).getChild(7).getText());				
		assertEquals("H", tree.getChild(2).getChild(0).getChild(8).getText());				
		assertEquals("I", tree.getChild(2).getChild(0).getChild(9).getText());				
		assertEquals("J", tree.getChild(2).getChild(0).getChild(10).getText());				
	}*/
	
	public void testDigitsIgnoredAfterFirstLine() throws Throwable {
		Tree tree = antlrTreeFromPhylip("1 10\nMyTaxa    ABCDE\n\n6 7 FGHIJ\n");		
		assertEquals("NTAX", tree.getChild(0).getText());
		assertEquals("1", tree.getChild(0).getChild(0).getText());
		assertEquals("NCHAR", tree.getChild(1).getText());
		assertEquals("10", tree.getChild(1).getChild(0).getText());
		assertEquals("SEQUENCES", tree.getChild(2).getText());
		assertEquals("SEQUENCE", tree.getChild(2).getChild(0).getText());
		assertEquals("MyTaxa", tree.getChild(2).getChild(0).getChild(0).getText());
		assertEquals("A", tree.getChild(2).getChild(0).getChild(1).getText());		
		assertEquals("B", tree.getChild(2).getChild(0).getChild(2).getText());		
		assertEquals("C", tree.getChild(2).getChild(0).getChild(3).getText());		
		assertEquals("D", tree.getChild(2).getChild(0).getChild(4).getText());		
		assertEquals("E", tree.getChild(2).getChild(0).getChild(5).getText());		
		assertEquals("INTERLEAVE", tree.getChild(2).getChild(1).getText());
		assertEquals("F", tree.getChild(2).getChild(1).getChild(0).getText());
		assertEquals("G", tree.getChild(2).getChild(1).getChild(1).getText());
		assertEquals("H", tree.getChild(2).getChild(1).getChild(2).getText());
		assertEquals("I", tree.getChild(2).getChild(1).getChild(3).getText());
		assertEquals("J", tree.getChild(2).getChild(1).getChild(4).getText());
	}
	
	private Tree antlrTreeFromPhylip(String phylip) throws RecognitionException {
		CharStream cs = new ANTLRStringStream(phylip);
        PhylipSequenceLexer lexer = new PhylipSequenceLexer(cs);
        CommonTokenStream tokens = new CommonTokenStream();
        tokens.setTokenSource(lexer);
        PhylipSequenceParser parser = new PhylipSequenceParser(tokens);
        RuleReturnScope result = parser.phylip();
        if (lexer.getNumberOfSyntaxErrors() + parser.getNumberOfSyntaxErrors() > 0) {
        	throw new RecognitionException();
        }
		Tree tree = (Tree)result.getTree();
		return tree;
	}
}
