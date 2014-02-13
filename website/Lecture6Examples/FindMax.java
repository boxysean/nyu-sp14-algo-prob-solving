public class FindMax
{
    //static double f(double x) { return -2*(x-7)*(x-7) + 9; }
    //static double f(double x) { return Math.sin(x*Math.PI/100); }
    static double f(double x) { return x < 10 ? Math.exp(x) : -x; }
    static double findMax(double L, double R)
    {
	if (R-L < 1e-9) return L;
	double F = L + (R-L)/3, S = R - (R-L)/3;
	double fF = f(F), fS = f(S);
	if (fF < fS) return findMax(F,R);
	else return findMax(L,S);
    }
    public static void main(String[] args)
    {
	System.out.println(findMax(0,100));
    }
}