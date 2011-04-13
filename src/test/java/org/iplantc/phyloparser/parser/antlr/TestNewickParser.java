package org.iplantc.phyloparser.parser.antlr;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RuleReturnScope;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;

import junit.framework.TestCase;

public class TestNewickParser extends TestCase {
	public void testOneTaxa() throws Throwable {
		Tree trees = antlrTreeFromNewick("A;");
		Tree tree = trees.getChild(0).getChild(0);
		assertEquals("A", tree.getText());
		assertEquals(0, tree.getChildCount());
	}
	
	public void testTwoTaxa() throws Throwable {
		Tree trees = antlrTreeFromNewick("(A,B);");
		Tree tree = trees.getChild(0).getChild(0);
		assertEquals("ANON", tree.getText());
		assertEquals("A", tree.getChild(0).getText());
		assertEquals("B", tree.getChild(1).getText());		
	}
	
	public void testWithBranchLengths() throws Throwable {
		Tree trees = antlrTreeFromNewick("(A:0.3,B:0.5):0.2538;");
		Tree tree = trees.getChild(0).getChild(0);
		assertEquals("ANON", tree.getText());
		assertEquals("LENGTH", tree.getChild(0).getText());		
		assertEquals("0.2538", tree.getChild(0).getChild(0).getText());
		assertEquals("A", tree.getChild(1).getText());
		assertEquals("LENGTH", tree.getChild(1).getChild(0).getText());
		assertEquals("0.3", tree.getChild(1).getChild(0).getChild(0).getText());
		assertEquals("B", tree.getChild(2).getText());		
		assertEquals("LENGTH", tree.getChild(2).getChild(0).getText());
		assertEquals("0.5", tree.getChild(2).getChild(0).getChild(0).getText());
	}

	public void testWithInternalNodeLabel() throws Throwable {
		Tree trees = antlrTreeFromNewick("(A,B)C;");
		Tree tree = trees.getChild(0).getChild(0);
		assertEquals("C", tree.getText());
		assertEquals("A", tree.getChild(0).getText());
		assertEquals("B", tree.getChild(1).getText());		
	}

	public void testQuotedLabels() throws Throwable {
		Tree trees = antlrTreeFromNewick("(A,'B C')'D''s thing';");
		Tree tree = trees.getChild(0).getChild(0);
		assertEquals("'D''s thing'", tree.getText());
		assertEquals("A", tree.getChild(0).getText());
		assertEquals("'B C'", tree.getChild(1).getText());		
	}
	
	public void testMultipleTrees() throws Throwable {
		Tree trees = antlrTreeFromNewick("(A,B);(C,D);\n(E,F,G);\n");
		assertEquals("TREES", trees.getText());
		assertEquals(3, trees.getChildCount());

		Tree tree1 = trees.getChild(0).getChild(0);
		Tree tree2 = trees.getChild(1).getChild(0);
		Tree tree3 = trees.getChild(2).getChild(0);
		assertEquals("ANON", tree1.getText());
		assertEquals("A", tree1.getChild(0).getText());
		assertEquals("B", tree1.getChild(1).getText());
		assertEquals("ANON", tree2.getText());
		assertEquals("C", tree2.getChild(0).getText());
		assertEquals("D", tree2.getChild(1).getText());
		assertEquals("ANON", tree3.getText());
		assertEquals("E", tree3.getChild(0).getText());
		assertEquals("F", tree3.getChild(1).getText());
		assertEquals("G", tree3.getChild(2).getText());
	}
	
	public void testTreeWithNoLabels() throws Throwable {
		Tree trees = antlrTreeFromNewick("(,,(,));\n");
		assertEquals("TREES", trees.getText());
		assertEquals(1, trees.getChildCount());
		
		Tree tree = trees.getChild(0).getChild(0);
		assertEquals("ANON", tree.getText());
		assertEquals("ANON", tree.getChild(0).getText());
		assertEquals("ANON", tree.getChild(1).getText());		
		assertEquals("ANON", tree.getChild(2).getText());		
		assertEquals("ANON", tree.getChild(2).getChild(0).getText());		
		assertEquals("ANON", tree.getChild(2).getChild(1).getText());		
	}
	
	public void testTreeWithOnlyDistances() throws Throwable {
		Tree trees = antlrTreeFromNewick("(:0.1,:0.2,(:0.3,:0.4):0.5):0.0;\n");
		
		assertEquals("TREES", trees.getText());
		assertEquals(1, trees.getChildCount());

		Tree tree = trees.getChild(0).getChild(0);
		assertEquals("ANON", tree.getText());
		assertEquals("LENGTH", tree.getChild(0).getText());
		assertEquals("0.0", tree.getChild(0).getChild(0).getText());
		assertEquals("ANON", tree.getChild(1).getText());
		assertEquals("LENGTH", tree.getChild(1).getChild(0).getText());
		assertEquals("0.1", tree.getChild(1).getChild(0).getChild(0).getText());
		assertEquals("ANON", tree.getChild(2).getText());		
		assertEquals("LENGTH", tree.getChild(2).getChild(0).getText());
		assertEquals("0.2", tree.getChild(2).getChild(0).getChild(0).getText());
		assertEquals("ANON", tree.getChild(3).getText());	
		assertEquals("LENGTH", tree.getChild(3).getChild(0).getText());
		assertEquals("0.5", tree.getChild(3).getChild(0).getChild(0).getText());
		assertEquals("ANON", tree.getChild(3).getChild(1).getText());		
		assertEquals("LENGTH", tree.getChild(3).getChild(1).getChild(0).getText());
		assertEquals("0.3", tree.getChild(3).getChild(1).getChild(0).getChild(0).getText());
		assertEquals("ANON", tree.getChild(3).getChild(2).getText());		
		assertEquals("LENGTH", tree.getChild(3).getChild(2).getChild(0).getText());
		assertEquals("0.4", tree.getChild(3).getChild(2).getChild(0).getChild(0).getText());
	}
	
	public void testSingleNodeSubtree() throws Throwable {
		Tree trees = antlrTreeFromNewick("((()));\n");
		
		assertEquals("TREES", trees.getText());
		assertEquals(1, trees.getChildCount());

		Tree tree = trees.getChild(0).getChild(0);
		assertEquals("ANON", tree.getText());
		assertEquals("ANON", tree.getChild(0).getText());
		assertEquals("ANON", tree.getChild(0).getChild(0).getText());
		assertEquals("ANON", tree.getChild(0).getChild(0).getChild(0).getText());
	}
	
	public void testNhxAnnotation() throws Throwable {
		Tree trees = antlrTreeFromNewick("(A,B)[&&NHX:S=foo:E=1.1.1.1];");
		Tree tree = trees.getChild(0).getChild(0);
		assertEquals("ANON", tree.getText());
		assertEquals(NewickParser.NHX_ANNOTATION, tree.getChild(0).getType());
		assertEquals("[&&NHX:S=foo:E=1.1.1.1]", tree.getChild(0).getText());
		assertEquals("A", tree.getChild(1).getText());
		assertEquals("B", tree.getChild(2).getText());		
	}
	
	private Tree antlrTreeFromNewick(String newick) throws RecognitionException {
		CharStream cs = new ANTLRStringStream(newick);
        NewickLexer lexer = new NewickLexer(cs);
        CommonTokenStream tokens = new CommonTokenStream();
        tokens.setTokenSource(lexer);
        NewickParser parser = new NewickParser(tokens);
        RuleReturnScope result = parser.newick();
		Tree tree = (Tree)result.getTree();
		return tree;
	}
}
