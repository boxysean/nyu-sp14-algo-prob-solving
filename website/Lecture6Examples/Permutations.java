import java.util.*;
public class Permutations
{
    static void swap(int[] L, int a, int b)
    {
      int tmp = L[a]; L[a] = L[b]; L[b] = tmp;
    }
    static void f(int[] L)
    {
	for (int i : L) System.out.print(i+" ");
	System.out.println();
    }
    
    //Reverse entries in inclusive range [a,b]
    static void reverse(int[] L, int a, int b)
    {
      while (a < b)
      {
        swap(L,a,b);
        ++a;
        --b;
      }
    }
    //Should be initially called with a sorted list
    //Returns false when the list has returned to being sorted
    static boolean nextPerm(int[] L)
    {
      if (L.length == 1) return false;
      int R = L.length-2;
      while (R>=0)
      {
        if (L[R] >= L[R+1]) --R;
        else
        {
          int i = L.length-1;
          while (L[R] >= L[i]) --i;
          swap(L,R,i);
          reverse(L,R+1,L.length-1);
          return true;
        }
      }
      reverse(L,0,L.length-1);
      return false;
    }
    static void rec(int[] L, int pos)
    {
	if (pos == L.length) f(L);
	else
	{
	    for (int i = pos; i < L.length; ++i)
	    {
		swap(L,pos,i);
		rec(L,pos+1);
		swap(L,pos,i);
	    }
	}	
    }
    static void recMask(int[] L, int[] buf, int pos, BitSet b)
    {
	if (pos == L.length) f(buf);
	else 
	{
	    for (int i = 0; i < L.length; ++i)
	    {
		if (b.get(i)) continue;
		b.set(i);
		buf[pos] = L[i];
		recMask(L,buf,pos+1,b);
		b.clear(i);
	    }   
	}	
    }
    //Assumes L is sorted
    static void recLex(int[] L, int pos)
    {
	if (pos == L.length) f(L);
	else
	{
	    recLex(L,pos+1);
	    for (int i = pos+1; i < L.length; ++i)
	    {
		if (L[i] == L[pos]) continue;
		swap(L,pos,i);
		recLex(L,pos+1);
	    }
	    int tmp = L[pos];
	    for (int i = pos+1; i < L.length; ++i) L[i-1] = L[i];
	    L[L.length-1] = tmp;
	}	
    }
    static void iterate(int[] L)
    {
      Arrays.sort(L);
      do 
      {
        f(L); 
      } while (nextPerm(L));
    }
    //Call initially with parameters (L,new int[4],0,0)
    static void rec4(int[] L, int[] buf, int pos, int cnt)
    {
	if (cnt == 4) f(buf);
	else if (pos < L.length)
	{
	    buf[cnt] = L[pos];
	    rec4(L,buf,pos+1,cnt+1);
	    rec4(L,buf,pos+1,cnt);
	}
    }
    static void iter4(int[] L)
    {
	int[] buf = new int[4];
	for (int a = 0; a < L.length; ++a)
	{
	    buf[0] = L[a];
	    for (int b = a+1; b < L.length; ++b)
	    {
		buf[1] = L[b];
		for (int c = b+1; c < L.length; ++c)
		{
		    buf[2] = L[c];
		    for (int d = c+1; d < L.length; ++d)
		    {
			buf[3] = L[d];
			f(buf);
		    }
		}
	    }
	}		      
    }
    public static void main(String[] args)
    {
	int[] L = new int[args.length];
	for (int i = 0; i < L.length; ++i) L[i] = Integer.parseInt(args[i]);
	iterate(L);
	System.out.println("----------------");
	rec(L,0);
	System.out.println("----------------");
	recLex(L,0);
	System.out.println("----------------");
	recMask(L,new int[L.length],0, new BitSet(L.length));
	System.out.println("----------------");
	rec4(L, new int[4], 0, 0);
	System.out.println("----------------");
	iter4(L);
    }
}