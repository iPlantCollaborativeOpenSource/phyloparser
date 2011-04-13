package org.iplantc.phyloparser.identifier;

import java.io.IOException;

import junit.framework.TestCase;

public class TestPileupIdentifier extends TestCase {
	private PileupIdentifier identifier;
	
	public void setUp() {
		identifier = new PileupIdentifier();
	}

	public void testEmptyFile() throws IOException {
		assertFalse(identifier.identify(""));
	}
	
	public void test6ColumnFile() throws IOException {
		assertTrue(identifier.identify(
				"chrM  412  A  2       .,       II\n" +
				"chrM  413  G  4     ..t,     IIIH\n" +
				"chrM  414  C  4     ...a     III2\n" +
				"chrM  415  C  4     TTTt     III7\n"));
	}
	
	public void test10ColumnFile() throws IOException {
		assertTrue(identifier.identify(
				"chrM  412  A  A  75   0  25  2       .,       II\n" +
				"chrM  413  G  G  72   0  25  4     ..t,     IIIH\n" +
				"chrM  414  C  C  75   0  25  4     ...a     III2\n" +
				"chrM  415  C  T  75  75  25  4     TTTt     III7\n"));
	}
}
