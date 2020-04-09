/**
	JFlex scanner for NanoMorpho based on a scanner for NanoLisp.
 */

import java.io.*;

%%

%public
%class NanoMorphoLexer
%unicode
%byaccj
%line
%column

%{

// This part becomes a verbatim part of the program text inside
// the class, NanoLexer.java, that is generated.

public NanoMorphoParser yyparser;

public NanoMorphoLexer(Reader r, NanoMorphoParser yyparser)
{
    this(r);
    this.yyparser = yyparser;
}

public String getLexeme()
{
        return yytext();
}

public int getLine()
{
        return yyline;
}

public int getColumn()
{
        return yycolumn;
}

%}

  /* Reglulegar skilgreiningar */

  /* Regular definitions */

_DIGIT=[0-9]
_FLOAT={_DIGIT}+\.{_DIGIT}+([eE][+-]?{_DIGIT}+)?
_INT={_DIGIT}+
_STRING=\"([^\"\\]|\\b|\\t|\\n|\\f|\\r|\\\"|\\\'|\\\\|(\\[0-3][0-7][0-7])|\\[0-7][0-7]|\\[0-7])*\"
_CHAR=\'([^\'\\]|\\b|\\t|\\n|\\f|\\r|\\\"|\\\'|\\\\|(\\[0-3][0-7][0-7])|(\\[0-7][0-7])|(\\[0-7]))\'
_DELIM=[(){},;=]
_NAME=([:letter:]|{_DIGIT}|[_])+
_BOOLEAN="!"|"&&"|"||"
_OPNAME=([\+\-*/!%=><\:\^\~&|?])+

%%

  /* Lesgreiningarreglur */
  /* Scanning rules */

{_BOOLEAN} {
        yyparser.yylval = new NanoMorphoParserVal(yytext());
        String lexeme = yyparser.yylval.sval;
        int tok = NanoMorphoParser.YYERRCODE;
        switch(lexeme)
        {
            case "&&":
                tok = NanoMorphoParser.AND;
                break;
            case "||":
                tok = NanoMorphoParser.OR;
                break;
            case "!":
                tok = NanoMorphoParser.NOT;
                break;
        }
        return tok;
}

{_DELIM} {
        yyparser.yylval = new NanoMorphoParserVal(yytext());
        return yycharat(0);
}

{_STRING} | {_FLOAT} | {_CHAR} | {_INT} | null | true | false {
        yyparser.yylval = new NanoMorphoParserVal(yytext());
        return NanoMorphoParser.LITERAL;
}

"if" {
        //yyparser.yylval = new NanoMorphoParserVal(yytext());
        return NanoMorphoParser.IF;
}

"elsif" {
        yyparser.yylval = new NanoMorphoParserVal(yytext());
        return NanoMorphoParser.ELSIF;
}

"else" {
        yyparser.yylval = new NanoMorphoParserVal(yytext());
        return NanoMorphoParser.ELSE;
}

"var" {
        yyparser.yylval = new NanoMorphoParserVal(yytext());
        return NanoMorphoParser.VAR;
}

"while" {
        yyparser.yylval = new NanoMorphoParserVal(yytext());
        return NanoMorphoParser.WHILE;
}

"return" {
        yyparser.yylval = new NanoMorphoParserVal(yytext());
        return NanoMorphoParser.RETURN;
}

{_OPNAME} {
        yyparser.yylval = new NanoMorphoParserVal(yytext());
        int token = NanoMorphoParser.YYERRCODE;
        switch(yytext().charAt(0))
        {
                case '*': case '/': case '%':
                     token = NanoMorphoParser.OP7;
                     break;
                case '+': case '-':
                     token = NanoMorphoParser.OP6;
                     break;
                case '<': case '>': case '!': case '=':
                     token = NanoMorphoParser.OP5;
                     break;
                case '&':
                     token = NanoMorphoParser.OP4;
                     break;
                case '|':
                     token = NanoMorphoParser.OP3;
                     break;
                case ':':
                     token = NanoMorphoParser.OP2;
                     break;
                case '?': case '~': case '^':
                     token = NanoMorphoParser.OP1;
                     break;
        }
        return token;
}

{_NAME} {
        yyparser.yylval = new NanoMorphoParserVal(yytext());
	return NanoMorphoParser.NAME;
}

";;;".*$ {
}

[ \t\r\n\f] {
}

. {
        yyparser.yylval = new NanoMorphoParserVal(yytext());
	return NanoMorphoParser.YYERRCODE;
}


