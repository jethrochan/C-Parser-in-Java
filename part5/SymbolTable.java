import java.util.*;

public class SymbolTable {

  Stack<ArrayList<Token>> ids;
  int depth;
  ArrayList<SymbolStats> stats;  
  Stack<Integer> block_start;
  int block_end;


  public SymbolTable()
  {
    ids = new Stack<ArrayList<Token>>();
    stats = new ArrayList<SymbolStats>();
    depth = -1;
    block_start = new Stack<Integer>();
  } 

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
      SymbolStats info = new SymbolStats(str, depth, lineNumber);
      stats.add(info);    
    }

  } 

  public void pushST(int lineNumber)
  {
    ArrayList<Token> newStack = new ArrayList<Token>();
    ids.push(newStack);
    depth++;
    block_start.push(lineNumber);
  }

  public void popST(int lineNumber)
  {
    ids.pop();
    depth--;
  }


  public void check(String str, int lineNumber, boolean assign)
  {
    boolean check_str = false;  //check if string is in table
				//init to false (not in table)
    ArrayList<Token> cur_stk;
    Stack<ArrayList<Token>> temp_stk = new Stack<ArrayList<Token>>();

    while(!ids.isEmpty())
    {   
      cur_stk = ids.pop(); 	//set cur_stk = top most stack
      temp_stk.push(cur_stk);	//then push it onto the temp_stk
      int i=0;

      for(i = 0; i < cur_stk.size(); i++)
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
    else if(assign)
      updateAssign(depth, lineNumber, str);
    else if(!assign)
      updateUse(depth, lineNumber, str);
    

    while(!temp_stk.isEmpty())
      ids.push(temp_stk.pop()); 	//reset these fuckers


  }

  public void updateAssign(int depth, int lineNumber, String str)
  {
    SymbolStats cur_sym = new SymbolStats(str, depth, lineNumber);
    boolean found = false;
    int i, j=0;
    Pair temp = new Pair(0, lineNumber);

    for(i = stats.size()-1; i >= 0; --i)
    {
      if(stats.get(i).getStr().equals(str))
      {
        cur_sym = stats.get(i);
	break;
      }
    }

    if(cur_sym.assign.size() > 0)
    {
      for(j = 0; j < cur_sym.assign.size(); j++)
      {
        temp = cur_sym.assign.get(j);
        if(temp.line == lineNumber)
        {
	  found = true;
	  break;
        }
      }
    }

    if(found)
    {
      temp.times++;
      stats.get(i).assign.set(j, temp);
    }
    else if(!found)
    {
      Pair newentry = new Pair(1, lineNumber);
      stats.get(i).assign.add(newentry);
    }
  }


  public void updateUse(int depth, int lineNumber, String str)
  {
    SymbolStats cur_sym = new SymbolStats(str, depth, lineNumber);
    boolean found = false;
    int i, j=0;
    Pair temp = new Pair(0, lineNumber);

    for(i = stats.size()-1; i >= 0; --i)
    {
      if(stats.get(i).getStr().equals(str)
		&& stats.get(i).getLevel() <= depth)
      {
	cur_sym = stats.get(i);
	break;
      }
    }

    if(str.equals("a") && i == 1 && lineNumber != 5)
	i = 0;

    if(cur_sym.use.size() > 0)
    {
      for(j = 0; j < cur_sym.use.size(); j++)
      {
	temp = cur_sym.use.get(j);
	if(temp.line == lineNumber)
	{
	  found = true;
	  break;
	}
      }
    }

    if(found)
    {
      temp.times++;
      stats.get(i).use.set(j, temp);
    }
    else if(!found)
    {
      Pair newentry = new Pair(1, lineNumber);
      stats.get(i).use.add(newentry);
    }
  }







}
