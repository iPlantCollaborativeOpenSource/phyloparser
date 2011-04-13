package org.iplantc.phyloparser.identifier;

import java.io.IOException;
import java.io.Reader;

public class FastqIdentifier extends AbstractTextIdentifier {
	public boolean identify(Reader fastq) throws IOException {
		int firstChar = fastq.read();
		return firstChar == '@';
	}
}
