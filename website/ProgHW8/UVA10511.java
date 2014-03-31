import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Queue;
import java.util.StringTokenizer;


public class UVA10511
{
	static HashMap<String,Integer> nameMap = new HashMap<String,Integer>();
	static HashMap<String,Integer> partyMap = new HashMap<String,Integer>();
	static HashMap<String,Integer> clubMap = new HashMap<String,Integer>();
	static ArrayList<String> revNameMap = new ArrayList<String>();
	static ArrayList<String> revPartyMap = new ArrayList<String>();
	static ArrayList<String> revClubMap = new ArrayList<String>();
	
	static int getNum(HashMap<String,Integer> map, ArrayList<String> rev, String s)
	{
		if (!map.containsKey(s)) 
		{
			map.put(s, map.size());
			rev.add(s);
		}
		return map.get(s);
	}
	public static void main(String[] args) throws Exception
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		int T = Integer.parseInt(in.readLine());
		in.readLine();
		for (int c = 0; c < T; ++c)
		{
			String line;
			nameMap.clear(); partyMap.clear(); clubMap.clear();
			revNameMap.clear(); revPartyMap.clear(); revClubMap.clear();
			ArrayList<ArrayList<Integer>> input = new ArrayList<ArrayList<Integer>>();
			while ((line = in.readLine()) != null)
			{
				if (line.trim().length() == 0)
				{
					if (c > 0) System.out.println();
					process(input);	
					break;
				}
				else
				{
					StringTokenizer st = new StringTokenizer(line);
					ArrayList<Integer> al = new ArrayList<Integer>();
					al.add(getNum(nameMap,revNameMap,st.nextToken())); 
					al.add(getNum(partyMap,revPartyMap,st.nextToken()));
					while (st.hasMoreTokens()) al.add(getNum(clubMap,revClubMap,st.nextToken()));
					input.add(al);
				}
			}
		}
	}
	static void addEdge(int u, int v, int cap, int[][] caps, ArrayList<Integer>[] adj)
	{
		if (caps[u][v] == 0 && caps[v][u] == 0)
		{
			adj[u].add(v); adj[v].add(u);
		} 
		caps[u][v] += cap;
	}
	static void process(ArrayList<ArrayList<Integer>> input)
	{
		int N = nameMap.size(), P = partyMap.size(), C = clubMap.size();
		int s = 0, t = 1, sC = 2, sN = sC+C, sP = sN+N, V = N+P+C+2;
		ArrayList<Integer>[] adj = new ArrayList[V];
		for (int i = 0; i < V; ++i) adj[i] = new ArrayList<Integer>();
		int[][] caps = new int[V][V];
		for (ArrayList<Integer> al : input)
		{
			int n = al.get(0)+sN, p = al.get(1)+sP;
			addEdge(n,p,1,caps,adj);
			for (int i = 2; i < al.size(); ++i) addEdge(al.get(i)+sC,n,1,caps,adj);
		}
		for (int c = 0; c < C; ++c) addEdge(s,c+sC,1,caps,adj);
		for (int p = 0; p < P; ++p) addEdge(p+sP,t,(C-1)/2,caps,adj);
		int flow = maxflow(adj,caps,s,t);
		if (flow < C) System.out.println("Impossible.");
		else
		{
			StringBuilder sb = new StringBuilder();
			for (int n = 0; n < N; ++n)
			{				
				for (int j = 0; j < adj[n+sN].size(); ++j)
				{
					int v = adj[n+sN].get(j);
					if (caps[n+sN][v] > 0 && v >= sC && v < sC+C) 
						sb.append(revNameMap.get(n)).append(" ").append(revClubMap.get(v-sC)).append('\n');
				}
			}
			System.out.print(sb);
		}
	}
	static int maxflow(ArrayList<Integer>[] adj, int[][] caps, int source, int sink)
	{
		int ret = 0;
		while (true)
		{
			int f = augment(adj,caps,source,sink);
			if (f == 0) break;
			ret += f;
		}
		return ret;
	}
	static int augment(ArrayList<Integer>[] adj, int[][] caps, int source, int sink)
	{
		Queue<Integer> q = new ArrayDeque<Integer>();
		int[] pred = new int[adj.length];
		Arrays.fill(pred,-1);
		int[] f = new int[adj.length];
		pred[source] = source; f[source] = Integer.MAX_VALUE; q.add(source);
		while (!q.isEmpty())
		{
			int curr = q.poll(), currf = f[curr];
			if (curr == sink)
			{
				update(caps,pred,curr,f[curr]);
				return f[curr];
			}
			for (int i = 0; i < adj[curr].size(); ++i)
			{
				int j = adj[curr].get(i);
				if (pred[j] != -1 || caps[curr][j] == 0) continue;
				pred[j] = curr; f[j] = Math.min(currf, caps[curr][j]); q.add(j);
			}
		}
		return 0;
	}
	static void update(int[][] caps, int[] pred, int curr, int f)
	{
		int p = pred[curr];
		if (p == curr) return;
		caps[p][curr] -= f;  caps[curr][p] += f;
		update(caps,pred,p,f);
	}
}
