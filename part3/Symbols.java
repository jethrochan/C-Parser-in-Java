public class Symbols {

  String str;
  boolean redeclared; //true if redeclare & error was printed

  public Symbols(String str1, boolean re){
    str = str1;
    redeclared = re;
  }
  public String getStr()
  {
    return this.str;
  }

  public boolean getRedeclared()
  { 
    return this.redeclared;
  }
}
