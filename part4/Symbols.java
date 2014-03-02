import java.util.*;

public class Symbols {

  String str;
  int depth_decl = -1; //init to -1 for no reason
  int line_decl = -1;  //init to -1 for no fucking reason

  int[] assign_arr;
  int[] use_arr;

  ArrayList<int[]> assigned;		//ArrayList-ception
  ArrayList<int[]> used;		//just kidding, put the shits
					//above into single arrow
					//to keep times used/assigned
					//with lines used/assigned

  public Symbols(String str1, int d, int l){
    str = str1;
    depth_decl = d;
    line_decl = l;
    assign_arr = new int [2]; //0 = line asssigned, 1 = times assigned
    use_arr = new int [2];   //0 = line used, 1 = times used
    assigned = new ArrayList<int[]>();
    used = new ArrayList<int[]>();

assign_arr[0] = 1;
assign_arr[1] = 1;
assigned.add(assign_arr);
  }
  public String getStr()
  {
    return this.str;
  }

  
}
