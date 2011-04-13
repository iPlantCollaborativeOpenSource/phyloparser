package org.iplantc.phyloparser.parser;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.List;

import junit.framework.TestCase;

public class TestNameListCsvParser extends TestCase {
	private NameListCsvParser csvParser;
	private String csvString;
	private String csvSingleColString;
	private File csvFile;
	private File csvSingleColFile;
	
	public void setUp() {
		try {
			csvParser = new NameListCsvParser();
			csvFile = new File(this.getClass().getResource("/Workbook1.csv").toURI());
			
			csvSingleColFile = new File(this.getClass().getResource("/SingleColumnWorkbook.csv").toURI());
			
			FileReader fr = new FileReader(csvFile);
			BufferedReader br = new BufferedReader(fr);
			
			csvString = "";
			String line = null;
			
			while ((line = br.readLine()) != null) {
				csvString += line + "\n";
			}
			br.close();
			
			FileReader fr2 = new FileReader(csvSingleColFile);
			BufferedReader br2 = new BufferedReader(fr2);
			
			csvSingleColString = "";
			String line2 = null;
			while ((line2 = br2.readLine()) != null) {
				csvSingleColString += line2 + "\n";
			}
			br2.close();
			
		} catch (Exception e) {
			
		}
	}
	
	public void testFileLength() throws Throwable {
		List<String[]> contents = csvParser.parse(csvFile);
		assertEquals(contents.size(), 101);
	}
	
	public void testStringLength() throws Throwable {
		List<String[]> contents = csvParser.parse(csvString);
		assertEquals(contents.size(), 101);
	}
	
	public void testNumColumns() throws Throwable {
		List<String[]> contents = csvParser.parse(csvFile);
		String[] line0 = contents.get(0);
		assertEquals(line0.length, 2);
	}

	public void testFirstLine() throws Throwable {
		List<String[]> contents = csvParser.parse(csvString);
		String[] line0 = contents.get(0);
		assertEquals(line0[0], "Family_submitted");
		assertEquals(line0[1], "ScientificName_submitted");
	}
	
	public void testLastLine() throws Throwable {
		List<String[]> contents = csvParser.parse(csvString);
		String[] lastLine = contents.get(contents.size() - 1);
		assertEquals(lastLine[0], "ANACARDIACEAE");
		assertEquals(lastLine[1], "Comocladia glabra");
	}
	
	public void testSingleColFileLength() throws Throwable {
		List<String[]> contents = csvParser.parse(csvSingleColFile);
		assertEquals(contents.size(), 101);
	}
	
	public void testSingleColumnStringLength() throws Throwable {
		List<String[]> contents = csvParser.parse(csvSingleColString);
		assertEquals(contents.size(), 101);
	}
	
	public void testSingleColNumColumns() throws Throwable {
		List<String[]> contents = csvParser.parse(csvSingleColFile);
		String[] line0 = contents.get(0);
		assertEquals(line0.length, 1);
	}

	public void testSingleColFirstLine() throws Throwable {
		List<String[]> contents = csvParser.parse(csvSingleColString);
		String[] line0 = contents.get(0);
		assertEquals(line0[0], "Family_submitted");
	}
	
	public void testSingleColLastLine() throws Throwable {
		List<String[]> contents = csvParser.parse(csvSingleColString);
		String[] lastLine = contents.get(contents.size() - 1);
		assertEquals(lastLine[0], "ANACARDIACEAE");
	}
	
	public void testPlainText() throws Throwable {
		List<String[]> contents = csvParser.parse("Applecus Bananii\nBirdius Scrumptious\n");
		String[] lastLine = contents.get(contents.size() - 1);
		assertEquals(1, lastLine.length);
		assertEquals(lastLine[0], "Birdius Scrumptious");		
	}
	
	public void testPlainTextNamesWithCommas() throws Throwable {
		List<String[]> contents = csvParser.parse("Applecus, Bananii\nBirdius Scrumptious\nWolfius Brainius, Esq.\n");
		String[] lastLine = contents.get(contents.size() - 1);
		assertEquals(1, lastLine.length);
		assertEquals(lastLine[0], "Wolfius Brainius, Esq.");
	}
}
