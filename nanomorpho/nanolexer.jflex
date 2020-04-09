/**
	JFlex scanner example based on a scanner for NanoLisp.
	Author: Snorri Agnarsson, 2017-2020

	This stand-alone scanner/lexical analyzer can be built and run using:
		java -jar JFlex-full-1.7.0.jar nanolexer.jflex
		javac NanoLexer.java
		java NanoLexer inputfile > outputfile
	Also, the program 'make' can be used with the proper 'makefile':
		make test
 */

import java.io.*;

%%

%public
%class NanoLexer
%unicode
%byaccj
%line
%column

%{

// This part becomes a verbatim part of the program text inside
// the class, NanoLexer.java, that is generated.

// Definitions of tokens:
final static int ERROR = -1;
final static int IF = 1001;
final static int DEFINE = 1002;
final static int NAME = 1003;
final static int LITERAL = 1004;
final static int OPNAME = 1005;
final static int ELSIF = 1006;
final static int ELSE = 1007;
final static int VAR = 1008;
final static int WHILE = 1009;
final static int RETURN = 1010;

final static int AND = 1011;
final static int OR = 1012;
final static int NOT = 1013;

final static int OP1 = 1101; 
final static int OP2 = 1102;
final static int OP3 = 1103;
final static int OP4 = 1104;
final static int OP5 = 1105;
final static int OP6 = 1106;
final static int OP7 = 1107;

// A variable that will contain lexemes as they are recognized:
private static String lexeme;
private static int t1, t2 = 0;              // t1 = current token, t2 = next token
private static String l1, l2 = "";
private static int line1, line2 = 0;
private static int column1, column2 =0;


// This runs the scanner:
public static void main(String[] args) throws Exception
{
        NanoLexer lexer = new NanoLexer(new FileReader(args[0]));
        lexer.init();
}


public void init() throws Exception
{
        
        t1 = yylex();
        l1 = l2;
        t2 = yylex();
        

}     
  

public void advance() throws Exception
{
<<<<<<< HEAD
=======

>>>>>>> master
        // System.out.println("advancing from token: " + t1 + " (" + l1 + ") to " + t2 + " (" + l2 + ")");
        t1 = t2;
        l1 = l2; //
        t2 = yylex();
        line1 = line2;
        line2 = yyline;
        column1 = column2;
        column2 = yycolumn;
        if (t2 == 0) {
                l1 = l2;
                l2 = yytext();    
        }
       

}

public int getToken()
{
        return t1;
}

public int getNextToken()
{
        return t2;
}

public String getLexeme()
{
        return l1;
}

public String getNextLexeme()
{
        return l2;
}

public int getLine()
{
        return line1;
}

public int getColumn()
{
        return column1;
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
_BOOLEAN=['!'|'&&'|'||']
_OPNAME=([\+\-*/!%=><\:\^\~&|?])+

%%

  /* Lesgreiningarreglur */
  /* Scanning rules */

{_BOOLEAN} {
        l2 = yytext();
        int tok = ERROR;
        switch(l2)
        {
            case "&&":
                tok = AND;
                break;
            case "||":
                tok = OR;
                break;
            case "!":
                tok = NOT;
                break;
        }
        return tok;
}

{_DELIM} {
        l2 = yytext();
	return yycharat(0);
}

{_STRING} | {_FLOAT} | {_CHAR} | {_INT} | null | true | false {
        l2 = yytext();
	return LITERAL;
}

"if" {
        l2 = yytext();
	return IF;
}

"elsif" {
        l2 = yytext();
        return ELSIF;
}

"else" {
        l2 = yytext();
        return ELSE;
}

"var" {
        l2 = yytext();
	return VAR;
}

"while" {
        l2 = yytext();
        return WHILE;
}

"return" {
        l2 = yytext();
        return RETURN;
}

{_OPNAME} {
        l2 = yytext();
        int token = -1;
        switch(l2.charAt(0))
{
                case '*': case '/': case '%':
                    token = 1107;
                    break;
                case '+': case '-':
                     token = 1106;
                     break;
                case '<': case '>': case '!': case '=':
                     token = 1105;
                     break;
                case '&':
                     token = 1104;
                     break;
                case '|':
                     token = 1103;
                     break;
                case ':':
                     token = 1102;
                     break;
                case '?': case '~': case '^':
                     token = 1101;
                     break;
        }
        return token;
}

{_NAME} {
        l2 = yytext();
	return NAME;
}

";;;".*$ {
}

[ \t\r\n\f] {
}

. {
        l2 = yytext();
	return ERROR;
}


