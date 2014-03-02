/* *** This file is given as part of the programming assignment. *** */

import java.util.*;
//import java.util.LinkedList;

public class Parser {

    
    // tok is global to all these parsing methods;
    // scan just calls the scanner's scan method and saves the result in tok.
    private Token tok; // the current token
    SymbolTable st = new SymbolTable();
	
    private void scan() {
        tok = scanner.scan();
    }

    private Scan scanner;
    Parser(Scan scanner) {
        this.scanner = scanner;
	scan();
	if(is(TK.ERROR))
		mustbe(TK.ERROR);
	program();	
        if( tok.kind != TK.EOF )
            parse_error("junk after logical end of program");
    }

    private void program() {
        block();
    }

    private void block() {
	st.pushST();
    	if(is(TK.VAR))
    	{
    		declarations();
		statement_list();
    	}else
 	    	statement_list();
	st.popST();
    }

    private void declarations() {
        mustbe(TK.VAR);
        while( is(TK.ID) ) {
	    st.addTo(tok.string, tok.lineNumber);
            mustbe(TK.ID);
		//check will see if string is in arrayList
		//if it isn't, we add
		//if it is, prints error
        }
        mustbe(TK.RAV);
    }

    private void statement_list()
    {
    	while(tok.kind != TK.EOF)
	{
		if(is(TK.ID) || is(TK.PRINT) || is(TK.IF) || is(TK.DO) || 
			is(TK.FA))
			statement();
		else
			break;
	}
    }
    
    private void statement()
    {
      if( is(TK.ID) )
      {
        assignment();
	//check with SymbolTable in assignment(), no need to check it here
      }
      else if( is(TK.PRINT) )
      {
        print();
      }
      else if( is(TK.IF) )
      {
        if_e();
      }
      else if( is(TK.DO) )
      {
        do_e();
      }
      else if( is(TK.FA) )
      {
        fa();
      }
    }
    
    private void assignment()
    {
 	if(is(TK.ID))
	{
		st.check(tok.string, tok.lineNumber);
	    	mustbe(TK.ID);
		//check if ID is in SymbolTable
		//ir it isn't, eturn error message
	}
	if(is(TK.ASSIGN))
	{
	      	mustbe(TK.ASSIGN);
	}
      	expression();
    }
    
    private void print()
    {
    	mustbe(TK.PRINT);

    	expression();
    }
    
    private void if_e()
    {
    	mustbe(TK.IF);
    	guarded_commands();
    	mustbe(TK.FI);
    }
    
    private void do_e()
    {
    	mustbe(TK.DO);
    	guarded_commands();
    	mustbe(TK.OD);
    }
    
    private void fa()
    {
    	mustbe(TK.FA);
	st.check(tok.string, tok.lineNumber);
    	mustbe(TK.ID);
		//check if variable is in SymbolTable
		//if it isn't, return error message
    	mustbe(TK.ASSIGN);
    	expression();
	mustbe(TK.TO);
    	expression();
    	if( is(TK.ST) )
    	{
    		mustbe(TK.ST);
    		expression();
    	}
    	commands();
    	mustbe(TK.AF);
    }
    
    private void guarded_commands()
    {
    	guarded_command();
      while( is(TK.BOX) ) {
        mustbe(TK.BOX);
        guarded_command();
      }
      if( is(TK.ELSE) )
      {
        mustbe(TK.ELSE);
        commands();
      }
    }
    
    private void guarded_command()
    {
    	expression();
    	commands();
    }
    
    private void commands()
    {
    	mustbe(TK.ARROW);
    	block();
    }
    
    private void expression()
    {
    	simple();
    	if( is(TK.EQ) || is(TK.NE) || is(TK.LT) || is(TK.GT) || is(TK.LE) || is(TK.GE)
        || is(TK.PLUS) || is(TK.MINUS) )
    	{
    		relop();
    		simple();
    	}
    }
    
    private void simple()
    {
    	term();
    	while( is(TK.PLUS) || is(TK.MINUS) ) 
    	{
    		addop();
    		term();
    	}
    }
    
    private void term()
    {
    	factor();
    	while( is(TK.TIMES) || is(TK.DIVIDE) ) 
    	{
    		multop();
    		factor();
    	}
    }
    
    private void factor()
    {
    	if( is(TK.LPAREN) || is(TK.RPAREN) )
    	{
    		mustbe(TK.LPAREN);
    		expression();
    		mustbe(TK.RPAREN);
    	}
    	else if( is(TK.ID) )
    	{
		st.check(tok.string, tok.lineNumber);
    		mustbe(TK.ID);
		//check if ID is in SymbolTable
		//if it isn't, return error message, else do nothing
    	}
    	else if( is(TK.NUM) )
    	{
    		mustbe(TK.NUM);
    	} else
		parse_error("factor");
    }
    
    private void relop()
    {
    	if( is(TK.EQ) )
    	{
    		mustbe(TK.EQ);
    	}
    	else if( is(TK.LT) )
    	{
    		mustbe(TK.LT);
    	}
    	else if( is(TK.GT) )
    	{
    		mustbe(TK.GT);
    	}
    	else if( is(TK.LE) )
    	{
    		mustbe(TK.LE);
    	}
    	else if( is(TK.GE) )
    	{
    		mustbe(TK.GE);
    	}
    	else if( is(TK.NE) )
    	{
    		mustbe(TK.NE);
    	}
    }
    
    private void addop()
    {
    	if( is(TK.PLUS) )
    	{
    		mustbe(TK.PLUS);
    	}
    	else if( is(TK.MINUS) )
    	{
    		mustbe(TK.MINUS);
    	}
    }
    
    private void multop()
    {
    	if( is(TK.TIMES) )
    	{
    		mustbe(TK.TIMES);
    	}
    	else if( is(TK.DIVIDE) )
    	{
    		mustbe(TK.DIVIDE);
    	}
    }
    // you'll need to add a bunch of methods here

    // is current token what we want?
    private boolean is(TK tk) {
        return tk == tok.kind;
    }

    // ensure current token is tk and skip over it.
    private void mustbe(TK tk) {
        if( ! is(tk) ) {
            System.err.println( "mustbe: want " + tk + ", got " +
                                    tok);
            parse_error( "missing token (mustbe)" );
        }
        scan();
	if( tk == TK.ERROR )
		scan();
    }

    private void parse_error(String msg) {
        System.err.println( "can't parse: line "
                            + tok.lineNumber + " " + msg );
        System.exit(1);
    }
}
