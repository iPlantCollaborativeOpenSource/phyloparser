package org.iplantc.phyloparser.identifier;

import java.io.IOException;

import junit.framework.TestCase;

public class TestSamIdentifier extends TestCase {
	private SamIdentifier identifier;
	
	public void setUp() {
		identifier = new SamIdentifier();
	}

	public void testEmptyFile() throws IOException {
		assertFalse(identifier.identify(""));
	}
	
	public void testBasicFileWithHeader() throws IOException {
		assertTrue(identifier.identify("@HD\tVN:1.0\n" +
				"@SQ\tSN:chr20 LN:62435964\n" +
				"@RG\tID:L1 PU:SC_1_10 LB:SC_1 SM:NA12891\n" +
				"@RG\tID:L2 PU:SC_2_12 LB:SC_2 SM:NA12891\n" +
				"read_28833_29006_6945 99 chr20 28833 20 10M1D25M = 28993 195 \\\n" +
				"\tAGCTTAGCTAGCTACCTATATCTTGGTCTTGGCCG <<<<<<<<<<<<<<<<<<<<<:<9/,&,22;;<<< \\\n" +
				"\tNM:i:1 RG:Z:L1\n" +
				"read_28701_28881_323b 147 chr20 28834 30 35M	= 28701 -168 \\\n" +
				"\tACCTATATCTTGGCCTTGGCCGATGCGGCCTTGCA <<<<<;<<<<7;:<<<6;<<<<<<<<<<<<7<<<< \\\n" +
				"\tMF:i:18 RG:Z:L2\n"));
	}
	
	public void testBasicFileWithoutHeader() throws IOException {
		assertTrue(identifier.identify("read_28833_29006_6945 99 chr20 28833 20 10M1D25M = 28993 195 \\\n" +
				"\tAGCTTAGCTAGCTACCTATATCTTGGTCTTGGCCG <<<<<<<<<<<<<<<<<<<<<:<9/,&,22;;<<< \\\n" +
				"\tNM:i:1 RG:Z:L1\n" +
				"read_28701_28881_323b 147 chr20 28834 30 35M	= 28701 -168 \\\n" +
				"\tACCTATATCTTGGCCTTGGCCGATGCGGCCTTGCA <<<<<;<<<<7;:<<<6;<<<<<<<<<<<<7<<<< \\\n" +
				"\tMF:i:18 RG:Z:L2\n"));
	}
}
