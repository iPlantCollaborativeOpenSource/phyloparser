package org.iplantc.phyloparser.identifier;

import java.io.IOException;

import junit.framework.TestCase;

public class TestFastaIdentifier extends TestCase {
	private FastaIdentifier identifier;
	
	public void setUp() {
		identifier = new FastaIdentifier();
	}

	public void testEmptyFile() throws IOException {
		assertFalse(identifier.identify(""));
	}
	
	public void testBasicFile() throws IOException {
		assertTrue(identifier.identify(">GAR|BLE|DE|GOOK|123|45|6789|0\nCGTCTAGCTTCTCCTATGAAA\n"));
	}
	
	public void testMultipleSequenceFile() throws IOException {
		assertTrue(identifier.identify(">GAR|BLE|DE|GOOK|123|45|6789|0\nCGTCTAGCTTCTCCTATGAAA\n" +
				">GAR|BLE|DE|GOOK|123|45|6789|0\nCGTCTAGCTTCTCCTATGAAA\n"));		
	}
	
	public void testFastaStartsWithSemicolon() throws IOException {
		assertTrue(identifier.identify(";FASTA files can start with comments too\n;GAR|BLE|DE|GOOK|123|45|6789|0\nCGTCTAGCTTCTCCTATGAAA\n"));
	}
}
