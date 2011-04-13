grammar NexusTrees;

options {
	output=AST;
}

tokens {
	ROOTED;
	UNROOTED;
	PAIR;
	ANON;
	LENGTH;
	TREE;
	PTREE;
	TREENAME;
	UNRECOGNIZED;
}

@header {
	package org.iplantc.phyloparser.parser.antlr;
}

@lexer::header {
	package org.iplantc.phyloparser.parser.antlr;
}

@lexer::members {
	boolean hideComments = false;
}

block_trees
	:	trees_statement* EOF;

trees_statement
	:	translate
	|	ptree
	|	COMMENT
	|	unrecognized_statement;

unrecognized_statement
	:	(stmt=unrecognized_statement_word (body+=unrecognized_statement_body)*) SEMI -> ^(UNRECOGNIZED[$stmt.text] $body*)
	|	SEMI;

unrecognized_statement_word
	:	~(TRANSLATE | TREETOK | COMMENT | SEMI);

unrecognized_statement_body
	:	~SEMI;


translate
	:	TRANSLATE paired_value_list SEMI -> ^(TRANSLATE paired_value_list);

paired_value_list
	:	generic_word generic_word (COMMA paired_value_list?)? -> ^(PAIR generic_word generic_word) paired_value_list?;

ptree	:	TREETOK STAR? generic_word? EQUALS rooted? newick SEMI -> {$generic_word.start != null}? ^(PTREE ^(TREENAME[$generic_word.text]) rooted? newick)
	->	^(PTREE rooted? newick);

rooted	:	ROOTEDTOK -> ROOTED
	|	UNROOTEDTOK -> UNROOTED;

generic_word
	:	( WORD | QUOTED_WORD | SINGLE_QUOTED_WORD | PINTEGER | FLOAT | EQUALS | TREETOK | TRANSLATE | STAR | COMMA | LPAREN | RPAREN | COLON );

newick	:	node -> ^(TREE node);

// Why $nlabel.start != null and not $nlabel.text != null?
// It appears (at least in the Java ANTLR API, that $nlabel.text
// builds a string, which could get rather large in files with, say,
// 50K taxa.  Checking the start token for null avoids that
node
	:	(LPAREN node (COMMA node)* RPAREN)? nlabel? ( COLON branch_length )? -> {$nlabel.start != null}? ^(nlabel ^(LENGTH branch_length)? node*)
		-> ^(ANON ^(LENGTH branch_length)? node*);

branch_length
	:	FLOAT
	|	PINTEGER;

nlabel	:	SINGLE_QUOTED_WORD
	|	QUOTED_WORD
	|	( WORD | PINTEGER | TREETOK | TRANSLATE | FLOAT ) -> ^(WORD[$text]);

ROOTEDTOK
	:	'[&R]'|'[&r]';
UNROOTEDTOK
	:	'[&U]'|'[&u]';

QUOTED_WORD 
	:	'"' (options {greedy = false;} : .)* '"' {hideComments = true;};

SINGLE_QUOTED_WORD 
	:	'\'' (( '\'' '\'' ) => '\'' '\'' | ~'\'')* '\'' {hideComments = true;};

COMMENT	:	'[' (~('['|']') | COMMENT)* ']' {if (hideComments) $channel=HIDDEN;};

TRANSLATE
	:	('T'|'t')('R'|'r')('A'|'a')('N'|'n')('S'|'s')('L'|'l')('A'|'a')('T'|'t')('E'|'e') {hideComments = true;};

TREETOK	:	('T'|'t')('R'|'r')('E'|'e')('E'|'e') {hideComments = true;};

SEMI	:	';' {hideComments = false;};

EQUALS	:	'=' {hideComments = true;};

STAR	:	'*' {hideComments = true;};

COMMA	:	',' {hideComments = true;};

LPAREN	:	'(' {hideComments = true;};

RPAREN	:	')' {hideComments = true;};

COLON	:	':' {hideComments = true;};

PINTEGER:	('1'..'9')('0'..'9')* {hideComments = true;};

FLOAT	:	('+'|'-')?((('0'..'9')* '.' ('0'..'9')+) | (('0'..'9')+ '.'?))(('E'|'e') ('+'|'-')? ('0'..'9')+)? {hideComments = true;};

WORD	:	(~(' '|'\t'|'\r'|'\n'|'['|']'|'('|')'|':'|';'|'='|','))+ {hideComments = true;};

WS	:	(' '|'\t'|'\r'|'\n')+ {$channel=HIDDEN;};
