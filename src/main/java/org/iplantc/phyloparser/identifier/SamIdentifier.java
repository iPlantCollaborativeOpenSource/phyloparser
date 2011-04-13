package org.iplantc.phyloparser.identifier;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

public class SamIdentifier extends AbstractTextIdentifier {

	@Override
	public boolean identify(Reader sam) throws IOException {
		StringBuilder firstLineBuilder = new StringBuilder();
		int charRead;
		char lastChar = '\0';
		
		while ((charRead = sam.read()) != -1) {
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
		
		return (firstLine.startsWith("@HD") && firstLine.contains("VN:")) ||
			Pattern.compile("[^ \t\n\r]+\\s+[0-9]+\\s+[^ \t\n\r@=]+\\s+[0-9]+\\s+[0-9]+\\s+([0-9]+[MIDNSHP])+|\\*\\s+[^ \t\n\r@]+\\s+[0-9]+\\s+-?[0-9]+\\s+").matcher(firstLine).find(0);	
	}
}
