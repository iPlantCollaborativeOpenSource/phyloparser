package org.iplantc.phyloparser.generator;

import java.io.StringWriter;
import java.util.List;

import org.iplantc.phyloparser.exception.GeneratorException;
import org.iplantc.phyloparser.model.FileData;
import org.iplantc.phyloparser.model.Tree;
import org.iplantc.phyloparser.model.block.Block;
import org.iplantc.phyloparser.model.block.CharactersBlock;
import org.iplantc.phyloparser.model.block.TaxaBlock;
import org.iplantc.phyloparser.model.block.TreesBlock;
import org.iplantc.phyloparser.model.block.UnknownBlock;
import org.iplantc.phyloparser.model.statement.CharStateLabelsStatement;
import org.iplantc.phyloparser.model.statement.Comment;
import org.iplantc.phyloparser.model.statement.DimensionsCharStatement;
import org.iplantc.phyloparser.model.statement.DimensionsTaxaStatement;
import org.iplantc.phyloparser.model.statement.FormatStatement;
import org.iplantc.phyloparser.model.statement.MatrixStatement;
import org.iplantc.phyloparser.model.statement.Statement;
import org.iplantc.phyloparser.model.statement.TaxlabelsStatement;
import org.iplantc.phyloparser.model.statement.TreeStatement;
import org.iplantc.phyloparser.model.statement.UnrecognizedStatement;
import org.iplantc.phyloparser.util.TreeToNewickTransformer;

public class NexusGenerator {
	public String generate(FileData fd) throws GeneratorException {
		StringWriter writer = new StringWriter();
		generate(fd, writer);
		return writer.toString();
	}

	public void generate(FileData fd, StringWriter writer) throws GeneratorException {
		writer.write("#NEXUS\n");

		if (fd.getProvenance() != null) {
			if (fd.getProvenance().size() > 0) {
				writer.write("\n[");
				for (String provenance : fd.getProvenance()) {
					writer.write("\n");
					writer.write(provenance);
				}
				writer.write("\n]\n");
			}
		}

		for (Block block : fd.getBlocks()) {
			if (block instanceof TaxaBlock) {
				TaxaBlock taxaBlock = (TaxaBlock) block;
				writer.write("\nBEGIN TAXA;\n");
				for (Statement stmt : taxaBlock.getStatements()) {
					if (stmt instanceof DimensionsTaxaStatement) {
						writer.write("  DIMENSIONS NTAX=");
						writer.write(String.valueOf(taxaBlock.getTaxaLabels().size()));
						writer.write(";\n");
					} else if (stmt instanceof TaxlabelsStatement) {
						writer.write("  TAXLABELS");
						for (String taxonLabel : taxaBlock.getTaxaLabels()) {
							writer.write(" ");
							writer.write(quote(taxonLabel));
						}
						writer.write(";\n");
					} else if (stmt instanceof UnrecognizedStatement) {
						writer.write("  ");
						writer.write(((UnrecognizedStatement)stmt).getContent().replaceAll("\r\n?", "\n"));
						writer.write("\n");
					} else if (stmt instanceof Comment) {
						writer.write("  [");
						writer.write(((Comment)stmt).getContent().replaceAll("\r\n?", "\n"));
						writer.write("]\n");
					} else {
						throw new GeneratorException("Received unexpected statement in TAXA block");
					}
				}
				writer.write("END;\n");
			} else if (block instanceof TreesBlock) {
				TreesBlock treesBlock = (TreesBlock) block;
				writer.write("\nBEGIN TREES;\n");
				for (Statement stmt : treesBlock.getStatements()) {
					if (stmt instanceof TreeStatement) {
						Tree tree = ((TreeStatement)stmt).getTree();
						writer.write("  TREE ");
						if (tree.getName() != null && tree.getName().length() > 0) {
							writer.write(quote(tree.getName()));
							writer.write(" ");
						}
						writer.write("= ");

						writer.write(new TreeToNewickTransformer().transform(tree));

						writer.write(";\n");
					} else if (stmt instanceof UnrecognizedStatement) {
						writer.write("  ");
						writer.write(((UnrecognizedStatement)stmt).getContent().replaceAll("\r\n?", "\n"));
						writer.write("\n");
					} else if (stmt instanceof Comment) {
						writer.write("  [");
						writer.write(((Comment)stmt).getContent().replaceAll("\r\n?", "\n"));
						writer.write("]\n");
					} else {
						throw new GeneratorException("Received unexpected statement in TREES block");
					}
				}
				writer.write("END;\n");
			} else if (block instanceof CharactersBlock) {
				CharactersBlock charsBlock = (CharactersBlock) block;
				writer.write("\nBEGIN CHARACTERS;\n");
				for (Statement stmt : charsBlock.getStatements()) {
					if (stmt instanceof DimensionsCharStatement) {
						DimensionsCharStatement dcStatement = (DimensionsCharStatement) stmt;
						writer.write("  DIMENSIONS NCHAR=");
						writer.write(String.valueOf(dcStatement.getCharacterLabels().size()));
						writer.write(" NTAX=");
						writer.write(String.valueOf(getTaxaCount(charsBlock.getStatements())));
						writer.write(";\n");
					} else if (stmt instanceof FormatStatement) {
						writer.write("  FORMAT DATATYPE=CONTINUOUS;\n");
					} else if (stmt instanceof CharStateLabelsStatement) {
						CharStateLabelsStatement cslStatement = (CharStateLabelsStatement) stmt;
						writer.write("  CHARLABELS ");
						boolean first = true;
						for (String charLabel : cslStatement.getCharacterLabels()) {
							if (!first) {
								writer.write(" ");
							}
							writer.write(quote(charLabel));
							first = false;
						}
						writer.write(";\n");
					} else if (stmt instanceof MatrixStatement) {
						MatrixStatement matrixStatement = (MatrixStatement) stmt;
						writer.write("  MATRIX\n");
						int maxTaxonLength = 0;
						for (String taxon : matrixStatement.getTaxaLabels()) {
							taxon = quote(taxon);
							if (taxon.length() > maxTaxonLength) {
								maxTaxonLength = taxon.length();
							}
						}
						for (String taxon : matrixStatement.getTaxaLabels()) {
							writer.write(pad(quote(taxon), maxTaxonLength + 1));
							for (Object value : matrixStatement.getCharacterMatrix().get(taxon)) {
								if (value == null) {
									value = "?";
								} else if  (!(value instanceof Number)) {
								    value = quote(value.toString());
								}
								writer.write(" ");
								writer.write(value.toString());
							}
							writer.write("\n");
						}
						writer.write(";\n");
					} else if (stmt instanceof UnrecognizedStatement) {
						writer.write("  ");
						writer.write(((UnrecognizedStatement)stmt).getContent().replaceAll("\r\n?", "\n"));
						writer.write("\n");
					} else if (stmt instanceof Comment) {
						writer.write("  [");
						writer.write(((Comment)stmt).getContent().replaceAll("\r\n?", "\n"));
						writer.write("]\n");
					} else {
						throw new GeneratorException("Received unexpected statement in CHARACTERS block");
					}
				}
				writer.write("END;\n");
			} else if (block instanceof Comment) {
				Comment comment = (Comment) block;
				writer.write("\n[");
				writer.write(comment.getContent().replaceAll("\r\n?","\n"));
				writer.write("]\n");
			} else if (block instanceof UnknownBlock) {
				UnknownBlock unkBlock = (UnknownBlock) block;
				writer.write("\nBEGIN ");
				writer.write(unkBlock.getType());
				writer.write(";");
				writer.write(unkBlock.getContent().replaceAll("\r\n?","\n"));
				writer.write("END;\n");
			}
		}
	}

	private int getTaxaCount(List<Statement> statements) {
		for (Statement stmt : statements) {
			if (stmt instanceof MatrixStatement) {
				MatrixStatement matrixStatement = (MatrixStatement) stmt;
				return matrixStatement.getTaxaLabels().size();
			}
		}
		return 0;
	}

	private String pad(String str, int length) {
		if (str.length() > length) {
			return str;
		}
		char[] padded = new char[length];
		str.getChars(0, str.length(), padded, 0);
		for (int i = str.length(); i < length; i++) {
			padded[i] = ' ';
		}
		return new String(padded);
	}

	private String quote(String taxonLabel) {
		if (taxonLabel == null) {
			return "";
		} else if (taxonLabel.indexOf(' ') != -1) {
			return "'" + taxonLabel.replace("'", "''") + "'";
		} else {
			return taxonLabel;
		}
	}
}
