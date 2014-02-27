public class Fibo
{
    public static int FibDP(int a)
    {
	int[] dp = new int[a+1];
	dp[0] = 0; dp[1] = 1;
	for (int i = 2; i < dp.length; ++i) dp[i] = (dp[i-1]+dp[i-2])%1000003;
	return dp[a];
    }
    static int Fib(int n)
    {
	int a = 0, b = 1;
	for (int i = 2; i <= n; ++i)
	{
	    int t = b;
	    b = (a + b) % 1000003;
	    a = t;
	}
	return b;
    }
    public static void main(String[] args) throws Exception
    {
	int n = Integer.parseInt(args[0]);
	System.out.printf("%d %d\n",FibDP(n),Fib(n));
    }
}