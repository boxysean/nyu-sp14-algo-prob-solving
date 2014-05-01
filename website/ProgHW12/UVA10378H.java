import java.util.Comparator;


public class UVA10378H
{
	static final double EPS = 1e-9;
	static class Comp implements Comparator<double[]>
	{
		@Override
		public int compare(double[] o1, double[] o2)
		{
			if (Math.abs(o1[0]-o2[0]) > EPS) return o1[0] < o2[0] ? 1 : -1;
			if (Math.abs(o1[1]-o2[1]) > EPS) return o1[1] < o2[1] ? 1 : -1;
			return 0;
		}		
	}
	
	static String[] format(double[] v)
	{
		String as = String.format("%.03f",v[0]), bs = String.format("%+.03f",v[1]);
		if (as.equals("-0.000")) as = "0.000";
		if (bs.equals("-0.000")) bs = "+0.000";
		return new String[]{as,bs};
	}
}
