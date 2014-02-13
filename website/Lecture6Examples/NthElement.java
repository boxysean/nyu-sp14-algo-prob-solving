import java.util.*;
public class NthElement
{
    static Random ran = new Random();
    static void swap(int[] L, int a, int b)
    {
      int tmp = L[a]; L[a] = L[b]; L[b] = tmp;
    }
    static boolean testPartition(int[] arr, int L, int R, int p)
    {
	for (int i = L; i <= R; ++i)
	{
	    if (i < p && arr[i] > arr[p]) return false;
	    if (i > p && arr[i] < arr[p]) return false;
	}
	return true;
    }
    static int ranPartition(int[] arr, int L, int R)
    {
	int len = R - L + 1, p = ran.nextInt(len)+L;
	int piv = arr[p];
	swap(arr,R,p);
	int l = L, r = R-1;
	while (true)
	{
	    while (l <= R && arr[l] < piv) l++;
	    while (L <= r && arr[r] > piv) r--;	    
	    if (l >= r) 
	    {
		if (l >= R) return R;
		else swap(arr,l,R);
		return l;
	    }
	    swap(arr,l,r);
	    l++; r--;
	}
    }
    //Permutes arr so that arr[i] <= arr[n] for i in [L,n) and
    //arr[n] <= arr[i] for i in (n,R]
    static void nthElement(int[] arr, int L, int R, int n)
    {
	int p = ranPartition(arr,L,R);
	if (n == p) return;
	else if (n < p) nthElement(arr,L,p-1,n);
	else nthElement(arr,p+1,R,n);
    }
    public static void main(String[] args)
    {
	int N = Integer.parseInt(args[0]);
	int n = Integer.parseInt(args[1]);
	ArrayList<Integer> al = new ArrayList<Integer>();
	for (int i = 0; i < N; ++i) al.add(i%3);
	long time = System.nanoTime();
	//Collections.shuffle(al);
	System.out.printf("%.06f\n",(System.nanoTime()-time)*1e-9);
	int[] arr = new int[al.size()];
	for (int i = 0; i < arr.length; ++i) arr[i] = al.get(i);
	time = System.nanoTime();
	nthElement(arr,0,arr.length-1,n);
	System.out.printf("%.06f\n",(System.nanoTime()-time)*1e-9);
	System.out.println(arr[n]);
    }
}