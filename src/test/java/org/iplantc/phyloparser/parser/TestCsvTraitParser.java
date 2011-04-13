package org.iplantc.phyloparser.parser;

import org.iplantc.phyloparser.exception.ParserException;
import org.iplantc.phyloparser.model.FileData;
import org.iplantc.phyloparser.model.SourceFormat;
import org.iplantc.phyloparser.model.block.CharactersBlock;

import junit.framework.TestCase;

public class TestCsvTraitParser extends TestCase {
	private CsvTraitParser csvTraitParser;
	
	public void setUp() {
		csvTraitParser = new CsvTraitParser();
	}

	public void testOneTaxonOneCharacter() throws Throwable {
		FileData fileData = csvTraitParser.parse(",size_of_head\rTaxa_1,0.5");
		assertEquals(SourceFormat.CSV_TRAIT, fileData.getSourceFormat());
		assertEquals(1, fileData.getBlocks().size());
		CharactersBlock block = (CharactersBlock)fileData.getBlocks().get(0);
		assertEquals(CharactersBlock.CONTINUOUS, block.getDataType());
		assertEquals(1, block.getCharacterLabels().size());
		assertEquals("size_of_head", block.getCharacterLabels().get(0));
		assertEquals(1, block.getCharacterMatrix().get("Taxa_1").size());
		assertEquals(0.5, block.getCharacterMatrix().get("Taxa_1").get(0));
	}

	public void testMultipleTaxaMultipleCharacters() throws Throwable {	
		FileData fileData = csvTraitParser.parse(",size_of_head,size_of_neck,size_of_belly_button\rTaxa_1,0.5,0.6,0.7\rTaxa_2,0.1,0.2,0.3\rTaxa_3,0.3,0.2,0.1");
		assertEquals(SourceFormat.CSV_TRAIT, fileData.getSourceFormat());
		assertEquals(1, fileData.getBlocks().size());
		CharactersBlock block = (CharactersBlock)fileData.getBlocks().get(0);
		assertEquals(CharactersBlock.CONTINUOUS, block.getDataType());
		assertEquals(3, block.getCharacterLabels().size());		
		assertEquals("size_of_head", block.getCharacterLabels().get(0));
		assertEquals("size_of_neck", block.getCharacterLabels().get(1));
		assertEquals("size_of_belly_button", block.getCharacterLabels().get(2));
		assertEquals(3, block.getCharacterMatrix().get("Taxa_1").size());
		assertEquals(0.5, block.getCharacterMatrix().get("Taxa_1").get(0));
		assertEquals(0.6, block.getCharacterMatrix().get("Taxa_1").get(1));
		assertEquals(0.7, block.getCharacterMatrix().get("Taxa_1").get(2));
		assertEquals(3, block.getCharacterMatrix().get("Taxa_2").size());
		assertEquals(0.1, block.getCharacterMatrix().get("Taxa_2").get(0));
		assertEquals(0.2, block.getCharacterMatrix().get("Taxa_2").get(1));
		assertEquals(0.3, block.getCharacterMatrix().get("Taxa_2").get(2));
		assertEquals(3, block.getCharacterMatrix().get("Taxa_3").size());
		assertEquals(0.3, block.getCharacterMatrix().get("Taxa_3").get(0));
		assertEquals(0.2, block.getCharacterMatrix().get("Taxa_3").get(1));
		assertEquals(0.1, block.getCharacterMatrix().get("Taxa_3").get(2));
	}
	
	public void testCommaInNames() throws Throwable {
		FileData fileData = csvTraitParser.parse(",\"size of head, second\"\r\"Taxa 1, older\",0.5");
		assertEquals(SourceFormat.CSV_TRAIT, fileData.getSourceFormat());
		assertEquals(1, fileData.getBlocks().size());
		CharactersBlock block = (CharactersBlock)fileData.getBlocks().get(0);
		assertEquals(CharactersBlock.CONTINUOUS, block.getDataType());
		assertEquals(1, block.getCharacterLabels().size());
		assertEquals("size of head, second", block.getCharacterLabels().get(0));
		assertEquals(1, block.getCharacterMatrix().get("Taxa 1, older").size());
		assertEquals(0.5, block.getCharacterMatrix().get("Taxa 1, older").get(0));		
	}
	
	public void testBadCsv() throws Throwable {
		try {
			csvTraitParser.parse("This is very clearly not a CSV file!\n");
			fail("Parsed bad CSV file successfully");
		} catch (ParserException pe) {
			
		}
	}

	public void testDifferentLineEndings() throws Throwable {
		FileData fileData = csvTraitParser.parse(",size_of_head\nTaxa_1,0.5");
		assertEquals(SourceFormat.CSV_TRAIT, fileData.getSourceFormat());
		assertEquals(1, fileData.getBlocks().size());
		CharactersBlock block = (CharactersBlock)fileData.getBlocks().get(0);
		assertEquals(CharactersBlock.CONTINUOUS, block.getDataType());
		assertEquals(1, block.getCharacterLabels().size());
		assertEquals("size_of_head", block.getCharacterLabels().get(0));
		assertEquals(1, block.getCharacterMatrix().get("Taxa_1").size());
		assertEquals(0.5, block.getCharacterMatrix().get("Taxa_1").get(0));

		fileData = csvTraitParser.parse(",size_of_head\r\nTaxa_1,0.5");
		assertEquals(1, fileData.getBlocks().size());
		block = (CharactersBlock)fileData.getBlocks().get(0);
		assertEquals(CharactersBlock.CONTINUOUS, block.getDataType());
		assertEquals(1, block.getCharacterLabels().size());
		assertEquals("size_of_head", block.getCharacterLabels().get(0));
		assertEquals(1, block.getCharacterMatrix().get("Taxa_1").size());
		assertEquals(0.5, block.getCharacterMatrix().get("Taxa_1").get(0));
	}

	public void testTabSeparatedData() throws Throwable {
		FileData fileData = csvTraitParser.parse("\tsize_of_head\tsize_of_neck\tsize_of_belly_button\rTaxa_1\t0.5\t0.6\t0.7\rTaxa_2\t0.1\t0.2\t0.3\rTaxa_3\t0.3\t0.2\t0.1");
		assertEquals(SourceFormat.CSV_TRAIT, fileData.getSourceFormat());
		assertEquals(1, fileData.getBlocks().size());
		CharactersBlock block = (CharactersBlock)fileData.getBlocks().get(0);
		assertEquals(CharactersBlock.CONTINUOUS, block.getDataType());
		assertEquals(3, block.getCharacterLabels().size());		
		assertEquals("size_of_head", block.getCharacterLabels().get(0));
		assertEquals("size_of_neck", block.getCharacterLabels().get(1));
		assertEquals("size_of_belly_button", block.getCharacterLabels().get(2));
		assertEquals(3, block.getCharacterMatrix().get("Taxa_1").size());
		assertEquals(0.5, block.getCharacterMatrix().get("Taxa_1").get(0));
		assertEquals(0.6, block.getCharacterMatrix().get("Taxa_1").get(1));
		assertEquals(0.7, block.getCharacterMatrix().get("Taxa_1").get(2));
		assertEquals(3, block.getCharacterMatrix().get("Taxa_2").size());
		assertEquals(0.1, block.getCharacterMatrix().get("Taxa_2").get(0));
		assertEquals(0.2, block.getCharacterMatrix().get("Taxa_2").get(1));
		assertEquals(0.3, block.getCharacterMatrix().get("Taxa_2").get(2));
		assertEquals(3, block.getCharacterMatrix().get("Taxa_3").size());
		assertEquals(0.3, block.getCharacterMatrix().get("Taxa_3").get(0));
		assertEquals(0.2, block.getCharacterMatrix().get("Taxa_3").get(1));
		assertEquals(0.1, block.getCharacterMatrix().get("Taxa_3").get(2));		
	}
	
	public void testUnknownValues() throws Throwable {
		FileData fileData = csvTraitParser.parse("\tsize_of_head\tsize_of_neck\tsize_of_belly_button\rTaxa_1\t0.5\t0.6\t0.7\rTaxa_2\t0.1\tNA\t0.3\rTaxa_3\tUNKNOWN\t0.2\t0.1");
		assertEquals(SourceFormat.CSV_TRAIT, fileData.getSourceFormat());
		assertEquals(1, fileData.getBlocks().size());
		CharactersBlock block = (CharactersBlock)fileData.getBlocks().get(0);
		assertEquals(CharactersBlock.CONTINUOUS, block.getDataType());
		assertEquals(3, block.getCharacterLabels().size());		
		assertEquals("size_of_head", block.getCharacterLabels().get(0));
		assertEquals("size_of_neck", block.getCharacterLabels().get(1));
		assertEquals("size_of_belly_button", block.getCharacterLabels().get(2));
		assertEquals(3, block.getCharacterMatrix().get("Taxa_1").size());
		assertEquals(0.5, block.getCharacterMatrix().get("Taxa_1").get(0));
		assertEquals(0.6, block.getCharacterMatrix().get("Taxa_1").get(1));
		assertEquals(0.7, block.getCharacterMatrix().get("Taxa_1").get(2));
		assertEquals(3, block.getCharacterMatrix().get("Taxa_2").size());
		assertEquals(0.1, block.getCharacterMatrix().get("Taxa_2").get(0));
		assertEquals("NA", block.getCharacterMatrix().get("Taxa_2").get(1));
		assertEquals(0.3, block.getCharacterMatrix().get("Taxa_2").get(2));
		assertEquals(3, block.getCharacterMatrix().get("Taxa_3").size());
		assertEquals("UNKNOWN", block.getCharacterMatrix().get("Taxa_3").get(0));
		assertEquals(0.2, block.getCharacterMatrix().get("Taxa_3").get(1));
		assertEquals(0.1, block.getCharacterMatrix().get("Taxa_3").get(2));		
	}
	
	public void testCharacterLabelsStartInFirstColumn() throws Throwable {
		FileData fileData = csvTraitParser.parse("size_of_head\tsize_of_neck\tsize_of_belly_button\rTaxa_1\t0.5\t0.6\t0.7\rTaxa_2\t0.1\t0.2\t0.3\rTaxa_3\t0.3\t0.2\t0.1");
		assertEquals(SourceFormat.CSV_TRAIT, fileData.getSourceFormat());
		assertEquals(1, fileData.getBlocks().size());
		CharactersBlock block = (CharactersBlock)fileData.getBlocks().get(0);
		assertEquals(CharactersBlock.CONTINUOUS, block.getDataType());
		assertEquals(3, block.getCharacterLabels().size());		
		assertEquals("size_of_head", block.getCharacterLabels().get(0));
		assertEquals("size_of_neck", block.getCharacterLabels().get(1));
		assertEquals("size_of_belly_button", block.getCharacterLabels().get(2));
		assertEquals(3, block.getCharacterMatrix().get("Taxa_1").size());
		assertEquals(0.5, block.getCharacterMatrix().get("Taxa_1").get(0));
		assertEquals(0.6, block.getCharacterMatrix().get("Taxa_1").get(1));
		assertEquals(0.7, block.getCharacterMatrix().get("Taxa_1").get(2));
		assertEquals(3, block.getCharacterMatrix().get("Taxa_2").size());
		assertEquals(0.1, block.getCharacterMatrix().get("Taxa_2").get(0));
		assertEquals(0.2, block.getCharacterMatrix().get("Taxa_2").get(1));
		assertEquals(0.3, block.getCharacterMatrix().get("Taxa_2").get(2));
		assertEquals(3, block.getCharacterMatrix().get("Taxa_3").size());
		assertEquals(0.3, block.getCharacterMatrix().get("Taxa_3").get(0));
		assertEquals(0.2, block.getCharacterMatrix().get("Taxa_3").get(1));
		assertEquals(0.1, block.getCharacterMatrix().get("Taxa_3").get(2));		
	}
	
	public void testQuoteEscaping() throws Throwable {
		FileData fileData = csvTraitParser.parse("\t\"character \"\"quoted\"\"\"\r\"Taxa 1 \"\"older\"\"\"\t0.5");
		assertEquals(SourceFormat.CSV_TRAIT, fileData.getSourceFormat());
		assertEquals(1, fileData.getBlocks().size());
		CharactersBlock block = (CharactersBlock)fileData.getBlocks().get(0);
		assertEquals(CharactersBlock.CONTINUOUS, block.getDataType());
		assertEquals(1, block.getCharacterLabels().size());
		assertEquals("character \"quoted\"", block.getCharacterLabels().get(0));
		assertEquals(1, block.getCharacterMatrix().get("Taxa 1 \"older\"").size());
		assertEquals(0.5, block.getCharacterMatrix().get("Taxa 1 \"older\"").get(0));		
	}
	
	public void testPreserveTaxonLabelOrdering() throws Throwable {
		FileData fileData = csvTraitParser.parse("size_of_head\tsize_of_neck\tsize_of_belly_button\rTaxa_1\t0.5\t0.6\t0.7\rTaxffa_2\t0.1\t0.2\t0.3\rTaxa_3\t0.3\t0.2\t0.1");
		assertEquals(SourceFormat.CSV_TRAIT, fileData.getSourceFormat());
		assertEquals(1, fileData.getBlocks().size());
		CharactersBlock block = (CharactersBlock)fileData.getBlocks().get(0);
		assertEquals(CharactersBlock.CONTINUOUS, block.getDataType());
		assertEquals(3, block.getTaxaLabels().size());		
		assertEquals("Taxa_1", block.getTaxaLabels().get(0));
		assertEquals("Taxffa_2", block.getTaxaLabels().get(1));
		assertEquals("Taxa_3", block.getTaxaLabels().get(2));
	}

	public void testSpeciesColumnWithHeader() throws Throwable {
	    FileData fileData = csvTraitParser.parse(
	        "Species,maxHeight,leafArea\n"
	        + "Lobelia_kauaensis,1,LOW\n"
	        + "Lobelia_villosa,2,HI\n"
	    );
	    assertEquals(SourceFormat.CSV_TRAIT, fileData.getSourceFormat());
	    assertEquals(1, fileData.getBlocks().size());
	    CharactersBlock block = (CharactersBlock)fileData.getBlocks().get(0);
	    assertEquals("maxHeight", block.getCharacterLabels().get(0));
	    assertEquals("leafArea", block.getCharacterLabels().get(1));
	    assertEquals("Lobelia_kauaensis", block.getTaxaLabels().get(0));
        assertEquals(2, block.getCharacterMatrix().get("Lobelia_kauaensis").size());
	    assertEquals(new Double(1), block.getCharacterMatrix().get("Lobelia_kauaensis").get(0));
	    assertEquals("LOW", block.getCharacterMatrix().get("Lobelia_kauaensis").get(1));
        assertEquals("Lobelia_villosa", block.getTaxaLabels().get(1));
        assertEquals(2, block.getCharacterMatrix().get("Lobelia_villosa").size());
        assertEquals(new Double(2), block.getCharacterMatrix().get("Lobelia_villosa").get(0));
        assertEquals("HI", block.getCharacterMatrix().get("Lobelia_villosa").get(1));
	}
}
