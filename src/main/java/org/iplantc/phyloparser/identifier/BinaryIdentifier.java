package org.iplantc.phyloparser.identifier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface BinaryIdentifier {
	public boolean identify(byte[] bam) throws IOException;
	public boolean identify(File bamFile) throws IOException;
	public boolean identify(InputStream bam) throws IOException;
}
