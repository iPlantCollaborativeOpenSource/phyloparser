package org.iplantc.phyloparser.util;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

public class NullFilteringReader extends FilterReader {

	public NullFilteringReader(Reader in) {
		super(in);
	}

	@Override
	public int read() throws IOException {
		int charRead = 0;
		while ((charRead = super.read()) == 0);
		return charRead;
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		int totalRead = 0;
		
		while (totalRead < len) {
			int charsRead = super.read(cbuf, off + totalRead, len - totalRead);
			if (charsRead == -1) {
				break;
			}
			for (int i = off + totalRead; i < off + totalRead + charsRead; i++) {
				if (cbuf[i] == 0) {
					System.arraycopy(cbuf, i + 1, cbuf, i, off + totalRead + charsRead - i - 1);
					charsRead--;
					i--;
				}
			}
			totalRead += charsRead;
		}
		return (totalRead > 0) ? totalRead : -1;
	}

}
