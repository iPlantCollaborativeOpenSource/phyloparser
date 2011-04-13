package org.iplantc.phyloparser.identifier;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

public abstract class AbstractTextIdentifier implements TextIdentifier, BinaryIdentifier {

	public AbstractTextIdentifier() {
		super();
	}

	public abstract boolean identify(Reader sam) throws IOException;

	public boolean identify(byte[] bam) throws IOException {
		return identify(new ByteArrayInputStream(bam));
	}

	public boolean identify(InputStream bam) throws IOException {
		return identify(new InputStreamReader(bam));
	}

	public boolean identify(String sam) throws IOException {
		return identify(new StringReader(sam));
	}

	public boolean identify(File samFile) throws IOException {
		return identify(new FileReader(samFile));
	}

}