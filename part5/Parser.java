/* *** This file is given as part of the programming assignment. *** */

import java.util.*;
//import java.util.LinkedList;

public class Parser {

    
    // tok is global to all these parsing methods;
    // scan just calls the scanner's scan method and saves the result in tok.
    private Token tok; // the current token
    SymbolTable st;
	
    private void scan() {
        tok = scanner.scan();
    }

    private Scan scanner;
    Parser(Scan scanner) {
        this.scanner = scanner;
	this.st = new SymbolTable();
	scan();
	if(is(TK.ERROR))
		mustbe(TK.ERROR);
	program();	

        if( tok.kind != TK.EOF )
            parse_error("junk after logical end of program");
	print_stuff();
    }
 
    private void print_stuff()
    {
      SymbolStats cur;

      for(int i = 0; i < st.stats.size(); i++)
      {
	cur = st.stats.get(i);
	System.err.println(cur.getStr());
	System.err.println("  declared on line "
				+cur.getDecl()
				+" at nesting depth "
				+cur.getLevel());

	if(cur.assign.size() == 0)
	  System.err.println("  never assigned");
  	else
	{
 	  System.err.print("  assigned to on:");

    	  //loop to print out lines and times assigned
    	
    	  for(int j = 0; j < cur.assign.size(); j++)
	  {
	    Pair slave = cur.assign.get(j);
	    System.err.print(" "+slave.line);
	    if(slave.times > 1)
	      System.err.print("("+slave.times+")");
	  }
	  System.err.println();
	}

	if(cur.use.size() == 0)
	  System.err.print("  never used");
	else
	{
 	  System.err.print("  used on:");

	  //loop to print out lines and times used 

	  for(int k = 0; k < cur.use.size(); k++)
	  {
	    Pair peasant = cur.use.get(k);
	    System.err.print(" "+peasant.line);
	    if(peasant.times > 1)
	      System.err.print("("+peasant.times+")");
	  }
	}

	System.err.println();
      }

    }


    private void program() {
	System.out.println("#include <stdio.h>");
	System.out.println("int main()");
        block();
    }

    private void block() {
	System.out.println("{");
	st.pushST(tok.lineNumber);
    	if(is(TK.VAR))
    		declarations();
    	statement_list();
	st.popST(tok.lineNumber);
	if(st.depth == -1)
	  System.out.println("return 0;");
	System.out.println("}");
    }

    private void declarations() {
        mustbe(TK.VAR);
        while( is(TK.ID) ) {
	    st.addTo(tok.string, tok.lineNumber);
	    System.out.println("int x_"+tok.string+";");
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
		st.check(tok.string, tok.lineNumber, true);
		System.out.print(tok.string);
	    	mustbe(TK.ID);
		//check if ID is in SymbolTable
		//ir it isn't, eturn error message
	}
	if(is(TK.ASSIGN))
	{
	      	mustbe(TK.ASSIGN);
		System.out.print(" = ");
	}
      	expression();
	System.out.println(";");
    }
    
    private void print()
    {
    	mustbe(TK.PRINT);
	System.out.print("printf(");
    	expression();
	System.out.println(");");
    }
    
    private void if_e()
    {
	System.out.print("if(");
    	mustbe(TK.IF);
    	guarded_commands();
    	mustbe(TK.FI);
      System.out.println(")");
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
	st.check(tok.string, tok.lineNumber, true);
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
		st.check(tok.string, tok.lineNumber, false);
		System.out.print(tok.string);
    		mustbe(TK.ID);
		//check if ID is in SymbolTable
		//if it isn't, return error message, else do nothing
    	}
    	else if( is(TK.NUM) )
    	{
		System.out.print(tok.string);
    		mustbe(TK.NUM);
    	} else
		parse_error("factor");
    }
    
    private void relop()
    {
    	if( is(TK.EQ) )
    	{
		System.out.print("=");
    		mustbe(TK.EQ);
    	}
    	else if( is(TK.LT) )
    	{
		System.out.print("<");
    		mustbe(TK.LT);
    	}
    	else if( is(TK.GT) )
    	{
		System.out.print(">");
    		mustbe(TK.GT);
    	}
    	else if( is(TK.LE) )
    	{
		System.out.print("<=");
    		mustbe(TK.LE);
    	}
    	else if( is(TK.GE) )
    	{
		System.out.print(">=");
    		mustbe(TK.GE);
    	}
    	else if( is(TK.NE) )
    	{
		System.out.print("!=");
    		mustbe(TK.NE);
    	}
    }
    
    private void addop()
    {
    	if( is(TK.PLUS) )
    	{
		System.out.print("+");
    		mustbe(TK.PLUS);
    	}
    	else if( is(TK.MINUS) )
    	{
		System.out.print("-");
    		mustbe(TK.MINUS);
    	}
    }
    
    private void multop()
    {
    	if( is(TK.TIMES) )
    	{
		System.out.print("*");
    		mustbe(TK.TIMES);
    	}
    	else if( is(TK.DIVIDE) )
    	{
		System.out.print("/");
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
