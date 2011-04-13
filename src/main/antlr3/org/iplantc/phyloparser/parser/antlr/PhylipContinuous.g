grammar PhylipContinuous;

options {
	output=AST;
}

tokens {
	NTAX;
	NCHAR;
	DATA;
}

@header {
	package org.iplantc.phyloparser.parser.antlr;
}

@lexer::header {
	package org.iplantc.phyloparser.parser.antlr;
}

phylip	:	WS? ntax=PINTEGER WS nchar=PINTEGER ignoreuntilnl phylip_line* EOF -> ^(NTAX[$ntax]) ^(NCHAR[$nchar]) ^(DATA phylip_line*);

phylip_line
	:	taxon_name traits+=trait_value (WS traits+=trait_value)* WS? NL -> ^(taxon_name $traits*);

taxon_name
	:	(TAXON_NAME padding=WS?) {$TAXON_NAME.text.length() + (($padding == null) ? 0 : $padding.text.length()) == 30}? -> TAXON_NAME;

ignoreuntilnl
	:	.* NL;

trait_value
	:	PINTEGER
	|	FLOAT;

// The fake semantic predicates are necessary in order to preserve rule ordering,
// as any rule with a semantic predicate is lifted in priority above ones without
// them
PINTEGER
	:	{true}?=> ('0'..'9')('0'..'9')*;

FLOAT	:	{true}?=> ('+'|'-')?((('0'..'9')* '.' ('0'..'9')+) | (('0'..'9')+ '.'?))(('E'|'e') ('+'|'-')? ('0'..'9')+)?;

TAXON_NAME
	:	({$text.length() < 30}? => TAXON_CHAR)+;

fragment TAXON_CHAR
	:	('A'..'Z')|('a'..'z')|('0'..'9')|'_'|'\\'|'/'|'?'|'@'|'#'|'$'|'%'|'^'|'&'|'*'|'-'|'+'|'|'|'"'|'\''|'<'|'>'|'!'|'.';

WS	:	(' '|'\t')+;

NL	:	('\r''\n'|'\r'|'\n');
