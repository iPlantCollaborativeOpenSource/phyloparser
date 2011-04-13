grammar NexusBlock;

options {
	output=AST;
}

tokens {
	PROVENANCE;
}

@header {
	package org.iplantc.phyloparser.parser.antlr;
}

@lexer::header {
	package org.iplantc.phyloparser.parser.antlr;
}

nexus	:	NEXUS (prov=COMMENT others+=COMMENT*)? block_with_comments* EOF -> ^(NEXUS ^(PROVENANCE $prov)? $others* block_with_comments*);

block_with_comments
	:	block COMMENT*;

block	:	BEGIN word SEMI statement* END SEMI -> ^(word statement*);

statement
	:	(statement_word word*)? SEMI -> {$statement_word.start != null}? ^(statement_word word*)
		-> SEMI
	|	COMMENT;

statement_word
	:	WORD | BEGIN;

word	:	WORD | COMMENT | BEGIN | END;

COMMENT	:	COMMENT_BLOCK;

fragment COMMENT_BLOCK
	:	'[' (~('['|']') | COMMENT_BLOCK)* ']';
	
// case-insensitive #NEXUS
NEXUS	:	'#'('N'|'n')('E'|'e')('X'|'x')('U'|'u')('S'|'s');

// case-insensitive begin
BEGIN	:	('B'|'b')('E'|'e')('G'|'g')('I'|'i')('N'|'n');

// case-insensitive end|endblock
END	:	('E'|'e')('N'|'n')('D'|'d')(('B'|'b')('L'|'l')('O'|'o')('C'|'c')('K'|'k'))?;

WORD	:	(~(' '|'\t'|'\r'|'\n'|SEMI|'['|']'))+;

SEMI	:	';';

WS	:	(' '|'\t'|'\r'|'\n')+ {$channel=HIDDEN;};
