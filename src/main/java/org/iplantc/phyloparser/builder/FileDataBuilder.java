package org.iplantc.phyloparser.builder;

import java.util.List;

import org.iplantc.phyloparser.model.FileData;
import org.iplantc.phyloparser.model.Matrix;
import org.iplantc.phyloparser.model.Tree;
import org.iplantc.phyloparser.model.block.CharactersBlock;
import org.iplantc.phyloparser.model.block.TreesBlock;
import org.iplantc.phyloparser.model.statement.CharStateLabelsStatement;
import org.iplantc.phyloparser.model.statement.DimensionsCharStatement;
import org.iplantc.phyloparser.model.statement.FormatStatement;
import org.iplantc.phyloparser.model.statement.MatrixStatement;
import org.iplantc.phyloparser.model.statement.TreeStatement;

public class FileDataBuilder {

	public FileData buildFileDataFromTrees(List<Tree> trees) {
		FileData fd = new FileData();
		
		if (trees != null && trees.size() > 0) {
			TreesBlock treesBlock = new TreesBlock();
			fd.getBlocks().add(treesBlock);				
			for (Tree tree : trees) {
				TreeStatement treeStatement = new TreeStatement(tree);
				treesBlock.getStatements().add(treeStatement);
			}
		}

		return fd;
	}

	public FileData buildFileDataFromMatrix(Matrix matrix) {
		FileData fd = new FileData();
		
		if (matrix != null) {
			CharactersBlock charsBlock = new CharactersBlock();
			DimensionsCharStatement statement1 = new DimensionsCharStatement(matrix.getCharacterLabels());
			FormatStatement statement2 = new FormatStatement(matrix.getDataType());
			CharStateLabelsStatement statement3 = new CharStateLabelsStatement(matrix.getCharacterLabels());
			MatrixStatement statement4 = new MatrixStatement(matrix.getCharacterMatrix(), matrix.getTaxaLabels());
			fd.getBlocks().add(charsBlock);
			charsBlock.getStatements().add(statement1);
			charsBlock.getStatements().add(statement2);
			charsBlock.getStatements().add(statement3);
			charsBlock.getStatements().add(statement4);
		}
		
		return fd;
	}

}
