package org.iplantc.phyloparser.parser.treevisitor;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.TreeVisitorAction;

public class AstSerializationVisitorAction implements TreeVisitorAction {

	private OutputStream sourceOutputStream;
	private ObjectOutputStream outputStream;
	
	public Object post(Object arg0) {
		try {
			outputStream.flush();
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
		return arg0;
	}

	public Object pre(Object arg0) {
		if (outputStream == null) {
			try {
				outputStream = new ObjectOutputStream(sourceOutputStream);
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}

		try {
			outputStream.writeObject(((CommonTree)arg0).getToken());
			outputStream.writeInt(((CommonTree)arg0).getChildCount());
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
		
		return arg0;
	}

	public void setOutputStream(OutputStream os) {
		this.sourceOutputStream = os;
		outputStream = null;
	}

}
