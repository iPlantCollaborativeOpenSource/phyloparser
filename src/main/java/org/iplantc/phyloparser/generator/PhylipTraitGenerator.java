package org.iplantc.phyloparser.generator;


import org.iplantc.phyloparser.exception.GeneratorException;

public class PhylipTraitGenerator extends AbstractPhylipTraitGenerator {
    
    @Override
	protected void validateCharacteristicValue(Object value) throws GeneratorException {
        if (!(value instanceof Number)) {
        	throw new GeneratorException("Phylip continuous trait data cannot have non-numeric values");
        }
    }
}
