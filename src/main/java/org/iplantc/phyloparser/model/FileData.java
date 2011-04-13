package org.iplantc.phyloparser.model;

import java.util.LinkedList;
import java.util.List;

import org.iplantc.phyloparser.model.block.Block;

public class FileData {
	private List<Block> blocks = new LinkedList<Block>();
	private List<String> provenance = new LinkedList<String>();
	private String name;
	private int sourceFormat;
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setSourceFormat(int sourceFormat) {
		this.sourceFormat = sourceFormat;
	}

	public int getSourceFormat() {
		return sourceFormat;
	}

	public void setBlocks(List<Block> blocks) {
		this.blocks = blocks;
	}

	public List<Block> getBlocks() {
		return blocks;
	}

	public void setProvenance(List<String> provenance) {
		this.provenance = provenance;
	}

	public List<String> getProvenance() {
		return provenance;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[name=");
		builder.append(name);
		builder.append(",blocks=");
		builder.append(blocks);
		builder.append(",provenance=");
		builder.append(provenance);
		builder.append("]");
		return builder.toString();
	}
}
