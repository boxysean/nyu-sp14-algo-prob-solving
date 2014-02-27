import java.util.*;
public class MaxQuery
{
    static int[][] buildTable(int[] arr)
    {
      int n = arr.length, m = (int)(Math.log(n)/Math.log(2)+1+1e-9);
      int[][] tab = new int[n][m];
      for (int i = 0; i < n; ++i) tab[i][0] = arr[i];
      for (int j = 1, L = 2; L <= arr.length; L<<=1, ++j)
        for (int a = 0; a+L-1 < arr.length; ++a)
          tab[a][j] = Math.max(tab[a][j-1],tab[a+L/2][j-1]);
      return tab;
    }
    //Query for indices in interval [a,b]
    static int maxQuery(int[][] tab, int a, int b)
    {
      int L = b-a+1, lgL = (int)Math.floor(Math.log(L)/Math.log(2)+1e-9);
      return Math.max(tab[a][lgL],tab[b+1-(1<<lgL)][lgL]);
    }
    static int maxQuery(int[] arr, int a, int b)
    {
	int max = arr[a];
	for (int i = a+1; i <= b; ++i) max = Math.max(max,arr[i]);
	return max;
    }
    public static void main(String[] args)
    {
	Random r = new Random(1);
	for (int X = 0; X < 100; ++X)
	{
	    int[] arr = new int[100000];
	    for (int i = 0; i < arr.length; ++i) arr[i] = r.nextInt();
	    int[][] tab = buildTable(arr);
	    for (int i = 0; i < 1000; ++i)
	    {
		int L = r.nextInt(arr.length), R = r.nextInt(arr.length);
		int m = Math.min(L,R), M = Math.max(L,R);
		int a = maxQuery(arr,m,M), b = maxQuery(tab,m,M);
		if (a != b) System.out.printf("BAD: %d %d\n",a,b);
	    }
	}
    }
}