package org.iplantc.phyloparser.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iplantc.phyloparser.exception.ParserException;
import org.iplantc.phyloparser.model.FileData;
import org.iplantc.phyloparser.model.SourceFormat;
import org.iplantc.phyloparser.model.block.CharactersBlock;
import org.iplantc.phyloparser.model.statement.CharStateLabelsStatement;
import org.iplantc.phyloparser.model.statement.DimensionsCharStatement;
import org.iplantc.phyloparser.model.statement.FormatStatement;
import org.iplantc.phyloparser.model.statement.MatrixStatement;

import au.com.bytecode.opencsv.CSVReader;

public class CsvTraitParser {

	public FileData parse(String newick, Boolean parseProvenance) throws IOException, ParserException {
		return parse(new StringReader(newick), parseProvenance);
	}

	public FileData parse(String csv) throws IOException, ParserException {
		return parse(new StringReader(csv), true);
	}

	public FileData parse(File csvFile) throws IOException, ParserException {
		return parse(new FileReader(csvFile), true);
	}

	public FileData parse(Reader reader, Boolean parseProvenance) throws IOException, ParserException {
		CharactersBlock charBlock;
		Reader bufferedReader = new BufferedReader(reader);
		// 1MB should be sufficiently large for any trait data we would encounter
		bufferedReader.mark(1024 * 1024);

		try {
			List<String[]> values = new CSVReader(bufferedReader, '\t', '"').readAll();
			charBlock = parseCsvModel(values);
		} catch (ParserException pe) {
			bufferedReader.reset();
			List<String[]> values = new CSVReader(bufferedReader, ',', '"').readAll();
			charBlock = parseCsvModel(values);
		}


		FileData fd = new FileData();
		fd.setSourceFormat(SourceFormat.CSV_TRAIT);
		fd.getBlocks().add(charBlock);
		return fd;
	}

	public CharactersBlock parseCsvModel(List<String[]> values) throws ParserException {
		List<String> charLabels = new ArrayList<String>();
		List<String> taxonLabels = new ArrayList<String>();
		Map<String, List<Object>> characterMatrix = new HashMap<String, List<Object>>();

		// Parsing content as CSV/TSV is going to pass almost 100% of the time, even on
		// content that isn't really CSV/TSV
		// We can catch a lot of non-CSV/TSV cases by checking that the first line of the file has
		// more than one entry
		if (values.get(0).length == 1) {
			throw new ParserException("Content does not appear to be valid trait data");
		}

		int charLabelsStartColumn = determineCharacterLabelStartColumn(values);
		for (int i = charLabelsStartColumn; i < values.get(0).length; i++) {
			charLabels.add(values.get(0)[i]);
		}
		values.remove(0);
		int i = 1;
		for (String line[] : values) {
			if (!isEmpty(line[0])) {
				taxonLabels.add(line[0]);
				List<Object> charValues = new ArrayList<Object>();
				for (int j = 1; j < line.length; j++) {
					try {
						charValues.add(Double.valueOf(line[j]));
					} catch (NumberFormatException nfe) {
						charValues.add(line[j]);
					}
				}
				characterMatrix.put(line[0], charValues);
				i++;
			}
		}

		CharactersBlock charBlock = new CharactersBlock();
		DimensionsCharStatement ncharStatement = new DimensionsCharStatement(charLabels);
		FormatStatement formatStatement = new FormatStatement(FormatStatement.CONTINUOUS);
		CharStateLabelsStatement charStateLabelsStatement = new CharStateLabelsStatement(charLabels);
		MatrixStatement matrixStatement = new MatrixStatement(characterMatrix, taxonLabels);
		charBlock.getStatements().add(ncharStatement);
		charBlock.getStatements().add(formatStatement);
		charBlock.getStatements().add(charStateLabelsStatement);
		charBlock.getStatements().add(matrixStatement);

		return charBlock;
	}

	/**
	 * Determines which column the character labels start in.  We support files with and without a header for the
	 * species column.  If the first row has one value fewer than the maximum number of values in the rest of the
	 * rows then we assume that there is no header for the species column.
	 *
	 * @param rows the list of rows obtained from the file.
	 * @return the starting column for the character labels.
	 */
    private int determineCharacterLabelStartColumn(List<String[]> rows) {
        int max = findMaxRowLength(rows, 1);
        String[] headers = rows.get(0);
        return headers.length == max - 1 ? 0 : 1;
    }

    /**
     * Finds the maximum row length in the given list of rows, starting at the given row index.
     * 
     * @param rows the list of rows.
     * @param startIndex the index of the starting row.
     * @return the maximum row length.
     */
    private int findMaxRowLength(List<String[]> rows, int startIndex) {
        int max = 0;
        for (int i = startIndex; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (max < row.length) {
                max = row.length;
            }
        }
        return max;
    }

    private boolean isEmpty(String s) {
		return (s == null || s.trim().length() == 0);
	}
}
