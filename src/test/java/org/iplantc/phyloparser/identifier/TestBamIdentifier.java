package org.iplantc.phyloparser.identifier;

import java.io.IOException;

import junit.framework.TestCase;

public class TestBamIdentifier extends TestCase {
	private BamIdentifier identifier;
	
	public void setUp() {
		identifier = new BamIdentifier();
	}

	public void testEmptyFile() throws IOException {
		assertFalse(identifier.identify(new byte[0]));
	}
	
	public void testBamFile() throws IOException {
		assertTrue(identifier.identify(this.getClass().getResourceAsStream("/ex1.bam")));
	}
	
	public void testNonBamGzipFile() throws IOException {
		assertFalse(identifier.identify(this.getClass().getResourceAsStream("/nonbam.gz")));		
	}
}
