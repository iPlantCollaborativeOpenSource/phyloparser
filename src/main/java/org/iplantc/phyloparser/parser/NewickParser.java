package org.iplantc.phyloparser.parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RuleReturnScope;
import org.antlr.runtime.tree.TreeVisitor;
import org.iplantc.phyloparser.exception.ParserException;
import org.iplantc.phyloparser.model.FileData;
import org.iplantc.phyloparser.model.Node;
import org.iplantc.phyloparser.model.SourceFormat;
import org.iplantc.phyloparser.model.Tree;
import org.iplantc.phyloparser.model.block.TreesBlock;
import org.iplantc.phyloparser.model.statement.TreeStatement;
import org.iplantc.phyloparser.parser.antlr.NewickLexer;
import org.iplantc.phyloparser.parser.treevisitor.NewickModelConstructorVisitorAction;
import org.iplantc.phyloparser.util.NullFilteringReader;

public class NewickParser {

	public FileData parse(String newick, Boolean parseProvenance) throws IOException, ParserException {
		return parse(new StringReader(newick), parseProvenance);
	}

	public FileData parse(String newick) throws IOException, ParserException {
		return parse(new StringReader(newick), true);
	}

	public FileData parse(File newickFile) throws IOException, ParserException {
		return parse(new FileReader(newickFile), true);
	}

	public FileData parse(Reader reader, Boolean parseProvenance) throws IOException, ParserException {
		CharStream cs = new ANTLRReaderStream(new NullFilteringReader(reader));
        NewickLexer lexer = new NewickLexer(cs);
        CommonTokenStream tokens = new CommonTokenStream();
        tokens.setTokenSource(lexer);
        org.iplantc.phyloparser.parser.antlr.NewickParser parser =
        	new org.iplantc.phyloparser.parser.antlr.NewickParser(tokens);
        RuleReturnScope result;
		try {
			result = parser.newick();
			if (parser.getNumberOfSyntaxErrors() + lexer.getNumberOfSyntaxErrors() > 0) {
				throw new ParserException("Error parsing Newick file");
			}
		} catch (RecognitionException e) {
			throw new ParserException(e);
		}
		NewickModelConstructorVisitorAction mcva = new NewickModelConstructorVisitorAction();
		new TreeVisitor().visit(result.getTree(), mcva);
		FileData fileData = new FileData();
		TreesBlock treesBlock = new TreesBlock();
		List<Node> treeRoots = mcva.getRoots();
		for (Node root : treeRoots) {
			treesBlock.getStatements().add(new TreeStatement(new Tree(root)));
		}
		fileData.getBlocks().add(treesBlock);
		fileData.setSourceFormat(SourceFormat.NEWICK);
		return fileData;
	}
}
