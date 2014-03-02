import java.util.*;

public class SymbolStats {

  private String str;
  protected final int level;
  ArrayList<Pair> assign;
  ArrayList<Pair> use;
  private int line_decl;
  int block_start;
  int block_end;

  SymbolStats(String str, int depth, int line)
  {
    this.str = str;
    this.level = depth;
    this.line_decl = line;

    this.assign = new ArrayList<Pair>();
    this.use = new ArrayList<Pair>();
  }

  public String getStr()
  {
    return this.str;
  }

  public int getLevel()
  {
    return this.level;
  }

  public int getDecl()
  {
    return this.line_decl;
  }
}
