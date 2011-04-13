grammar NexusCharacters;

options {
	output=AST;
}

tokens {
	CHARLABEL;
	UNRECOGNIZED;
}

@header {
	package org.iplantc.phyloparser.parser.antlr;
}

@lexer::header {
	package org.iplantc.phyloparser.parser.antlr;
}

@members {
	int charlabelIndex = 1;
}

@lexer::members {
	boolean hideComments = false;
}

block_characters
	:	characters_statement* EOF;

characters_statement
	:	(DIMENSIONS NCHAR EQUALS nchar=PINTEGER (NTAX EQUALS PINTEGER)? SEMI) -> ^(DIMENSIONS NCHAR[$nchar.text])
	|	format_statement
	|	(MATRIX matrix_word* SEMI) -> ^(MATRIX matrix_word*)
	|	(CHARSTATELABELS charstatelabel? (COMMA charstatelabel)* SEMI) -> ^(CHARSTATELABELS charstatelabel*)
	|	(CHARLABELS charlabel* SEMI) -> ^(CHARLABELS charlabel*)
	|	COMMENT
	|	unrecognized_statement;

format_statement
    :   (FORMAT format_subcommand*) -> ^(FORMAT format_subcommand*);

format_subcommand
    :   datatype_statement
    |   gap_statement
    |   missing_statement
    |   symbols_statement;

datatype_statement
    :   (DATATYPE EQUALS generic_word) -> ^(DATATYPE generic_word);

gap_statement
    :   (GAP EQUALS gap_value) -> ^(GAP gap_value);

gap_value
    :   PUNCT | QUOTED_WORD;

missing_statement
    :   (MISSING EQUALS missing_value) -> ^(MISSING missing_value);

missing_value
    :   PUNCT | QUOTED_WORD;

symbols_statement
    :   (SYMBOLS EQUALS symbols_value) -> ^(SYMBOLS symbols_value);

symbols_value
    :   QUOTED_WORD;

unrecognized_statement
	:	(stmt=unrecognized_statement_word (body+=unrecognized_statement_body)*) SEMI -> ^(UNRECOGNIZED[$stmt.text] $body*)
	|	SEMI;

unrecognized_statement_word
	:	~(DIMENSIONS | FORMAT | MATRIX | CHARSTATELABELS | CHARLABELS | COMMENT | SEMI);

unrecognized_statement_body
	:	~SEMI;


charlabel
	:	generic_word -> ^(CHARLABEL {new CommonTree(new CommonToken(PINTEGER, String.valueOf(charlabelIndex++)))} generic_word);

charstatelabel
	:	PINTEGER generic_word -> ^(CHARLABEL PINTEGER generic_word);

matrix_word
	:	number
	|	missing
	|	generic_word;

number	:	PINTEGER -> FLOAT[$text]
	|	FLOAT;

missing	:	QUESTION;

generic_word
	:	SINGLE_QUOTED_WORD
	|	QUOTED_WORD
	|	( WORD | PINTEGER | FLOAT | DIMENSIONS | NTAX | NCHAR | MATRIX | QUESTION ) -> WORD[$text];

COMMENT	:	'[' (~('['|']') | COMMENT)* ']' {if (hideComments) $channel=HIDDEN;};

QUOTED_WORD
	:	'"' (options {greedy = false;} : .)* '"' {hideComments = true;};

SINGLE_QUOTED_WORD
	:	'\'' (( '\'' '\'' ) => '\'' '\'' | ~'\'')* '\'' {hideComments = true;};

// case-insensitive dimensions
DIMENSIONS
	:	('D'|'d')('I'|'i')('M'|'m')('E'|'e')('N'|'n')('S'|'s')('I'|'i')('O'|'o')('N'|'n')('S'|'s') {hideComments = true;};

// case-insensitive ntax
NTAX	:	('N'|'n')('T'|'t')('A'|'a')('X'|'x') {hideComments = true;};

// case-insensitive ntax
NCHAR	:	('N'|'n')('C'|'c')('H'|'h')('A'|'a')('R'|'r') {hideComments = true;};

FORMAT	:	('F'|'f')('O'|'o')('R'|'r')('M'|'m')('A'|'a')('T'|'t') {hideComments = true;};

DATATYPE:	('D'|'d')('A'|'a')('T'|'t')('A'|'a')('T'|'t')('Y'|'y')('P'|'p')('E'|'e') {hideComments = true;};

GAP     :   ('G'|'g')('A'|'a')('P'|'p') {hideComments = true;};

MISSING :   ('M'|'m')('I'|'i')('S'|'s')('S'|'s')('I'|'i')('N'|'n')('G'|'g') {hideComments = true;};

SYMBOLS :   ('S'|'s')('Y'|'y')('M'|'m')('B'|'b')('O'|'o')('L'|'l')('S'|'s') {hideComments = true;};

MATRIX	:	('M'|'m')('A'|'a')('T'|'t')('R'|'r')('I'|'i')('X'|'x') {hideComments = true;};

CHARSTATELABELS
	:	('C'|'c')('H'|'h')('A'|'a')('R'|'r')('S'|'s')('T'|'t')('A'|'a')('T'|'t')('E'|'e')('L'|'l')('A'|'a')('B'|'b')('E'|'e')('L'|'l')('S'|'s') {hideComments = true;};

CHARLABELS
	:	('C'|'c')('H'|'h')('A'|'a')('R'|'r')('L'|'l')('A'|'a')('B'|'b')('E'|'e')('L'|'l')('S'|'s') {hideComments = true;};

SEMI	:	';' {hideComments = false;};

EQUALS	:	'=' {hideComments = true;};

COMMA	:	',' {hideComments = true;};

QUESTION:	'?' {hideComments = true;};

PINTEGER:	('1'..'9')('0'..'9')* {hideComments = true;};

FLOAT	:	('+'|'-')?((('0'..'9')* '.' ('0'..'9')+) | (('0'..'9')+ '.'?))(('E'|'e') ('+'|'-')? ('0'..'9')+)? {hideComments = true;};

WORD	:	(~(' '|'\t'|'\r'|'\n'|'['|']'|'('|')'|':'|';'|'='|','))+ {hideComments = true;};

WS	:	(' '|'\t'|'\r'|'\n')+ {$channel=HIDDEN;};

PUNCT   :   ('~'|'@'|'#'|'$'|'%'|'^'|'&'|'*'|'('|')'|'-'|'_'|'='|'+'|'{'|'}'|'['|']'|'|'|'\\'|':'|'<'|','|'>'|'.'|'?'|'/');
