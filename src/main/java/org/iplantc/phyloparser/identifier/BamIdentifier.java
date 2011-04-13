package org.iplantc.phyloparser.identifier;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BamIdentifier implements BinaryIdentifier {

	private static final int[] HEADER_SIGNATURE = new int[] { 31, 139, 8, 4, -1,
			-1, -1, -1, -1, -1, 6, 0, 66, 67, 2, 0 };

	public boolean identify(byte[] bam) throws IOException {
		return identify(new ByteArrayInputStream(bam));
	}

	public boolean identify(File bamFile) throws IOException {
		return identify(new FileInputStream(bamFile));
	}

	public boolean identify(InputStream bam) throws IOException {
		byte header[] = new byte[16];
		int totalRead = 0;
		while (totalRead < 16) {
			int bytesRead = bam.read(header);
			if (bytesRead == -1) {
				break;
			}
			totalRead += bytesRead;
		}

		if (totalRead < 16) {
			return false;
		}

		return compareSignature(header, HEADER_SIGNATURE);
	}
	
	private static boolean compareSignature(byte[] actual, int[] signature) {
		if (actual.length != signature.length) {
			return false;
		}
		
		for (int i = 0; i < signature.length; i++) {
			if (signature[i] == -1) {
				continue;
			} else if ((((int)actual[i]) & 0xff) != signature[i]) {
				return false;
			}
		}
		
		return true;
	}
}
