package org.iplantc.phyloparser.identifier;

import java.io.IOException;

import junit.framework.TestCase;

public class TestVcfIdentifier extends TestCase {
	private VcfIdentifier identifier;
	
	public void setUp() {
		identifier = new VcfIdentifier();
	}

	public void testEmptyFile() throws IOException {
		assertFalse(identifier.identify(""));
	}
	
	public void testBasicFile() throws IOException {
		assertTrue(identifier.identify(
				"##format=VCFv3.3\n" +
				"##fileDate=20090805\n" +
				"##source=myImputationProgramV3.1\n" +
				"##reference=1000GenomesPilot-NCBI36\n" +
				"##phasing=partial\n" +
				"#CHROM  POS     ID        REF   ALT    QUAL  FILTER  INFO                                 FORMAT       NA00001         NA00002         NA00003\n" +
				"20      14370   rs6054257 G     A      29    0       NS=58;DP=258;AF=0.786;DB;H2          GT:GQ:DP:HQ  0|0:48:1:51,51  1|0:48:8:51,51  1/1:43:5\n" +
				"20      13330   .         T     A      3     q10     NS=55;DP=202;AF=0.024                GT:GQ:DP:HQ  0|0:49:3:58,50  0|1:3:5:65,3    0/0:41:3\n" +
				"20      1110696 rs6040355 A     G,T    67    0       NS=55;DP=276;AF=0.421,0.579;AA=T;DB  GT:GQ:DP:HQ  1|2:21:6:23,27  2|1:2:0:18,2    2/2:35:4\n" +
				"20      10237   .         T     .      47    0       NS=57;DP=257;AA=T                    GT:GQ:DP:HQ  0|0:54:7:56,60  0|0:48:4:51,51  0/0:61:2\n" +
				"20      123456  microsat1 G     D4,IGA 50    0       NS=55;DP=250;AA=G                    GT:GQ:DP     0/1:35:4        0/2:17:2        1/1:40:3\n"));
	}

	public void testStartsWithFileformat() throws IOException {
		assertTrue(identifier.identify(
				"##fileformat=VCFv3.3" +
				"##INFO=DP,1,Integer,\"Total Depth\"" +
				"##FORMAT=GT,1,String,\"Genotype\"" +
				"##FORMAT=GQ,1,Integer,\"Genotype Quality\"" +
				"##FORMAT=DP,1,Integer,\"Read Depth\"" +
				"#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	samplename" +
				"Chr1	711	.	T	C	30	0	DP=4	GT:GQ:DP	1/1:30:4" +
				"Chr1	956	.	C	T	30	0	DP=4	GT:GQ:DP	1/1:30:4" +
				"Chr1	4519	.	G	T	57	0	DP=10	GT:GQ:DP	0/1:57:10" +
				"Chr1	6324	.	T	IA	49	0	DP=3	GT:GQ:DP	1/1:47:3"));
	}
}
