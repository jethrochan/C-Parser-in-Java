import java.util.*;

public class SymbolTable {

  Stack<ArrayList<Symbols>> ids;
  int depth;
  
  Stack<SymbolTable> pls_dont_disappear;

  public SymbolTable()
  {
    ids = new Stack<ArrayList<Symbols>>();
    depth = -1; //nothing in stack, depth = -1
    pls_dont_disappear = new Stack<SymbolTable>();
  } 

  public void addTo(String str, int lineNumber)
  {
    ArrayList<Symbols> cur_stk = ids.peek();
    boolean found = false;

    for(int i = 0; i < cur_stk.size(); i++)
      if(cur_stk.get(i).getStr().equals(str))
      {
	System.err.println("variable "+str+
				" is redeclared on line "+lineNumber);
	found = true;
	break;
      }
      
    if(!found)
    {
      Symbols cur_id = new Symbols(str, depth, lineNumber);
      ids.peek().add(cur_id);
    }

  } 

  public void pushST()
  {
    ArrayList<Symbols> newStack = new ArrayList<Symbols>();
    ids.push(newStack);
    depth++;
  }

  public void popST()
  {
    if(!(ids.empty()))
    {
      SymbolTable temp = new SymbolTable();
      temp.ids.push(this.ids.pop());
      temp.depth = this.depth;
      pls_dont_disappear.push(temp);
      depth--; 	//if pop, depth--
    }
  }


  public void check(String str, int lineNumber, boolean use, boolean assign)
  {
    boolean check_str = false;  //check if string is in table
				//init to false (not in table)
    ArrayList<Symbols> cur_stk = new ArrayList<Symbols>();
    Stack<ArrayList<Symbols>> temp_stk = new Stack<ArrayList<Symbols>>();
    Symbols cur_sym;
    int i;

    while(!ids.isEmpty())
    {   
      cur_stk = ids.pop(); 	//set cur_stk = top most stack
//      temp_stk.push(ids.pop());	//pop top ids stack, push to temp stack
      
      for(i = 0; i < cur_stk.size(); i++)
      {
        if(cur_stk.get(i).getStr().equals(str))
        {
          check_str = true;
	  if(assign)
	    updateAssign(i, cur_stk, lineNumber);
	  else
	    updateUse(i, cur_stk, lineNumber);
	      
          break;
        }
      }
      temp_stk.push(cur_stk);
      
    }  
    if(!check_str) //string is not in table, output error and exit
    {
      System.err.println("undeclared variable "+str+" on line "+lineNumber);
      System.exit(1);
    }   

    while(!temp_stk.isEmpty())
      this.ids.push(temp_stk.pop());

  }

  public void updateAssign(int index, ArrayList<Symbols> cur_stk, int line)
  {
    Symbols cur_sym;
    int [] temp_arr = new int [2];
    boolean found = false;
    int i;

    cur_sym = cur_stk.get(index);

    for(i = 0; i < cur_sym.assigned.size(); i++)
    {
      temp_arr = cur_sym.assigned.get(i);
      if(temp_arr[0] == line) 	//check to see if line number exists in arr
      {
	found = true;
	break;
      }
    }  

    if(found)			//if it is, update arr[1]
    {
      temp_arr[1]++;
      cur_sym.assigned.set(i, temp_arr);
    }      
    else  			//not found, so add a new entry to list
    {
      temp_arr[0] = line;
      temp_arr[1] = 1;
      cur_sym.assigned.add(temp_arr);
    }

    cur_stk.set(index, cur_sym);
  }

  public void updateUse(int index, ArrayList<Symbols> cur_stk, int line)
  {
    Symbols cur_sym;
    int [] temp_arr = new int [2];
    boolean found = false;
    int i;

    cur_sym = cur_stk.get(index);

    for(i = 0; i < cur_sym.used.size(); i++)
    {
      temp_arr = cur_sym.used.get(i);
      if(temp_arr[0] == line)
      {
        found = true;
        break;
      }
    }

    if(found)
    {
      temp_arr[1]++;
      cur_sym.used.set(i, temp_arr);
    }
    else
    {
      temp_arr[0] = line;
      temp_arr[1] = 1;
      cur_sym.used.add(temp_arr);
    }

    cur_stk.set(index, cur_sym);
  }

}
