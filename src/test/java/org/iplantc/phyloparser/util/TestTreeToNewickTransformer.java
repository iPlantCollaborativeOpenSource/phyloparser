package org.iplantc.phyloparser.util;

import org.iplantc.phyloparser.model.FileData;
import org.iplantc.phyloparser.model.Tree;
import org.iplantc.phyloparser.model.block.TreesBlock;
import org.iplantc.phyloparser.parser.NewickParser;

import junit.framework.TestCase;

public class TestTreeToNewickTransformer extends TestCase {
	private NewickParser newickParser;
	
	public void setUp() {
		newickParser = new NewickParser();
	}

	public void testBasicTrees() {
		String actual = null; 
		
		Tree a = getTree("(A);");
		assertNotNull(a);
		actual = new TreeToNewickTransformer().transform(a);
		assertEquals("(A)", actual);
		
		Tree ab = getTree("(A,B);");
		assertNotNull(ab);
		actual = new TreeToNewickTransformer().transform(ab);
		assertEquals("(A,B)", actual);
		
		Tree abc = getTree("(A,B,C);");
		assertNotNull(abc);
		actual = new TreeToNewickTransformer().transform(abc);
		assertEquals("(A,B,C)", actual);
		
		abc = getTree("(A,(B,C));");
		assertNotNull(abc);
		actual = new TreeToNewickTransformer().transform(abc);
		assertEquals("(A,(B,C))", actual);
		
		abc = getTree("(A , (    B,   C		));");
		assertNotNull(abc);
		actual = new TreeToNewickTransformer().transform(abc);
		assertEquals("(A,(B,C))", actual);
		
		abc = getTree("((A,B),C);");
		assertNotNull(abc);
		actual = new TreeToNewickTransformer().transform(abc);
		assertEquals("((A,B),C)", actual);
		
		Tree abcd = getTree("(A,B,(C,D));");
		assertNotNull(abcd);
		actual = new TreeToNewickTransformer().transform(abcd);
		assertEquals("(A,B,(C,D))", actual);
		
		Tree nonames = getTree("(,,(,));");
		assertNotNull(nonames);
		actual = new TreeToNewickTransformer().transform(nonames);
		assertEquals("(,,(,))", actual);
		
		abcd = getTree("((A,B),(C,D));");
		assertNotNull(abcd);
		actual = new TreeToNewickTransformer().transform(abcd);
		assertEquals("((A,B),(C,D))", actual);
		
		Tree abcde = getTree("(A,B,(C,D)E);");
		assertNotNull(abcde);
		actual = new TreeToNewickTransformer().transform(abcde);
		assertEquals("(A,B,(C,D)E)", actual);
		
		Tree abcdef = getTree("(A,B,(C,D)E)F;");
		assertNotNull(abcdef);
		actual = new TreeToNewickTransformer().transform(abcdef);
		assertEquals("(A,B,(C,D)E)F", actual);
	}

	public void testTreesWithBranchLengths() {
		String actual = null;
		
		Tree ab = getTree("(A:0.3,B:0.4);");
		assertNotNull(ab);
		actual = new TreeToNewickTransformer().transform(ab);
		assertEquals("(A:0.3,B:0.4)", actual);
		
		ab = getTree("(A:0.3,B:0.4):0.0;");
		assertNotNull(ab);
		actual = new TreeToNewickTransformer().transform(ab);
		assertEquals("(A:0.3,B:0.4):0.0", actual);
	
		Tree abcd = getTree("(A:0.1,B:0.2,(C:0.3,D:0.4):0.5);");
		assertNotNull(abcd);
		actual = new TreeToNewickTransformer().transform(abcd);
		assertEquals("(A:0.1,B:0.2,(C:0.3,D:0.4):0.5)", actual);
		
		Tree abcdef = getTree("(A:0.1,B:0.2,(C:0.3,D:0.4)E:0.5)F;");
		assertNotNull(abcdef);
		actual = new TreeToNewickTransformer().transform(abcdef);
		assertEquals("(A:0.1,B:0.2,(C:0.3,D:0.4)E:0.5)F", actual);
		
		abcdef = getTree("((B:0.2,(C:0.3,D:0.4)E:0.5)F:0.1)A;");
		assertNotNull(abcdef);
		actual = new TreeToNewickTransformer().transform(abcdef);
		assertEquals("((B:0.2,(C:0.3,D:0.4)E:0.5)F:0.1)A", actual);
	}
	
	public void testTreesWithQuotedLabels() {
		Tree quoted = getTree("('foo 1','bar 2');");
		assertNotNull(quoted);
		String actual = new TreeToNewickTransformer().transform(quoted);
		assertEquals("('foo 1','bar 2')", actual);
		
	}
	
	/*
	 * These trees were pulled from the APWEB2 demo dataset.  In some cases subclades were 
	 * from their published newick trees. 
	 * 
	 * @see http://svn.phylodiversity.net/tot/trees/ 
	 * 
	 * Note: this is a demo site and may no longer be available in the future
	 */
	// login info for phylodiversity/apweb2-demo username: apweb2 password: svnsvg
	public void testRealTrees() {
		String actual = null;
		Tree subcladePiperales = getTree("(hydnoraceae,lactoridaceae,(" +
				"aristolochia,asarum)aristolochiaceae);");
		assertNotNull(subcladePiperales);
		actual = new TreeToNewickTransformer().transform(subcladePiperales);
		assertEquals("(hydnoraceae,lactoridaceae,(aristolochia,asarum)aristolochiaceae)", actual);
		
		String piperalesNewick = "((hydnoraceae,lactoridaceae,(aristolochia," +
				"asarum)aristolochiaceae),((peperomia,piper)piperaceae," +
				"saururaceae))piperales;";
		Tree piperales = getTree(piperalesNewick);
		assertNotNull(piperales);
		actual = new TreeToNewickTransformer().transform(piperales);
		assertEquals(piperalesNewick.substring(0, piperalesNewick.length() - 1), actual);
		
		Tree gnetales = getTree("((gnetum,ephedra,welwitschia)[gnetales]);");
		assertNotNull(gnetales);
		actual = new TreeToNewickTransformer().transform(gnetales);
		assertEquals("((gnetum,ephedra,welwitschia))", actual);
		
		String ericalesNewick = "((balsaminaceae,(marcgraviaceae,(tetramerista," +
			"pelliciera)tetrameristaceae)marcgraviaceae_to_tetrameristaceae)" +
			"balsaminaceae_to_tetrameristaceae,((polemoniaceae,fouquieriaceae)" +
			"polemoniaceae_to_fouquieriaceae,lecythidaceae,((sladeniaceae," +
			"pentaphylacaceae)sladeniaceae_to_pentaphylacaceae,(sapotaceae," +
			"(ebenaceae,(maesaceae,(theophrastaceae,(primulaceae,myrsinaceae)" +
			"primulaceae_to_myrsinaceae)theophrastaceae_to_myrsinaceae)" +
			"maesaceae_to_myrsinaceae)ebenaceae_to_myrsinaceae)" +
			"sapotaceae_to_myrsinaceae,(mitrastemonaceae,theaceae,(symplocaceae," +
			"(styracaceae,diapensiaceae)styracaceae_to_diapensiaceae)" +
			"symplocaceae_to_diapensiaceae,(((actinidiaceae,roridulaceae)" +
			"actinidiaceae_to_roridulaceae,sarraceniaceae)" +
			"sarraceniaceae_to_actinidiaceae,(clethraceae,(cyrillaceae,ericaceae" +
			")cyrillaceae_to_ericaceae)clethraceae_to_ericaceae)" +
			"sarraceniaceae_to_ericaceae)mitrastemonaceae_to_ericaceae)" +
			"sladeniaceae_to_ericaceae)polemoniaceae_to_ericaceae)ericales;";
		Tree ericales = getTree(ericalesNewick);
		assertNotNull(ericales);
		actual = new TreeToNewickTransformer().transform(ericales);
		assertEquals(ericalesNewick.substring(0, ericalesNewick.length() - 1), actual);
		
		String fabalesNewick = "(quillajaceae,(fabaceae,(surianaceae,polygalaceae)))fabales;";
		Tree fabales = getTree(fabalesNewick);
		assertNotNull(fabales);
		actual = new TreeToNewickTransformer().transform(fabales);
		assertEquals(fabalesNewick.substring(0, fabalesNewick.length() - 1), actual);
		
		String poalesNewick = "((typhaceae,bromeliaceae),(rapateaceae,(((" +
				"xyridaceae,eriocaulaceae),(mayacaceae,(thurniaceae,(cyperaceae," +
				"juncaceae)))),((anarthriaceae,(centrolepidaceae,restionaceae))," +
				"(flagellariaceae,((joinvilleaceae,ecdeiocoleaceae),poaceae))))))" +
				"poales;";
		Tree poales = getTree(poalesNewick);
		assertNotNull(poales);
		actual = new TreeToNewickTransformer().transform(poales);
		assertEquals(poalesNewick.substring(0, poalesNewick.length() - 1), actual);
	}
	
	public void testNhxAnnotation() {
		String nhxNewick = "(A,B[&&NHX:foobarbaz=1]);";
		Tree nhx = getTree(nhxNewick);
		assertNotNull(nhx);
		String actual = new TreeToNewickTransformer().transform(nhx);
		assertEquals(nhxNewick.substring(0, nhxNewick.length() - 1), actual);		
	}
	
	private Tree getTree(String newickString) {
		Tree tree = null;
		try {
			tree = getTreeFromFileData(newickParser.parse(newickString));
		} catch (Exception e) {
			fail("Exceptions are bad.");
		}
		return tree;
	}
	
	private Tree getTreeFromFileData(FileData fileData) {
		return ((TreesBlock)fileData.getBlocks().get(0)).getTrees().get(0);
	}	
}
