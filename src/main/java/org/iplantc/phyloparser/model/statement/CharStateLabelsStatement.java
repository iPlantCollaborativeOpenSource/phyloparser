package org.iplantc.phyloparser.model.statement;

import java.util.List;

public class CharStateLabelsStatement implements Statement {
	private List<String> characterLabels;

	public CharStateLabelsStatement(List<String> charLabels) {
		this.characterLabels = charLabels;
	}

	public void setCharacterLabels(List<String> characterLabels) {
		this.characterLabels = characterLabels;
	}

	public List<String> getCharacterLabels() {
		return characterLabels;
	}
}
