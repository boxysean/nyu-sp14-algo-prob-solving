import java.util.*;
public class Tiling
{
    static int[][] cache;
    //Bits of S in column-major order
    static int count(int k, int S)
    {
      if (k == 0) return S==0?1:0;
      if ((S & 0x3F) == 0x3F) return count(k-1,S>>6);
      if (cache[k][S] > -1) return cache[k][S];
      int lowOff = (~S)&(S+1), below = lowOff<<1, right = lowOff<<6;
      int ret = 0;
      if ((S & below)==0 && below < (1<<6)) ret = (ret + count(k,S|lowOff|below))%1000003;
      if ((S & right)==0) ret = (ret + count(k,S|lowOff|right))%1000003;
      //System.out.printf("%d,%s val=%d\n",k,Integer.toBinaryString(S),ret);
      return cache[k][S] = ret;
    }
    static int count(int k) { return count(k,0); }
    public static void main(String[] args)
    {
	int n = Integer.parseInt(args[0]);
	cache = new int[n+1][1<<12];
	for (int i = 0; i < cache.length; ++i) Arrays.fill(cache[i],-1);
	System.out.println(count(n));
    }
}