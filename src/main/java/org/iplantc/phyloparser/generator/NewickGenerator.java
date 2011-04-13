package org.iplantc.phyloparser.generator;

import java.io.StringWriter;

import org.iplantc.phyloparser.exception.GeneratorException;
import org.iplantc.phyloparser.model.FileData;
import org.iplantc.phyloparser.model.Tree;
import org.iplantc.phyloparser.model.block.Block;
import org.iplantc.phyloparser.model.block.TreesBlock;
import org.iplantc.phyloparser.util.TreeToNewickTransformer;

public class NewickGenerator {
	public String generate(FileData fd) throws GeneratorException {
		StringWriter writer = new StringWriter();
		generate(fd, writer);
		return writer.toString();
	}

	public void generate(FileData fd, StringWriter writer) throws GeneratorException {
		if (fd.getBlocks() != null) {
			for (Block block : fd.getBlocks()) {
				if (block instanceof TreesBlock) {
					TreesBlock treesBlock = (TreesBlock)block;
					if (treesBlock.getTrees() != null) {
						for (Tree tree : treesBlock.getTrees()) {
							writer.write(new TreeToNewickTransformer().transform(tree));
							writer.write(";\n");						
						}
					}
				}				
			}
		}
	}
}
