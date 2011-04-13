package org.iplantc.phyloparser.identifier;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

public class PileupIdentifier extends AbstractTextIdentifier {
	public boolean identify(Reader pileup) throws IOException {
		StringBuilder firstLineBuilder = new StringBuilder();
		int charRead;
		char lastChar = '\0';
		
		while ((charRead = pileup.read()) != -1) {
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
		
		return Pattern.compile("^[^\\s]+\\s+[0-9]+\\s+[^\\s]\\s+[0-9]+\\s+").matcher(firstLine).find(0) ||
			Pattern.compile("^[^\\s]+\\s+[0-9]+\\s+[^\\s]\\s+[^\\s]\\s+[0-9]+\\s+[0-9]+\\s+[0-9]+\\s+[0-9]+\\s+").matcher(firstLine).find(0);
	}
}
