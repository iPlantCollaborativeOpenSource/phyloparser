grammar Newick;

options {
	output=AST;
	backtrack=true;
}

tokens {
	NODE;
	LABEL;
	LENGTH;
	ANON;
	TREES;
	TREE;
}

@header {
	package org.iplantc.phyloparser.parser.antlr;
}

@lexer::header {
	package org.iplantc.phyloparser.parser.antlr;
}

newick	:	newicktree+ EOF -> ^(TREES newicktree+);

newicktree
	:	node ';' -> ^(TREE node);

// Why $nlabel.start != null and not $nlabel.text != null?
// It appears (at least in the Java ANTLR API, that $nlabel.text
// builds a string, which could get rather large in files with, say,
// 50K taxa.  Checking the start token for null avoids that
node
	:	('(' node (',' node)* ')')? nlabel? ( ':' FLOAT )? NHX_ANNOTATION? -> {$nlabel.start != null}? ^(nlabel ^(LENGTH FLOAT)? NHX_ANNOTATION? node*)
		-> ^(ANON ^(LENGTH FLOAT)? NHX_ANNOTATION? node*);

nlabel	:	ID | FLOAT -> ID[$text]
	|	QUOTED_ID;

QUOTED_ID
	:	'\'' (( '\'' '\'' ) => '\'' '\'' | ~'\'')* '\'';

FLOAT	:	('+'|'-')?((('0'..'9')* '.' ('0'..'9')+) | (('0'..'9')+ '.'?))(('E'|'e') ('+'|'-')? ('0'..'9')+)?;

ID	:	(~(' '|'\t'|'\n'|'\r'|'('|')'|'['|']'|'\''|':'|';'|','))+ ;

NHX_ANNOTATION
	:	('[' '&&NHX') => '[' '&&NHX' (~']')* ']';

COMMENT	:	'[' (~']')* ']' {$channel=HIDDEN;};

WS	:	(' ' |'\t' |'\n' |'\r' )+ {$channel=HIDDEN;};
