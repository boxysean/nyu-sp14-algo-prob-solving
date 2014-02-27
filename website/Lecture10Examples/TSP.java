import java.util.*;
import static java.lang.Math.*;
public class TSP
{
    static double[][] cache;
    static double dist(double[] p1, double[] p2) 
    { 
      return sqrt((p1[0]-p2[0])*(p1[0]-p2[0])+(p1[1]-p2[1])*(p1[1]-p2[1]));
    }
    //Assuming we have a path from 0 to last using vertices in S, compute
    //the cost of completing the path
    static double solve(double[][] ps, int S, int last)
    {
      if (S == (1<<ps.length)-1) return 0;
      if (cache[S][last] > -1) return cache[S][last];
      double min = Double.POSITIVE_INFINITY;
      for (int next = 0; next < ps.length; ++next)
      {
        int b = 1<<next;
        if ( (b & S) != 0 ) continue;
        min = Math.min(min, solve(ps,S^b,next)+dist(ps[last],ps[next]));
      }
      return cache[S][last] = min;
    }
    static double solve(double[][] ps) { return solve(ps,1,0); }

    public static void main(String[] args)
    {
	int n = args.length/2;
	cache = new double[1<<n][n];
	for (int i = 0; i < cache.length; ++i) Arrays.fill(cache[i],-2);
	double[][] ps = new double[n][2];
	for (int i = 0; i < n; ++i)
	{
	    ps[i][0] = Double.parseDouble(args[2*i]);
	    ps[i][1] = Double.parseDouble(args[2*i+1]);
	}
	System.out.println(solve(ps));
    }
}