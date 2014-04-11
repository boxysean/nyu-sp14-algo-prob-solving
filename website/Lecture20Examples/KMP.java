
public class KMP
{
	public static int[] buildKMPTable(String pattern)
	{
		int[] table = new int[pattern.length()+1];
		for (int i = 2; i < table.length; ++i)
		{
			int j = table[i-1];
			do
			{
				if (pattern.charAt(j) == pattern.charAt(i-1)) { table[i] = j+1; break;}
				else j = table[j];
			} while (j != 0);
		}
		return table;
	}
	public static int simulate(int[] table, String pattern, String text)
	{
		int state = 0;
		for (int i = 0; i < pattern.length(); ++i)
		{
			while (true)
			{
				if (pattern.charAt(i) == text.charAt(state)) { state++; break; }
				else if (state == 0) break;
				state = table[state];
			} 
		}
		return state;
	}
}
