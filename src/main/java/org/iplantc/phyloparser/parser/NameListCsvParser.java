package org.iplantc.phyloparser.parser;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Uses the CSVReader class provided by the opencsv project to parse
 * a Name List CSV file and return a list of String arrays.
 * 
 * @author John Wregglesworth
 */

public class NameListCsvParser {
	/**
	 * Parses a String containing CSV data.
	 * 
	 * @param csvString - A String object.
	 * @return A List<String[]> containing the parsed contents of csvString.
	 * @throws IOException
	 */
	public List<String[]> parse(String csvString) throws IOException {
		return parse(new StringReader(csvString));
	}
	
	/**
	 * Parses a File containing CSV data.
	 * 
	 * @param csvFile - A File object.
	 * @return A List<String[]> containing the parsed contents of csvFile.
	 * @throws IOException
	 */
	public List<String[]> parse(File csvFile) throws IOException {
		return parse(new FileReader(csvFile));
	}
	
	/**
	 * Parses a Reader that's processing a source containing CSV data.
	 * @param reader - A Reader object.
	 * @return A List<String[]> containing the parsed contents of reader.
	 * @throws IOException
	 */
	public List<String[]> parse(Reader reader) throws IOException {
		BufferedReader bufReader = new BufferedReader(reader);
		List<String[]> contents;
		
		if (isTextFile(bufReader)) {
			contents = new ArrayList<String[]>();

			String lineRead;
			while ((lineRead = bufReader.readLine()) != null) {
				contents.add(new String[] {lineRead});
			}
		} else {
			try {
				bufReader.mark(4096);
				contents = new CSVReader(bufReader, ',', '"').readAll();
			} catch (IOException e) {
				bufReader.reset();
				contents = new CSVReader(bufReader, '\t', '"').readAll();
			}			
		}
		return contents;
	}

	private boolean isTextFile(BufferedReader reader) throws IOException {
		boolean isText = false;
		reader.mark(4096);

		String firstLines[] = new String[] {reader.readLine(),
											reader.readLine(),
											reader.readLine(),
											reader.readLine(),
											reader.readLine()};
		int lastCommaCount = 0;
		
		for (int i = 0; i < firstLines.length; i++) {
			if (firstLines[i] == null) {
				isText = true;
				break;
			}
			
			if (firstLines[i].contains("\t")) {
				break;
			}
			if (firstLines[i].contains("\"")) {
				break;
			}
			int thisCommaCount = firstLines[i].split(",").length;
			if (i > 0) {
				if (thisCommaCount != lastCommaCount) {
					isText = true;
					break;
				}
			}
			lastCommaCount = thisCommaCount;
		}
		
		reader.reset();
		return isText;
	}
}
