import java.util.*;
public class Cost
{
    static int[][] cache;
    static int cost(String x, String y, int xpos, int ypos)
    {
      if (xpos == x.length() || ypos==y.length()) return Math.abs(x.length()-y.length());
      if (cache[xpos][ypos] > -1) return cache[xpos][ypos];
      int cost = cost(x,y,xpos+1,ypos) + 1;
      cost = Math.min(cost, cost(x,y,xpos,ypos+1) + 1);
      int match = x.charAt(xpos)==y.charAt(ypos) ? 0 : 1; 
      cost = Math.min(cost,cost(x,y,xpos+1,ypos+1)+match);
      return cache[xpos][ypos] = cost;
    }
    static int cost(String x, String y) { return cost(x,y,0,0); }
    public static void main(String[] args) 
    {
	String x = args[0], y = args[1];
	cache = new int[x.length()][y.length()];
	for (int i = 0; i < cache.length; ++i) Arrays.fill(cache[i],-1);
	System.out.println(cost(x,y));
    }
}