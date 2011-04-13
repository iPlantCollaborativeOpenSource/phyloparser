package org.iplantc.phyloparser.parser.treevisitor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.TreeVisitor;

import junit.framework.TestCase;

public class TestAstSerializationVisitorAction extends TestCase {
	public void testOneNode() throws Throwable {
		CommonTree ct = new CommonTree();
		ct.token = new CommonToken(0, "hello");
		AstSerializationVisitorAction asva = new AstSerializationVisitorAction();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		asva.setOutputStream(baos);
		new TreeVisitor().visit(ct, asva);
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
		CommonToken readToken = (CommonToken) ois.readObject();
		assertEquals(0, readToken.getType());
		assertEquals("hello", readToken.getText());
		assertEquals(ct.token.getTokenIndex(), readToken.getTokenIndex());
		assertEquals(0, ois.readInt());
	}
	
	public void testOneChild() throws Throwable {
		CommonTree ct = new CommonTree();
		ct.token = new CommonToken(0, "hello");
		CommonTree child = new CommonTree();
		child.token = new CommonToken(1, "byebye");
		ct.addChild(child);
		AstSerializationVisitorAction asva = new AstSerializationVisitorAction();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		asva.setOutputStream(baos);
		new TreeVisitor().visit(ct, asva);
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
		CommonToken readToken = (CommonToken) ois.readObject();
		assertEquals(0, readToken.getType());
		assertEquals("hello", readToken.getText());
		assertEquals(ct.token.getTokenIndex(), readToken.getTokenIndex());
		assertEquals(1, ois.readInt());
		CommonToken readToken2 = (CommonToken) ois.readObject();
		assertEquals(1, readToken2.getType());
		assertEquals("byebye", readToken2.getText());
		assertEquals(child.token.getTokenIndex(), readToken2.getTokenIndex());
		assertEquals(0, ois.readInt());		
	}
	
	public void testDepthFirst() throws Throwable {
		CommonTree gp = new CommonTree();
		gp.token = new CommonToken(0, "hello");
		CommonTree parent1 = new CommonTree();
		parent1.token = new CommonToken(1, "byebye");
		gp.addChild(parent1);
		CommonTree parent2 = new CommonTree();
		parent2.token = new CommonToken(2, "what?");
		gp.addChild(parent2);
		CommonTree child = new CommonTree();
		child.token = new CommonToken(3, "I'm the baby!");
		parent1.addChild(child);
		AstSerializationVisitorAction asva = new AstSerializationVisitorAction();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		asva.setOutputStream(baos);
		new TreeVisitor().visit(gp, asva);
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
		CommonToken readToken = (CommonToken) ois.readObject();
		assertEquals(0, readToken.getType());
		assertEquals("hello", readToken.getText());
		assertEquals(gp.token.getTokenIndex(), readToken.getTokenIndex());
		assertEquals(2, ois.readInt());
		CommonToken readToken2 = (CommonToken) ois.readObject();
		assertEquals(1, readToken2.getType());
		assertEquals("byebye", readToken2.getText());
		assertEquals(parent1.token.getTokenIndex(), readToken2.getTokenIndex());
		assertEquals(1, ois.readInt());		
		CommonToken readToken3 = (CommonToken) ois.readObject();
		assertEquals(3, readToken3.getType());
		assertEquals("I'm the baby!", readToken3.getText());
		assertEquals(child.token.getTokenIndex(), readToken3.getTokenIndex());
		assertEquals(0, ois.readInt());		
		CommonToken readToken4 = (CommonToken) ois.readObject();
		assertEquals(2, readToken4.getType());
		assertEquals("what?", readToken4.getText());
		assertEquals(parent2.token.getTokenIndex(), readToken4.getTokenIndex());
		assertEquals(0, ois.readInt());		
	}
}
