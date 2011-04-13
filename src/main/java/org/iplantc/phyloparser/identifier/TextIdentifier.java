package org.iplantc.phyloparser.identifier;

import java.io.IOException;
import java.io.Reader;

public interface TextIdentifier {
	public boolean identify(String vcf) throws IOException;
	public boolean identify(Reader vcf) throws IOException;
}
