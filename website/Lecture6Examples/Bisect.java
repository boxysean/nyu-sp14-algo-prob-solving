public class Bisect
{
    static double f(double x) 
    {
	return 2*Math.pow(x,4)+Math.pow(x,3)-Math.exp(x)+Math.log(x);
    }
    public static void main(String[] args) throws Exception
    {
	double L = 1, R = 20;
	while (R-L > 1e-9)
	{
	    double M = (R+L)/2, fM = f(M);
	    System.out.printf("L=%f,R=%f,f(%f)=%f\n",L,R,M,f(M));
	    if (fM > 0) L = M;
	    else R = M;
	}
	System.out.printf("f(%.9f)=%f\n\n",L,f(L));
    }
}