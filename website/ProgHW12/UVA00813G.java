
public class UVA00813G
{
	public static final double EPS = 1e-9;
	static double[] fix(double[][] a) { return new double[]{a[0][0],a[1][0],a[2][0]}; }
	static boolean intersect(double[][] p1, double[][] q1, double[][] p2, double[][] q2)
	{
		return intersect(fix(p1),fix(q1),fix(p2),fix(q2));
	}
	static boolean intersect(double[] p1, double[] q1, double[] p2, double[] q2)
	{
		return intersecth(p1,q1,p2,q2) && intersecth(p2,q2,p1,q1); 
	}
	static boolean intersecth(double[] p1, double[] q1, double[] p2, double[] q2)
	{
		double[] da = sub(q1,p1), db = sub(q2,p2), dc = sub(p2,p1);
		double vol = Math.abs(dot(dc,cross(da,db)));
		if (vol > EPS) return false;
		double n = norm2(cross(da,db));
		double s = n < EPS ? 10 : dot(cross(dc,db),cross(da,db))/n;
		return s>EPS && s<1-EPS;
	} 
	static double[] sub(double[] v, double[] w) { return new double[]{v[0]-w[0],v[1]-w[1],v[2]-w[2]}; }
	static double norm2(double[] v) { return v[0]*v[0] + v[1]*v[1] + v[2]*v[2]; }
	static double dot(double[] v, double[] w) { return v[0]*w[0] + v[1]*w[1] + v[2]*w[2]; }
	static double[] cross(double[] v, double[] w)
	{
		double[] ret = {v[1]*w[2]-v[2]*w[1],w[0]*v[2]-w[2]*v[0],v[0]*w[1]-v[1]*w[0]};
		return ret;
	}
	static double[][] mult(double[][] a, double[][] b)
	{
		double[][] ret = new double[a.length][b[0].length];
		for (int r = 0; r < ret.length; ++r) for (int c = 0; c < ret[0].length; ++c)
			for (int i = 0; i < b.length; ++i) ret[r][c] += a[r][i]*b[i][c];
		return ret;
	}
	static double[][] add(double[][] a, double[][] b)
	{
		double[][] ret = new double[a.length][a[0].length];
		for (int r = 0; r < ret.length; ++r) for (int c = 0; c < ret[0].length; ++c) ret[r][c] = a[r][c]+b[r][c];
		return ret;
	}
}
