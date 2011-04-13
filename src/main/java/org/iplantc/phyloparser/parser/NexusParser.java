package org.iplantc.phyloparser.parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RuleReturnScope;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.TreeVisitor;
import org.iplantc.phyloparser.exception.ParserException;
import org.iplantc.phyloparser.model.FileData;
import org.iplantc.phyloparser.model.SourceFormat;
import org.iplantc.phyloparser.model.block.Block;
import org.iplantc.phyloparser.model.block.CharactersBlock;
import org.iplantc.phyloparser.model.block.TaxaBlock;
import org.iplantc.phyloparser.model.block.TreesBlock;
import org.iplantc.phyloparser.model.block.UnknownBlock;
import org.iplantc.phyloparser.model.statement.Comment;
import org.iplantc.phyloparser.parser.antlr.NexusBlockLexer;
import org.iplantc.phyloparser.parser.antlr.NexusBlockParser;
import org.iplantc.phyloparser.parser.antlr.NexusCharactersParser;
import org.iplantc.phyloparser.parser.antlr.NexusTreesLexer;
import org.iplantc.phyloparser.parser.antlr.NexusTaxaLexer;
import org.iplantc.phyloparser.parser.antlr.NexusTaxaParser;
import org.iplantc.phyloparser.parser.antlr.NexusCharactersLexer;
import org.iplantc.phyloparser.parser.antlr.NexusTreesParser;
import org.iplantc.phyloparser.parser.treevisitor.CharactersModelConstructorVisitorAction;
import org.iplantc.phyloparser.parser.treevisitor.TaxaModelConstructorVisitorAction;
import org.iplantc.phyloparser.parser.treevisitor.TreesModelConstructorVisitorAction;
import org.iplantc.phyloparser.util.NullFilteringReader;

public class NexusParser {

	public FileData parse(String newick, Boolean parseProvenance) throws IOException, ParserException {
		return parse(new StringReader(newick), parseProvenance);
	}

	public FileData parse(String nexus) throws IOException, ParserException {
		return parse(new StringReader(nexus), true);
	}

	public FileData parse(File nexusFile) throws IOException, ParserException {
		return parse(new FileReader(nexusFile), true);
	}

	public FileData parse(Reader reader, Boolean parseProvenance) throws IOException, ParserException {
		CharStream cs = new ANTLRReaderStream(new NullFilteringReader(reader));
        NexusBlockLexer lexer = new NexusBlockLexer(cs);
        CommonTokenStream tokens = new CommonTokenStream();
        tokens.setTokenSource(lexer);
        NexusBlockParser parser =
        	new NexusBlockParser(tokens);
        RuleReturnScope result;
		try {
			result = parser.nexus();
			if (parser.getNumberOfSyntaxErrors() + lexer.getNumberOfSyntaxErrors() > 0) {
				throw new ParserException("Error parsing Nexus file");
			}
		} catch (RecognitionException e) {
			throw new ParserException(e);
		}
		FileData fileData = new FileData();
		fileData.setSourceFormat(SourceFormat.NEXUS);
		CommonTree astTree = (CommonTree)result.getTree();
		if (astTree != null) {
			int startOfBlocks = 0;

			if (parseProvenance && astTree.getChild(0) != null && astTree.getChild(0).getType() == NexusBlockParser.PROVENANCE) {
				String rawComment = astTree.getChild(0).getChild(0).getText();
				// Strip off the outside brackets
				fileData.getProvenance().add(rawComment.substring(1, rawComment.length() - 1));
				startOfBlocks++;
			}
			for (int i = startOfBlocks; i < astTree.getChildCount(); i++) {
				Block newBlock;
				String blockType = astTree.getChild(i).getText();
				if ("TAXA".equalsIgnoreCase(blockType)) {
					String blockContent = tokens.toString(astTree.getChild(i).getTokenStartIndex() + 4, astTree.getChild(i).getTokenStopIndex() - 2);
					newBlock = parseTaxaBlock(blockContent);
				} else if ("TREES".equalsIgnoreCase(blockType)) {
					String blockContent = tokens.toString(astTree.getChild(i).getTokenStartIndex() + 4, astTree.getChild(i).getTokenStopIndex() - 2);
					newBlock = parseTreesBlock(blockContent);
				} else if ("CHARACTERS".equalsIgnoreCase(blockType)) {
					String blockContent = tokens.toString(astTree.getChild(i).getTokenStartIndex() + 4, astTree.getChild(i).getTokenStopIndex() - 2);
					if (blockContent.toUpperCase().contains("CONTINUOUS")) {
						newBlock = parseCharactersBlock(blockContent);
					} else {
						String type = astTree.getChild(i).getText();
						newBlock = parseUnknownBlock(blockContent, type);
					}
				} else if (!parseProvenance && astTree.getChild(i).getType() == NexusBlockParser.PROVENANCE) { // treat it like it is a comment
					String blockContent = astTree.getChild(i).getChild(0).getText();
					blockContent = blockContent.substring(1, blockContent.length() - 1);
					newBlock = parseCommentBlock(blockContent);
				}
				else if (astTree.getChild(i).getType() == NexusBlockParser.COMMENT) {
					String blockContent = astTree.getChild(i).getText();
					blockContent = blockContent.substring(1, blockContent.length() - 1);
					newBlock = parseCommentBlock(blockContent);
				} else {
					String blockContent = tokens.toString(astTree.getChild(i).getTokenStartIndex() + 4, astTree.getChild(i).getTokenStopIndex() - 2);
					String type = astTree.getChild(i).getText();
					newBlock = parseUnknownBlock(blockContent, type);
				}
				fileData.getBlocks().add(newBlock);
			}
		}
		return fileData;
	}

	private Block parseUnknownBlock(String blockContent, String type) {
		UnknownBlock unkBlock = new UnknownBlock();
		unkBlock.setType(type);
		unkBlock.setContent(blockContent);
		return unkBlock;
	}

	private Block parseCommentBlock(String blockContent) {
		Comment comment = new Comment();
		comment.setContent(blockContent);
		return comment;
	}

	private TaxaBlock parseTaxaBlock(String blockContent) throws IOException, ParserException {
		TaxaBlock taxaBlock = new TaxaBlock();
		CharStream taxaCs = new ANTLRReaderStream(new StringReader(blockContent));
	    NexusTaxaLexer taxaLexer = new NexusTaxaLexer(taxaCs);
	    CommonTokenStream taxaTokens = new CommonTokenStream();
	    taxaTokens.setTokenSource(taxaLexer);
	    NexusTaxaParser treesParser =
	    	new NexusTaxaParser(taxaTokens);
	    RuleReturnScope taxaResult;
		try {
			taxaResult = treesParser.block_taxa();
			if (treesParser.getNumberOfSyntaxErrors() + taxaLexer.getNumberOfSyntaxErrors() > 0) {
				throw new ParserException("Error parsing TAXA block of Nexus file");
			}
		} catch (RecognitionException e) {
			throw new ParserException(e);
		}
		TaxaModelConstructorVisitorAction mcva = new TaxaModelConstructorVisitorAction();
		mcva.setTokens(taxaTokens);
		if (taxaResult.getTree() != null) {
			new TreeVisitor().visit(taxaResult.getTree(), mcva);
		}
		taxaBlock.setStatements(mcva.getStatements());
		return taxaBlock;
	}

	private TreesBlock parseTreesBlock(String blockContent) throws IOException, ParserException {
		TreesBlock treesBlock = new TreesBlock();
		CharStream treesCs = new ANTLRReaderStream(new StringReader(blockContent));
        NexusTreesLexer treesLexer = new NexusTreesLexer(treesCs);
        CommonTokenStream treesTokens = new CommonTokenStream();
        treesTokens.setTokenSource(treesLexer);
        NexusTreesParser treesParser =
        	new NexusTreesParser(treesTokens);
        RuleReturnScope treesResult;
		try {
			treesResult = treesParser.block_trees();
			if (treesParser.getNumberOfSyntaxErrors() + treesLexer.getNumberOfSyntaxErrors() > 0) {
				throw new ParserException("Error parsing TREES block of Nexus file");
			}
		} catch (RecognitionException e) {
			throw new ParserException(e);
		}
		TreesModelConstructorVisitorAction mcva = new TreesModelConstructorVisitorAction();
		mcva.setTokens(treesTokens);
		if (treesResult.getTree() != null) {
			new TreeVisitor().visit(treesResult.getTree(), mcva);
		}
		treesBlock.setStatements(mcva.getStatements());
		return treesBlock;
	}

	private Block parseCharactersBlock(String blockContent) throws IOException, ParserException {
		CharactersBlock charsBlock = new CharactersBlock();
		CharStream cs = new ANTLRReaderStream(new StringReader(blockContent));
        NexusCharactersLexer lexer = new NexusCharactersLexer(cs);
        CommonTokenStream tokens = new CommonTokenStream();
        tokens.setTokenSource(lexer);
        NexusCharactersParser parser =
        	new NexusCharactersParser(tokens);
        RuleReturnScope result;
		try {
			result = parser.block_characters();
			if (parser.getNumberOfSyntaxErrors() + lexer.getNumberOfSyntaxErrors() > 0) {
				throw new ParserException("Error parsing CHARACTERS block of Nexus file");
			}
		} catch (RecognitionException e) {
			throw new ParserException(e);
		}
		CharactersModelConstructorVisitorAction mcva = new CharactersModelConstructorVisitorAction();
		mcva.setTokens(tokens);
		if (result.getTree() != null) {
			new TreeVisitor().visit(result.getTree(), mcva);
		}
		charsBlock.setStatements(mcva.getStatements());
		return charsBlock;
	}
}
