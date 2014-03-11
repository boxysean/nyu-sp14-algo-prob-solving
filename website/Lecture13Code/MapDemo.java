package Lecture13;

import java.util.Arrays;

public class MapDemo {
	static int dr[] = new int[] { 0, 1, 0, -1 };
	static int dc[] = new int[] { 1, 0, -1, 0 };
	
	public static void main(String[] args) {
		int rows = 5;
		int cols = 10;
		
		String mapStr[] = {
			".........E",
			".xxxxxxxxx",
			"..........",
			"xxxxxxxxx.",
			"S.........",
		};
		
		boolean map[][] = new boolean[rows+2][cols+2];
		
		// set all parts of map accessible (== true)
		for (boolean m[] : map) {
			Arrays.fill(m, true);
		}
		
		// Make buffer on LHS and RHS of map
		for (int r = 0; r < rows; r++) {
			map[r][0] = map[r][cols+1] = false;
		}
		
		// Make buffer on top and bottom of map
		for (int c = 0; c < cols; c++) {
			map[0][c] = map[rows+1][c] = false;
		}
		
		// offset the map index by 1
		for (int r = 1; r <= rows; r++) {
			for (int c = 1; c <= cols; c++) {
				map[r][c] = mapStr[r-1].charAt(c-1) == '.';
			}
		}
		
		// count how many cells are accessible in UDLR directionsi
		for (int r = 1; r <= rows; r++) {
			for (int c = 1; c <= cols; c++) {
				int count = 0;
				for (int i = 0; i < 4; i++) {
					if (map[r+dr[i]][c+dc[i]]) {
						count++;
					}
				}
				System.out.printf("(%d, %d) count %d\n", r, c, count);
			}
		}
	}
}
