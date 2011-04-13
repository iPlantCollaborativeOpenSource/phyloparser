grammar NexusTaxa;

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

block_taxa
	:	taxa_statement* EOF;
	
taxa_statement
	:	(DIMENSIONS NTAX EQUALS PINTEGER SEMI) -> ^(DIMENSIONS NTAX[$PINTEGER.text])
	|	(TAXLABELS taxlabels_body? SEMI) -> ^(TAXLABELS taxlabels_body?)
	|	COMMENT
	|	unrecognized_statement;

unrecognized_statement
	:	(stmt=unrecognized_statement_word (body+=unrecognized_statement_body)*) SEMI -> ^(UNRECOGNIZED[$stmt.text] $body*)
	|	SEMI;

unrecognized_statement_word
	:	~(DIMENSIONS | TAXLABELS | COMMENT | SEMI);

unrecognized_statement_body
	:	~SEMI;

taxlabels_body
	:	generic_word (COMMA!? generic_word)*;

generic_word
	:	SINGLE_QUOTED_WORD
	|	QUOTED_WORD
	|	( WORD | PINTEGER | EQUALS | TAXA | DIMENSIONS | NTAX | TAXLABELS ) -> WORD[$text];

COMMENT	:	'[' (~('['|']') | COMMENT)* ']' {if (hideComments) $channel=HIDDEN;};

QUOTED_WORD 
	:	'"' (options {greedy = false;} : .)* '"' {hideComments = true;};

SINGLE_QUOTED_WORD 
	:	'\'' (( '\'' '\'' ) => '\'' '\'' | ~'\'')* '\'' {hideComments = true;};

// case-insensitive taxa
TAXA	:	('T'|'t')('A'|'a')('X'|'x')('A'|'a') {hideComments = true;};

// case-insensitive dimensions
DIMENSIONS
	:	('D'|'d')('I'|'i')('M'|'m')('E'|'e')('N'|'n')('S'|'s')('I'|'i')('O'|'o')('N'|'n')('S'|'s') {hideComments = true;};

// case-insensitive ntax
NTAX	:	('N'|'n')('T'|'t')('A'|'a')('X'|'x') {hideComments = true;};

// case-insensitive taxlabels
TAXLABELS
	:	('T'|'t')('A'|'a')('X'|'x')('L'|'l')('A'|'a')('B'|'b')('E'|'e')('L'|'l')('S'|'s') {hideComments = true;};

SEMI	:	';' {hideComments = false;};

EQUALS	:	'=' {hideComments = true;};

COMMA	:	',' {hideComments = true;};

PINTEGER:	('1'..'9')('0'..'9')* {hideComments = true;};

WORD	:	(~(' '|'\t'|'\r'|'\n'|'['|']'|'('|')'|':'|';'|'='|','))+ {hideComments = true;};

WS	:	(' '|'\t'|'\r'|'\n')+ {$channel=HIDDEN;};
