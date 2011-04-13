package org.iplantc.phyloparser.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.iplantc.phyloparser.exception.GeneratorException;
import org.iplantc.phyloparser.model.FileData;
import org.iplantc.phyloparser.model.SourceFormat;
import org.iplantc.phyloparser.model.block.CharactersBlock;
import org.iplantc.phyloparser.model.block.TaxaBlock;
import org.iplantc.phyloparser.model.block.UnknownBlock;
import org.iplantc.phyloparser.model.statement.CharStateLabelsStatement;
import org.iplantc.phyloparser.model.statement.DimensionsCharStatement;
import org.iplantc.phyloparser.model.statement.DimensionsTaxaStatement;
import org.iplantc.phyloparser.model.statement.FormatStatement;
import org.iplantc.phyloparser.model.statement.MatrixStatement;
import org.iplantc.phyloparser.model.statement.TaxlabelsStatement;


public class TestUnvalidatedPhylipTraitGenerator extends TestCase {
    public void testEmptyFile() throws Throwable {
        FileData fd = new FileData();
        AbstractPhylipTraitGenerator ptg = new UnvalidatedPhylipTraitGenerator();
        
        String phylipFile = ptg.generate(fd);
        
        assertEquals("", phylipFile);
    }

    public void testOneUnknownBlock() throws Throwable {
        FileData fd = new FileData();
        UnknownBlock unkBlock = new UnknownBlock();
        unkBlock.setType("MYBLOCK");
        unkBlock.setContent("\n  TITLE This is my block;\n  CONTENT And this is the content;\n");
        fd.getBlocks().add(unkBlock);
        
        AbstractPhylipTraitGenerator ptg = new UnvalidatedPhylipTraitGenerator();      
        String phylipFile = ptg.generate(fd);
        
        assertEquals("", phylipFile);
    }
    
    public void testEmptyCharactersBlock() throws GeneratorException {
        FileData fd = new FileData();
        CharactersBlock charsBlock = new CharactersBlock();
        fd.getBlocks().add(charsBlock);
        
        AbstractPhylipTraitGenerator ptg = new UnvalidatedPhylipTraitGenerator();      
        String phylipFile = ptg.generate(fd);
        
        assertEquals("", phylipFile);
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
        List<String> taxonList = new ArrayList<String>();
        taxonList.add("taxon1");
        Map<String, List<Object>> taxonValueMap = new HashMap<String, List<Object>>();
        taxonValueMap.put("taxon1", taxonValues);
        charBlock.getStatements().add(new MatrixStatement(taxonValueMap, taxonList));
        fd.getBlocks().add(charBlock);
        
        AbstractPhylipTraitGenerator ptg = new UnvalidatedPhylipTraitGenerator();      
        String phylipFile = ptg.generate(fd);
        
        assertEquals("   1   1\ntaxon1    0.1\n", phylipFile);      
    }

    public void testLargerThanTenCharacterTaxonName() throws GeneratorException {
        FileData fd = new FileData();
        CharactersBlock charBlock = new CharactersBlock();
        List<String> chars = new ArrayList<String>();
        chars.add("char1");
        chars.add("char2");
        chars.add("char3");
        charBlock.getStatements().add(new DimensionsCharStatement(chars));
        charBlock.getStatements().add(new FormatStatement(FormatStatement.CONTINUOUS));
        charBlock.getStatements().add(new CharStateLabelsStatement(chars));
        List<Object> taxonValues = new ArrayList<Object>();
        taxonValues.add(0.1);
        taxonValues.add(0.2);
        taxonValues.add(0.3);
        List<String> taxonList = new ArrayList<String>();
        taxonList.add("taxon1");
        taxonList.add("taxon2");
        taxonList.add("taxontaxon3");
        Map<String, List<Object>> taxonValueMap = new HashMap<String, List<Object>>();
        taxonValueMap.put("taxon1", taxonValues);
        taxonValueMap.put("taxon2", taxonValues);
        taxonValueMap.put("taxontaxon3", taxonValues);
        charBlock.getStatements().add(new MatrixStatement(taxonValueMap, taxonList));
        fd.getBlocks().add(charBlock);
        
        AbstractPhylipTraitGenerator ptg = new UnvalidatedPhylipTraitGenerator();      
        String phylipFile = ptg.generate(fd);
        
        assertEquals("   3   3\ntaxon1    0.1  0.2  0.3\ntaxon2    0.1  0.2  0.3\ntaxontaxon0.1  0.2  0.3\n", phylipFile);      
    }

    public void testNonNumericValueFailure() throws GeneratorException {
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

		AbstractPhylipTraitGenerator ptg = new UnvalidatedPhylipTraitGenerator();
		String phylipFile = ptg.generate(fd);
		String expected = "   3   3\n"
			+ "taxon_1   0.1  0.2  0.3\n"
			+ "taxon_2   0.4  0.5  0.6\n"
			+ "taxon_3   0.7    UNK\n";
		assertEquals(expected, phylipFile);
    }

    public void testTaxonNameWithSpaces() throws GeneratorException {
        FileData fd = new FileData();
        CharactersBlock charBlock = new CharactersBlock();
        List<String> chars = new ArrayList<String>();
        chars.add("char1");
        charBlock.getStatements().add(new DimensionsCharStatement(chars));
        charBlock.getStatements().add(new FormatStatement(FormatStatement.CONTINUOUS));
        charBlock.getStatements().add(new CharStateLabelsStatement(chars));
        List<Object> taxonValues = new ArrayList<Object>();
        taxonValues.add(0.1);
        List<String> taxonList = new ArrayList<String>();
        taxonList.add("taxon 1");
        Map<String, List<Object>> taxonValueMap = new HashMap<String, List<Object>>();
        taxonValueMap.put("taxon 1", taxonValues);
        charBlock.getStatements().add(new MatrixStatement(taxonValueMap, taxonList));
        fd.getBlocks().add(charBlock);
        
        AbstractPhylipTraitGenerator ptg = new UnvalidatedPhylipTraitGenerator();      
        String phylipFile = ptg.generate(fd);
        
        assertEquals("   1   1\ntaxon_1   0.1\n", phylipFile);      
    }
    
    public void testMultipleBlocks() throws GeneratorException {
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

        CharactersBlock charBlock = new CharactersBlock();
        List<String> chars = new ArrayList<String>();
        chars.add("char1");
        charBlock.getStatements().add(new DimensionsCharStatement(chars));
        charBlock.getStatements().add(new FormatStatement(FormatStatement.CONTINUOUS));
        charBlock.getStatements().add(new CharStateLabelsStatement(chars));
        List<Object> taxonValues = new ArrayList<Object>();
        taxonValues.add(0.1);
        List<String> taxonList = new ArrayList<String>();
        taxonList.add("taxon 1");
        Map<String, List<Object>> taxonValueMap = new HashMap<String, List<Object>>();
        taxonValueMap.put("taxon 1", taxonValues);
        charBlock.getStatements().add(new MatrixStatement(taxonValueMap, taxonList));
        fd.getBlocks().add(charBlock);

        AbstractPhylipTraitGenerator ptg = new UnvalidatedPhylipTraitGenerator();      
        String phylipFile = ptg.generate(fd);
        
        assertEquals("   1   1\ntaxon_1   0.1\n", phylipFile);      
    }
    
    public void testMultipleCharactersBlocks() throws GeneratorException {
        FileData fd = new FileData();

        CharactersBlock charBlock = new CharactersBlock();
        List<String> chars = new ArrayList<String>();
        chars.add("char1");
        charBlock.getStatements().add(new DimensionsCharStatement(chars));
        charBlock.getStatements().add(new FormatStatement(FormatStatement.CONTINUOUS));
        charBlock.getStatements().add(new CharStateLabelsStatement(chars));
        List<Object> taxonValues = new ArrayList<Object>();
        taxonValues.add(0.1);
        List<String> taxonList = new ArrayList<String>();
        taxonList.add("taxon 1");
        Map<String, List<Object>> taxonValueMap = new HashMap<String, List<Object>>();
        taxonValueMap.put("taxon 1", taxonValues);
        charBlock.getStatements().add(new MatrixStatement(taxonValueMap, taxonList));
        fd.getBlocks().add(charBlock);

        CharactersBlock charBlock2 = new CharactersBlock();
        List<String> chars2 = new ArrayList<String>();
        chars2.add("char2");
        charBlock2.getStatements().add(new DimensionsCharStatement(chars2));
        charBlock2.getStatements().add(new FormatStatement(FormatStatement.CONTINUOUS));
        charBlock2.getStatements().add(new CharStateLabelsStatement(chars2));
        List<Object> taxonValues2 = new ArrayList<Object>();
        taxonValues2.add(0.1);
        List<String> taxonList2 = new ArrayList<String>();
        taxonList2.add("taxon 2");
        Map<String, List<Object>> taxonValueMap2 = new HashMap<String, List<Object>>();
        taxonValueMap2.put("taxon 2", taxonValues2);
        charBlock2.getStatements().add(new MatrixStatement(taxonValueMap2, taxonList2));
        fd.getBlocks().add(charBlock2);

        try {
            AbstractPhylipTraitGenerator ptg = new UnvalidatedPhylipTraitGenerator();      
            ptg.generate(fd);
            fail("Phylip trait files cannot be generated with more than one character matrix");
        } catch (GeneratorException ge) {
            // expected
        }
    }
    
    public void testDifferentNumberOfTaxaAndCharacters() throws GeneratorException {
        FileData fd = new FileData();
        CharactersBlock charBlock = new CharactersBlock();
        List<String> chars = new ArrayList<String>();
        chars.add("char1");
        charBlock.getStatements().add(new DimensionsCharStatement(chars));
        charBlock.getStatements().add(new FormatStatement(FormatStatement.CONTINUOUS));
        charBlock.getStatements().add(new CharStateLabelsStatement(chars));
        List<Object> taxonValues = new ArrayList<Object>();
        taxonValues.add(0.1);
        List<Object> taxonValues2 = new ArrayList<Object>();
        taxonValues2.add(0.2);
        List<String> taxonList = new ArrayList<String>();
        taxonList.add("taxon1");
        taxonList.add("taxon2");
        Map<String, List<Object>> taxonValueMap = new HashMap<String, List<Object>>();
        taxonValueMap.put("taxon1", taxonValues);
        taxonValueMap.put("taxon2", taxonValues2);
        charBlock.getStatements().add(new MatrixStatement(taxonValueMap, taxonList));
        fd.getBlocks().add(charBlock);
        
        AbstractPhylipTraitGenerator ptg = new UnvalidatedPhylipTraitGenerator();      
        String phylipFile = ptg.generate(fd);
        
        assertEquals("   2   1\ntaxon1    0.1\ntaxon2    0.2\n", phylipFile);       
    }
}
