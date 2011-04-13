package org.iplantc.phyloparser.identifier;

import java.io.IOException;
import java.io.Reader;

public class FastaIdentifier extends AbstractTextIdentifier {
	public boolean identify(Reader fasta) throws IOException {
		int firstChar = fasta.read();
		return firstChar == '>' || firstChar == ';';
	}
}
