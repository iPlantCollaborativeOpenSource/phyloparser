package org.iplantc.phyloparser.identifier;

import java.io.IOException;

import junit.framework.TestCase;

public class TestFastqIdentifier extends TestCase {
	private FastqIdentifier identifier;
	
	public void setUp() {
		identifier = new FastqIdentifier();
	}

	public void testEmptyFile() throws IOException {
		assertFalse(identifier.identify(""));
	}
	
	public void testBasicFile() throws IOException {
		assertTrue(identifier.identify("@GAR_BLE_DE_GOOK_123_45_6789_0\nCGTCTAGCTTCTCCTATGAAA\n+\n;;;;;;4;;;3;;2;;;1;99\n"));
	}
	
	public void testMultipleSequenceFile() throws IOException {
		assertTrue(identifier.identify("@GAR_BLE_DE_GOOK_123_45_6789_0\nCGTCTAGCTTCTCCTATGAAA\n+\n;;;;;;4;;;3;;2;;;1;99\n" +
				"@GAR_BLE_DE_GOOK_123_45_6789_0\nCGTCTAGCTTCTCCTATGAAA\n+\n;;;;;;4;;;3;;2;;;1;99\n"));		
	}
}
