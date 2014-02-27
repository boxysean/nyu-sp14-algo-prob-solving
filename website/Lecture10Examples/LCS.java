import java.util.*;
public class LCS
{
    static int[][] cache;
    static int lcs(int[] arr1, int[] arr2, int p1, int p2)
    {
      if (p1 == arr1.length || p2 == arr2.length) return 0;
      if (cache[p1][p2] > -1) return cache[p1][p2];
      int ret = Math.max(lcs(arr1,arr2,p1+1,p2),lcs(arr1,arr2,p1,p2+1));
      if (arr1[p1]==arr2[p2]) ret = 1+lcs(arr1,arr2,p1+1,p2+1);
      return cache[p1][p2] = ret;
    }
    static int lcs(int[] arr1, int[] arr2) { return lcs(arr1,arr2,0,0); }
    static void printlcs(int[] arr1, int[] arr2, int p1, int p2)
    {
	if (p1 == arr1.length || p2 == arr2.length) return;
	int val = lcs(arr1,arr2,p1,p2);
	if (val == lcs(arr1,arr2,p1+1,p2)) printlcs(arr1,arr2,p1+1,p2);
	else if (val == lcs(arr1,arr2,p1,p2+1)) printlcs(arr1,arr2,p1,p2+1);
	else
	{ 
	    System.out.print(arr1[p1]+" ");
	    printlcs(arr1,arr2,p1+1,p2+1);
	}    
    }
    static void printlcs(int[] arr1, int[] arr2) { printlcs(arr1,arr2,0,0); }
    public static void main(String[] args)
    {
	String s = args[0], t = args[1];
	int[] sa = new int[s.length()], ta = new int[t.length()];
	for (int i = 0; i < s.length(); ++i) sa[i] = s.charAt(i)-'0';
	for (int i = 0; i < t.length(); ++i) ta[i] = t.charAt(i)-'0';
	cache = new int[sa.length][ta.length];
	for (int i = 0; i < cache.length; ++i) Arrays.fill(cache[i],-1);
	System.out.println(lcs(sa,ta));
	printlcs(sa,ta);
	System.out.println();
    }
}
