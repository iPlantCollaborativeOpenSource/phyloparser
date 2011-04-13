package org.iplantc.phyloparser.identifier;

import java.io.IOException;
import java.io.Reader;

public class VcfIdentifier extends AbstractTextIdentifier {
	public boolean identify(Reader vcf) throws IOException {
		StringBuilder firstLineBuilder = new StringBuilder();
		int charRead;
		char lastChar = '\0';
		
		while ((charRead = vcf.read()) != -1) {
			if (charRead == '\n' && lastChar =='\\') {
				firstLineBuilder.deleteCharAt(firstLineBuilder.length() - 1);
			}
			if (charRead != '\t' && lastChar == '\n') {
				break;
			}
			firstLineBuilder.append((char)charRead);
			lastChar = (char)charRead;
		}
		
		String firstLine = firstLineBuilder.toString();
		
		return firstLine.startsWith("##format=VCFv3.3") || firstLine.startsWith("##fileformat=VCFv3.3");
	}
}
