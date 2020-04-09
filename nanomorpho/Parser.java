// A parser for NanoMorpho based on EBNF in grammar2.txt and ifexpr.txt

import java.io.FileReader;

public class Parser
{

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

    private static NanoLexer lexer;

    public static void main(final String[] args) throws Exception
    {

        lexer = new NanoLexer(new FileReader(args[0])); //lesa inn skránna
        lexer.init(); // upphafstilla
        parse(); // keyra parse-erinn

    }

    private static String err(String tok)
    {
        int line = lexer.getLine() + 1;
        int column = lexer.getColumn() + 1;
        String pos = "\nError in line " + line + ", column " + column;
        String e = ".\nExpected " + tok + ". Next lexeme is \'" + lexer.getLexeme() + "\'.";
        return pos + e;
    }

    public static void parse()
    {
        try
        {
            function();
            while (lexer.getToken() != 0)
            {
                function();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // function	= 	NAME, '(', [ NAME, { ',', NAME } ] ')'
	// 			    '{', { decl, ';' }, { expr, ';' }, '}'
    private static void function()
    {
        try
        {
            if (lexer.getToken() != NAME) {
                throw new Error(err("name"));
            }
            lexer.advance();
            if (lexer.getToken() != 40) {
                throw new Error(err("("));
            }
            // Svigi hefur opnast.
            lexer.advance();

            if (lexer.getToken() == NAME) {
                lexer.advance();
                while (lexer.getToken() == 44) {
                    // 44 er ','
                    lexer.advance();
                    if (lexer.getToken() != NAME) {
                        throw new Error(err("name"));
                    }
                    lexer.advance();
                }
            }

            if (lexer.getToken() != 41) {
                throw new Error(err(")"));
            }
            // Svigi hefur lokast og við á taka að "{".
            lexer.advance();

            if (lexer.getToken() != 123) {
                throw new Error(err("{"));
            }
            // Hornklofi hefur opnast.
            lexer.advance();

            while (lexer.getToken() == VAR) {
                decl();
                if (lexer.getToken() != 59) {
                    throw new Error(err(";"));
                }
                lexer.advance();
            }
            // Búið er að lesa {decl, ';'}
            // Næst ætti að koma {expr, ';'} , '}'

            while (lexer.getToken() != 125) {
                expr();
                if (lexer.getToken() != 59)
                    throw new Error(err(";"));
                lexer.advance();
            }

            if (lexer.getToken() != 125)
                throw new Error(err("}"));
            lexer.advance();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // decl ='var', NAME, { ',', NAME }
    private static void decl() throws Exception
    {
        lexer.advance();
        if (lexer.getToken() != NAME) {
            throw new Error(err("name"));
        }
        lexer.advance();
        while (lexer.getToken() == 44) {
            // 44 er ','
            lexer.advance();
            if (lexer.getToken() != NAME) {
                throw new Error(err("name"));
            }
            lexer.advance();
        }
    }

    // expr = 'return', expr
    //      | NAME, '=', expr
    //      | orexpr
    private static void expr() throws Exception
    {
        int tok = lexer.getToken();
        if (tok == RETURN)
        {
            lexer.advance();
            expr();
            return;
        }
        if (tok == NAME && lexer.getNextToken() == 61)
        {
            lexer.advance();
            lexer.advance();
            expr();
            return;
        }
        orexpr();
    }

    // orexpr = andexpr, [ '||', orexpr ]
    private static void orexpr() throws Exception
    {
        andexpr();
        if (lexer.getToken() == OR){
            lexer.advance();
            orexpr();
        }
    }

    // andexpr = notexpr, [ '||', andexpr]
    private static void andexpr() throws Exception
    {
        notexpr();
        if (lexer.getToken() == AND){
            lexer.advance();
            andexpr();
        }
    }

    // notexpr = '!', notexpr | binopexpr1
    private static void notexpr() throws Exception
    {
        if (lexer.getToken() == NOT){
            lexer.advance();
            notexpr();
            return;
        }
        binopexpr1();
    }

    // binopexpr1 = binopexpr2, { OPNAME1 binopexpr2 }
    private static void binopexpr1() throws Exception
    {
        binopexpr2();
        while(lexer.getToken() == OP1){
            lexer.advance();
            binopexpr2();
        }
    }

    // binopexpr2 =	binopexpr3, [ OPNAME2, binopexpr2 ]
    private static void binopexpr2() throws Exception
    {
        binopexpr3();
        if(lexer.getToken() == OP2){
            lexer.advance();
            binopexpr2();
        }
    }

    // binopexpr3 =	binopexpr4, { OPNAME3, binopexpr4 }
    private static void binopexpr3() throws Exception
    {
        binopexpr4();
        while(lexer.getToken() == OP3){
            lexer.advance();
            binopexpr4();
        }
    }

    // binopexpr4 =	binopexpr5, { OPNAME4, binopexpr5 }
    private static void binopexpr4() throws Exception
    {
        binopexpr5();
        while(lexer.getToken() == OP4){
            lexer.advance();
            binopexpr5();
        }
    }

    // binopexpr5 =	binopexpr6, { OPNAME5, binopexpr6 }
    private static void binopexpr5() throws Exception
    {
        binopexpr6();
        while(lexer.getToken() == OP5){
            lexer.advance();
            binopexpr6();
        }
    }

    // binopexpr6 =	binopexpr7, { OPNAME6, binopexpr7 }
    private static void binopexpr6() throws Exception
    {
        binopexpr7();
        while(lexer.getToken() == OP6){
            lexer.advance();
            binopexpr7();
        }
    }

    // binopexpr7 =	smallexpr, { OPNAME7, smallexpr }
    private static void binopexpr7() throws Exception
    {
        smallexpr();
        while(lexer.getToken() == OP7){
            lexer.advance();
            smallexpr();
        }
    }

    // smallexpr = NAME
	// 		    |	NAME, '(', [ expr, { ',', expr } ], ')'
	// 		    |	opname, smallexpr
	// 		    | 	LITERAL 
	// 		    |	'(', expr, ')'
	// 		    |	ifexpr
	// 		    |	'while', '(', expr, ')', body
    private static void smallexpr() throws Exception
    {
      if (lexer.getToken() == NAME)
      {
        if (lexer.getNextToken() == 40)
        {
          lexer.advance();
          lexer.advance();
          if (lexer.getToken() != 41) {
            expr();
            while (lexer.getToken() == 44) {
              lexer.advance();
              expr();
            }
          }
          if (lexer.getToken() == 41)
          {
            lexer.advance();
          } else
          {
            throw new Error(err(")"));
          }
        } else
        {
          lexer.advance();
        }
      } else if (1100 < lexer.getToken() && lexer.getToken() < 1108)
      {
        opname();
        smallexpr();
      } else if (lexer.getToken() == LITERAL)
      {
        lexer.advance();
      } else if (lexer.getToken() == 40)
      {
        lexer.advance();
        expr();
        if (lexer.getToken() == 41)
        {
          lexer.advance();
        } else
        {
          throw new Error(err(")"));
        }
      } else if (lexer.getToken() == IF)
      {
        ifexpr();
      } else if (lexer.getToken() == WHILE)
      {
        lexer.advance();
        if (lexer.getToken() != 40)
        {
          throw new Error(err("("));
        }
        lexer.advance();
        expr();
        if (lexer.getToken() != 41)
        {
          throw new Error(err(")"));
        }
        lexer.advance();
        body();
      } else
      {
        throw new Error(err("smallexpr"));
      }
    }

    // opname		=	OPNAME1
	// 		        |	OPNAME2
	// 		        |	OPNAME3
	// 		        |	OPNAME4
	// 		        |	OPNAME5
	// 		        |	OPNAME6
	// 		        |	OPNAME7
    private static void opname() throws Exception
    {
      if (1100 < lexer.getToken() && lexer.getToken() < 1108)
      {
        lexer.advance();
      } else
      {
          err("operator");
      }
    }

    // ifexpr = 'if', '(', expr, ')', body, elsepart;
    private static void ifexpr() throws Exception
    {
        lexer.advance();
        if (lexer.getToken() != 40)
            throw new Error(err("("));
        lexer.advance();
        // Búið er að lesa yfir if', '(',

        expr();
        if (lexer.getToken() != 41)
            throw new Error(err(")"));
        lexer.advance();
        // Búið er að lesa yfir 'if', '(', expr, ')',

        body();
        elsepart();
        // Búið er að lesa yfir 'if', '(', expr, ')', body, elsepart;
    }

    // elsepart = /* nothing */
    //          | 'else', body
    //          | 'elsif', '(', expr, ')', body, elsepart
    //          ;
    private static void elsepart() throws Exception
    {
        int tok = lexer.getToken();
        if (tok == ELSE)
        {
            lexer.advance();
            body();
            return;
        }
        if (tok == ELSIF)
        {
            lexer.advance();
            if (lexer.getToken() != 40)
                throw new Error(err("("));
            lexer.advance();
            expr();
            if (lexer.getToken() != 41)
                throw new Error(err(")"));
            lexer.advance();
            body();
            elsepart();
        }
        // if tok is neither ELSE nor ELSEIF, do nothing.
    }

    // body = '{', { expr, ';' }, '}';
    private static void body() throws Exception
    {
        if (lexer.getToken() != 123)
            throw new Error(err("{"));
        lexer.advance();
        while (lexer.getToken() != 125)
        {
            expr();
            if (lexer.getToken() != 59)
                throw new Error(err(";"));
            lexer.advance();
        }
        lexer.advance();
    }

}
