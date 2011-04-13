package org.iplantc.phyloparser.generator;

import java.io.StringWriter;

import org.iplantc.phyloparser.exception.GeneratorException;
import org.iplantc.phyloparser.model.FileData;
import org.iplantc.phyloparser.model.block.Block;
import org.iplantc.phyloparser.model.block.CharactersBlock;

public abstract class AbstractPhylipTraitGenerator {

    public String generate(FileData fd) throws GeneratorException {
    	StringWriter writer = new StringWriter();
    	generate(fd, writer);
    	return writer.toString();
    }

    public void generate(FileData fd, StringWriter writer) throws GeneratorException {
    	boolean matrixSeen = false;
    	
    	if (fd.getBlocks().size() > 0) {
    		for (Block block : fd.getBlocks()) {
    			if (block instanceof CharactersBlock) {
    				CharactersBlock charsBlock = (CharactersBlock)block;
    				if (charsBlock.getTaxaLabels().size() > 0) {
    					if (matrixSeen) {
    						throw new GeneratorException("Phylip trait files can only contain one character matrix");
    					}
    					matrixSeen = true;
    					writer.write("   ");
    					writer.write(String.valueOf(charsBlock.getTaxaLabels().size()));
    					writer.write("   ");
    					writer.write(String.valueOf(charsBlock.getCharacterLabels().size()));
    					writer.write("\n");
    					for (String taxon : charsBlock.getTaxaLabels()) {
    						writer.write(padOrTrim(taxon.replace(' ','_'), 10));
    						boolean first = true;
    						for (Object value : charsBlock.getCharacterMatrix().get(taxon)) {
    							validateCharacteristicValue(value);
    							if (!first) {
    								writer.write("  ");							
    							}
    							first = false;
    							writer.write(value == null ? "" : value.toString());
    						}
    						writer.write("\n");
    					}					
    				}
    			}				
    		}
    	}
    }

    protected abstract void validateCharacteristicValue(Object value) throws GeneratorException;

    private String padOrTrim(String str, int length) {
    	if (str.length() > length) {
    		return str.substring(0, length);
    	}
    	char[] padded = new char[length];
    	str.getChars(0, str.length(), padded, 0);
    	for (int i = str.length(); i < length; i++) {
    		padded[i] = ' ';
    	}
    	return new String(padded);
    }

}
