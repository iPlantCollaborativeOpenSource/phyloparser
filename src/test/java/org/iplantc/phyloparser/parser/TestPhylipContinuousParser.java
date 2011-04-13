package org.iplantc.phyloparser.parser;

import java.io.File;
import java.io.IOException;

import org.iplantc.phyloparser.exception.ParserException;
import org.iplantc.phyloparser.model.FileData;
import org.iplantc.phyloparser.model.block.CharactersBlock;

import junit.framework.TestCase;

public class TestPhylipContinuousParser extends TestCase {
	private PhylipContinuousParser phylipParser;
	
	public void setUp() {
		phylipParser = new PhylipContinuousParser();
	}

	public void testEmptyFile() throws IOException, ParserException {
		FileData fd = phylipParser.parse("   0 0\n");
		assertEquals(1, fd.getBlocks().size());
		CharactersBlock charsBlock = (CharactersBlock) fd.getBlocks().get(0);
		assertEquals(0, charsBlock.getCharacterLabels().size());
		assertEquals(0, charsBlock.getTaxaLabels().size());
	}
	
	public void testNoTaxa() throws IOException, ParserException {
		FileData fd = phylipParser.parse("   0 10\n");
		assertEquals(1, fd.getBlocks().size());
		CharactersBlock charsBlock = (CharactersBlock) fd.getBlocks().get(0);
		assertEquals(10, charsBlock.getCharacterLabels().size());
		assertEquals(0, charsBlock.getTaxaLabels().size());		
	}
	
	public void testOneTaxaOneCharacter() throws IOException, ParserException {
		FileData fd = phylipParser.parse("   1 1\nTaxa_1                        0.01\n");
		assertEquals(1, fd.getBlocks().size());
		CharactersBlock charsBlock = (CharactersBlock) fd.getBlocks().get(0);
		assertEquals(1, charsBlock.getCharacterLabels().size());
		assertEquals(1, charsBlock.getTaxaLabels().size());
		assertEquals("Taxa_1", charsBlock.getTaxaLabels().get(0));
		assertEquals(0.01, charsBlock.getCharacterMatrix().get("Taxa_1").get(0));
	}
	
	public void testMultipleTaxaMultipleCharacters() throws IOException, ParserException {
		FileData fd = phylipParser.parse("   3 3\nTaxa_1                        0.01\t0.02\t0.03\nTaxa_2                        0.04\t0.05\t0.06\nTaxa_3                        0.07\t0.08\t0.09\n");
		assertEquals(1, fd.getBlocks().size());
		CharactersBlock charsBlock = (CharactersBlock) fd.getBlocks().get(0);
		assertEquals(3, charsBlock.getCharacterLabels().size());
		assertEquals(3, charsBlock.getTaxaLabels().size());
		assertEquals("Taxa_1", charsBlock.getTaxaLabels().get(0));
		assertEquals("Taxa_2", charsBlock.getTaxaLabels().get(1));
		assertEquals("Taxa_3", charsBlock.getTaxaLabels().get(2));
		assertEquals(0.01, charsBlock.getCharacterMatrix().get("Taxa_1").get(0));		
		assertEquals(0.02, charsBlock.getCharacterMatrix().get("Taxa_1").get(1));		
		assertEquals(0.03, charsBlock.getCharacterMatrix().get("Taxa_1").get(2));		
		assertEquals(0.04, charsBlock.getCharacterMatrix().get("Taxa_2").get(0));		
		assertEquals(0.05, charsBlock.getCharacterMatrix().get("Taxa_2").get(1));		
		assertEquals(0.06, charsBlock.getCharacterMatrix().get("Taxa_2").get(2));		
		assertEquals(0.07, charsBlock.getCharacterMatrix().get("Taxa_3").get(0));		
		assertEquals(0.08, charsBlock.getCharacterMatrix().get("Taxa_3").get(1));		
		assertEquals(0.09, charsBlock.getCharacterMatrix().get("Taxa_3").get(2));		
	}
	
	public void testTaxaCountMismatch() throws IOException {
		try {
			phylipParser.parse("   2 1\nTaxa_1                        0.01\n");
			fail("Successfully parsed Phylip file with taxa count mismatch between header and body");
		} catch (ParserException pe) {
			// This should happen
		}
		try {
			phylipParser.parse("   1 1\nTaxa_1                        0.01\nTaxa_2                        0.01\n");
			fail("Successfully parsed Phylip file with taxa count mismatch between header and body");
		} catch (ParserException pe) {
			// This should happen
		}
	}

	public void testCharCountMismatch() throws IOException {
		try {
			phylipParser.parse("   3 3\nTaxa_1                        0.01\t0.02\t0.03\nTaxa_2                        0.04\t0.05\nTaxa_3                        0.07\t0.08\t0.09\n");
			fail("Successfully parsed Phylip file with character count mismatch between header and body");
		} catch (ParserException pe) {
			// This should happen
		}
		try {
			phylipParser.parse("   3 3\nTaxa_1                        0.01\t0.02\t0.03\nTaxa_2                        0.04\t0.05\t0.06\t0.07\nTaxa_3                        0.07\t0.08\t0.09\n");
			fail("Successfully parsed Phylip file with character count mismatch between header and body");
		} catch (ParserException pe) {
			// This should happen
		}
	}

	public void testCharacterAcceptance() throws IOException, ParserException {
		FileData fd = phylipParser.parse("   19 5\n" +
				"Calidris_bairdii\\             39.3      39.7    9.6     4       2\n" +
				"Calidris_canutus/             126       148     19.3    3.7     2\n" +
				"Calidris_maritima?            67.6      76.3    13.3    3.9     2\n" +
				"Calidris_mauri@               28        31      7.5     3.9     2\n" +
				"Calidris_minuta#              24        27.1    6.3     3.8     1\n" +
				"Calidris_minutilla$           20.3      22.2    6.4     3.9     2\n" +
				"Calidris_ptilocnemis%         76.3      83      14.2    4       2\n" +
				"Calidris_pusilla^             25        27      6.9     4       2\n" +
				"Calidris_ruficollis&          25.7      26.6    8.3     4       2\n" +
				"Calidris_subminuta*           29        32      7.5     4       2\n" +
				"Calidris_temminckii-          24.3      27.8    5.8     4       1\n" +
				"Calidris_tenuirostris+        156       174     22      4       2\n" +
				"Catoptrophorus_semipalmatus|  273       301.4   39.5    4       2\n" +
				"Charadrius_dubius\"            38.3      39.2    7.7     3.9     2\n" +
				"Charadrius_hiaticula'         63.5      64.7    10.9    3.8     2\n" +
				"Charadrius_melodus<           54.9      55.6    9.4     3.3     2\n" +
				"Charadrius_montanus>          102       114     16.5    3       1\n" +
				"Charadrius_vociferus.         92.1      101     14.5    4       2\n" +
				"Charadrius_wilsonia!          59       63      12.4    3       2\n");
		assertEquals(1, fd.getBlocks().size());
		CharactersBlock charsBlock = (CharactersBlock) fd.getBlocks().get(0);
		assertEquals(5, charsBlock.getCharacterLabels().size());
		assertEquals(19, charsBlock.getTaxaLabels().size());
		assertEquals("Calidris_bairdii\\", charsBlock.getTaxaLabels().get(0));
		assertEquals("Calidris_canutus/", charsBlock.getTaxaLabels().get(1));
		assertEquals("Calidris_maritima?", charsBlock.getTaxaLabels().get(2));
		assertEquals("Calidris_mauri@", charsBlock.getTaxaLabels().get(3));
		assertEquals("Calidris_minuta#", charsBlock.getTaxaLabels().get(4));
		assertEquals("Calidris_minutilla$", charsBlock.getTaxaLabels().get(5));
		assertEquals("Calidris_ptilocnemis%", charsBlock.getTaxaLabels().get(6));
		assertEquals("Calidris_pusilla^", charsBlock.getTaxaLabels().get(7));
		assertEquals("Calidris_ruficollis&", charsBlock.getTaxaLabels().get(8));
		assertEquals("Calidris_subminuta*", charsBlock.getTaxaLabels().get(9));
		assertEquals("Calidris_temminckii-", charsBlock.getTaxaLabels().get(10));
		assertEquals("Calidris_tenuirostris+", charsBlock.getTaxaLabels().get(11));
		assertEquals("Catoptrophorus_semipalmatus|", charsBlock.getTaxaLabels().get(12));
		assertEquals("Charadrius_dubius\"", charsBlock.getTaxaLabels().get(13));
		assertEquals("Charadrius_hiaticula'", charsBlock.getTaxaLabels().get(14));
		assertEquals("Charadrius_melodus<", charsBlock.getTaxaLabels().get(15));
		assertEquals("Charadrius_montanus>", charsBlock.getTaxaLabels().get(16));
		assertEquals("Charadrius_vociferus.", charsBlock.getTaxaLabels().get(17));
		assertEquals("Charadrius_wilsonia!", charsBlock.getTaxaLabels().get(18));
	}
	
	public void testCharacterFailure() throws IOException {
		try {
			phylipParser.parse("   1 5\n" +
					"Actophilornis_africanus[]     39.3      39.7    9.6     4       2\n");
			fail("File with unacceptable taxa characters was accepted");
		} catch (ParserException pe) {
			// This should happen
		}
		try {
			phylipParser.parse("   1 5\n" +
					"Aphriza_virgata()             126       148     19.3    3.7     2\n");
			fail("File with unacceptable taxa characters was accepted");
		} catch (ParserException pe) {
			// This should happen
		}
		try {
			phylipParser.parse("   1 5\n" +
					"Arenaria_interpres:           67.6      76.3    13.3    3.9     2\n");
			fail("File with unacceptable taxa characters was accepted");
		} catch (ParserException pe) {
			// This should happen
		}
		try {
			phylipParser.parse("   1 5\n" +
					"Arenaria_melanocephala;       28        31      7.5     3.9     2\n");
			fail("File with unacceptable taxa characters was accepted");
		} catch (ParserException pe) {
			// This should happen
		}
		try {
			phylipParser.parse("   1 5\n" +
					"Bartramia_longicauda,         24        27.1    6.3     3.8     1\n");
			fail("File with unacceptable taxa characters was accepted");
		} catch (ParserException pe) {
			// This should happen
		}
	}
	
	public void testTaxaNameTooLong() throws IOException {
		try {
			phylipParser.parse("   1 1\n" +
					"This_is_too_long_of_a_taxa_name0.1\n");
			fail("File with 31-character taxa name was accepted");
		} catch (ParserException pe) {
			// This should happen
		}		
	}

	public void testBadPadding() throws IOException {
		try {
			phylipParser.parse("   1 5\n" +
					"Actophilornis_africanus        39.3      39.7    9.6     4       2\n");
			fail("File with 31-character taxa padding was accepted");
		} catch (ParserException pe) {
			// This should happen
		}		
		try {
			phylipParser.parse("   1 5\n" +
					"Actophilornis_africanus      39.3      39.7    9.6     4       2\n");
			fail("File with 29-character taxa padding was accepted");
		} catch (ParserException pe) {
			// This should happen
		}		
	}
	
	public void test30CharacterTaxaName() throws IOException, ParserException {
		FileData fd = phylipParser.parse("   1 1\nThis_taxa_name_is_not_too_long0.01\n");
		assertEquals(1, fd.getBlocks().size());
		CharactersBlock charsBlock = (CharactersBlock) fd.getBlocks().get(0);
		assertEquals(1, charsBlock.getCharacterLabels().size());
		assertEquals(1, charsBlock.getTaxaLabels().size());
		assertEquals("This_taxa_name_is_not_too_long", charsBlock.getTaxaLabels().get(0));
		assertEquals(0.01, charsBlock.getCharacterMatrix().get("This_taxa_name_is_not_too_long").get(0));
	}
}
