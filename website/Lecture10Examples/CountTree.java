import java.util.*;
public class CountTree
{
    static long[] cache = new long[100];
    static long count(int n)
    {
      if (n == 0) return 1;
      if (cache[n]>-1) return cache[n];
      long ret = 0;
      for (int root = 1; root <= n; ++root)
         ret += count(root-1)*count(n-root);
      return cache[n] = ret;
    }

    static long catalan(int n)
    {
	long ret = 1;
	for (int i = 1; i <= n; ++i)
	{
	    ret *= 2*n-i+1;
	    ret /= i;
	}
	return ret/(n+1);
    }
    
    public static void main(String[] args)
    {
	Arrays.fill(cache,-1);
	int n = Integer.parseInt(args[0]);
	System.out.printf("%d %d\n",count(n),catalan(n));
    }
}