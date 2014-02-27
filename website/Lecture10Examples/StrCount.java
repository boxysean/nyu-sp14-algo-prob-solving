import java.util.*;
public class StrCount
{
    static long[][] cache = new long[3][40];
    static long count(int n, int bba)
    {
      if (n==0) return 1;
      if (cache[bba][n] > -1) return cache[bba][n];
      long ret = count(n-1,0); //letter c
      ret += count(n-1,Math.min(bba+1,2)); //letter b
      if (bba < 2) ret += count(n-1,0); //letter a
      return cache[bba][n] = ret;
    }

    public static void main(String[] args)
    {
	for (int i = 0; i < cache.length; ++i) Arrays.fill(cache[i],-1);
	System.out.println(count(Integer.parseInt(args[0]),0));
    }
}