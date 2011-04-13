package org.iplantc.phyloparser.parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RuleReturnScope;
import org.antlr.runtime.tree.CommonTree;
import org.iplantc.phyloparser.exception.ParserException;
import org.iplantc.phyloparser.model.FileData;
import org.iplantc.phyloparser.model.block.CharactersBlock;
import org.iplantc.phyloparser.model.statement.DimensionsCharStatement;
import org.iplantc.phyloparser.model.statement.MatrixStatement;
import org.iplantc.phyloparser.parser.antlr.PhylipContinuousLexer;
import org.iplantc.phyloparser.util.NullFilteringReader;

public class PhylipContinuousParser {

	public FileData parse(String phylip) throws IOException, ParserException {
		return parse(new StringReader(phylip));
	}

	public FileData parse(File phylipFile) throws IOException, ParserException {
		return parse(new FileReader(phylipFile));
	}

	public FileData parse(Reader reader) throws IOException, ParserException {
		CharStream cs = new ANTLRReaderStream(new NullFilteringReader(reader));
        PhylipContinuousLexer lexer = new PhylipContinuousLexer(cs);
        CommonTokenStream tokens = new CommonTokenStream();
        tokens.setTokenSource(lexer);
        org.iplantc.phyloparser.parser.antlr.PhylipContinuousParser parser =
        	new org.iplantc.phyloparser.parser.antlr.PhylipContinuousParser(tokens);
        RuleReturnScope result;
		try {
			result = parser.phylip();
			if (parser.getNumberOfSyntaxErrors() + lexer.getNumberOfSyntaxErrors() > 0) {
				throw new ParserException("Error parsing Phylip continuous trait file");
			}
		} catch (RecognitionException e) {
			throw new ParserException(e);
		}
		CommonTree astTree = (CommonTree)result.getTree();

		int nchar;
		nchar = Integer.valueOf(astTree.getChild(1).getText());

		List<String> charLabels = new ArrayList<String>();
		for (int i = 0; i < nchar; i++) {
			charLabels.add(null);
		}
		
		int ntax;
		ntax = Integer.valueOf(astTree.getChild(0).getText());
		
		List<String> taxLabels = new ArrayList<String>();
		Map<String,List<Object>> charMatrix = new HashMap<String,List<Object>>();
		
		for (int i = 0; i < astTree.getChild(2).getChildCount(); i++) {
			CommonTree taxaNode = (CommonTree) astTree.getChild(2).getChild(i);
			taxLabels.add(taxaNode.getText());
			List<Object> taxaValues = new ArrayList<Object>();
			if (nchar != taxaNode.getChildCount()) {
				throw new ParserException("Number of character values for taxa " + taxaNode.getText() + " does not match count in header");
			}
			for (int j = 0; j < taxaNode.getChildCount(); j++) {
				taxaValues.add(Double.valueOf(taxaNode.getChild(j).getText()));
			}
			charMatrix.put(taxaNode.getText(), taxaValues);
		}
		
		if (ntax != taxLabels.size()) {
			throw new ParserException("Taxa count in header did not match number of taxa in data");
		}
		
		FileData fd = new FileData();
		CharactersBlock charsBlock = new CharactersBlock();
		DimensionsCharStatement ncharStatement = new DimensionsCharStatement(charLabels);
		MatrixStatement matrixStatement = new MatrixStatement(charMatrix, taxLabels);
		charsBlock.getStatements().add(ncharStatement);
		charsBlock.getStatements().add(matrixStatement);
		fd.getBlocks().add(charsBlock);
		return fd;
	}

}
