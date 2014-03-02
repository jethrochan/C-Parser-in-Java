import java.util.*;

public class SymbolTable {

  Stack<ArrayList<Token>> ids = new Stack<ArrayList<Token>>();
  
  public SymbolTable(){} 

  public void addTo(String str, int lineNumber)
  {
    ArrayList<Token> cur_stk = ids.peek();
    boolean found = false;

    for(int i = 0; i < cur_stk.size(); i++)
      if(cur_stk.get(i).string.equals(str))
      {
	System.err.println("variable "+str+
				" is redeclared on line "+lineNumber);
	found = true;
	break;
      }
      
    if(!found)
    {
      Token cur_id = new Token(TK.VAR, str, lineNumber);
      ids.peek().add(cur_id);
    }

  } 

  public void pushST()
  {
    ArrayList<Token> newStack = new ArrayList<Token>();
    ids.push(newStack);
  }

  public void popST()
  {
    ids.pop();
  }


  public void check(String str, int lineNumber)
  {
    boolean check_str = false;  //check if string is in table
				//init to false (not in table)
    ArrayList<Token> cur_stk;
    Stack<ArrayList<Token>> temp_stk = new Stack<ArrayList<Token>>();


    while(!ids.isEmpty())
    {   
      cur_stk = ids.pop(); 	//set cur_stk = top most stack
      temp_stk.push(cur_stk);	//then push it onto the temp_stk

      for(int i = 0; i < cur_stk.size(); i++)
        if(cur_stk.get(i).string.equals(str))
        {
          check_str = true;
          break;
        }
    }  
    if(!check_str) //string is not in table, output error and exit
    {
      System.err.println("undeclared variable "+str+" on line "+lineNumber);
      System.exit(1);
    }   
    

    while(!temp_stk.isEmpty())
      ids.push(temp_stk.pop()); 	//reset these fuckers


  }


}
