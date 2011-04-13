grammar PhylipSequence;

options {
	output=AST;
}

tokens {
	NTAX;
	NCHAR;
	SEQUENCE;
	SEQUENCES;
	INTERLEAVE;
	TNAME;
}

@header {
	package org.iplantc.phyloparser.parser.antlr;
}

@lexer::header {
	package org.iplantc.phyloparser.parser.antlr;
}

phylip	:	WS? ntax=PINTEGER WS nchar=PINTEGER ignoreuntilnl phylip_body EOF -> ^(NTAX $ntax) ^(NCHAR $nchar) ^(SEQUENCES phylip_body);

phylip_body
	:	taxa_block (NL! nontaxa_block)*;

taxa_block
@init {
	String taxonName = new String();
}
	:	(taxon_name (SEQUENCE_CHAR|WS)+ NL)+ -> ^(SEQUENCE taxon_name SEQUENCE_CHAR+)+;

nontaxa_block
	:	(( options {greedy = false;} : .)* SEQUENCE_CHAR (SEQUENCE_CHAR|WS)* NL)+ -> ^(INTERLEAVE SEQUENCE_CHAR+);

taxon_name
@init {
	String taxonName = new String();
}
	:	( {taxonName.length() < 10}? => (SEQUENCE_CHAR|TAXON_CHAR|PINTEGER) {taxonName = $text;})+  WS? -> {new CommonTree(new CommonToken(TNAME,taxonName))};


ignoreuntilnl
	:	.* NL;

PINTEGER
	:	('0'..'9')('0'..'9')*;

SEQUENCE_CHAR
	:	('A'..'Z')|('a'..'z')|'-'|'?';

TAXON_CHAR
	:	('A'..'Z')|('a'..'z')|('0'..'9')|'_'|'-'|'?';

WS	:	(' '|'\t')+;

NL	:	('\r'|'\r''\n'|'\n');

CHAR	:	.;
