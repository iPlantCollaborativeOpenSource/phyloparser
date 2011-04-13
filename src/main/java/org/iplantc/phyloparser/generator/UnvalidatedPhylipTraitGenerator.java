package org.iplantc.phyloparser.generator;

import org.iplantc.phyloparser.exception.GeneratorException;

public class UnvalidatedPhylipTraitGenerator extends AbstractPhylipTraitGenerator {

    @Override
    protected void validateCharacteristicValue(Object value) throws GeneratorException {
    }

}
