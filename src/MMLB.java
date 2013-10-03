import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class MMLB {

	/*
	 * Minimum Matching Lower Bound
	 */
	public static int calcMMLB(List<String> tmpBoard) {
		char[][] board = new char[tmpBoard.size()][];
		int numBoxes = 0;
		ArrayList<Coordinate> boxes = new ArrayList<Coordinate>();
		ArrayList<Coordinate> goals = new ArrayList<Coordinate>();
		String line = "";
		for (int y = 0; y < tmpBoard.size(); y++) {
			line = tmpBoard.get(y);
			board[y] = line.toCharArray();
			for (int i = 0; i < line.length(); i++) {
				if (line.charAt(i) == '$' || line.charAt(i) == '*') {
					numBoxes += 1;
					boxes.add(new Coordinate(i, y));
				} else if (line.charAt(i) == '.' || line.charAt(i) == '*'
						|| line.charAt(i) == '+') {
					goals.add(new Coordinate(i, y));
				}
			}
		}
		int[][] manhattanDist = new int[numBoxes][numBoxes];
		int[][] manhattanDistOrg = new int[numBoxes][numBoxes];
		int x = 0;
		int y = 0;
		int dist = 0;
		for (Coordinate box : boxes) {
			for (Coordinate goal : goals) {
				dist = Math.abs(box.x - goal.x) + Math.abs(box.x - goal.x);
				manhattanDist[x][y] = dist;
				manhattanDistOrg[x][y] = dist;
				x++;
			}
			x = 0;
			y++;
		}
		return startMMLB(manhattanDist, manhattanDistOrg);

	}
	


	private static int startMMLB(int[][] manhattanDist, int[][] manhattanDistOrg) {
		/*
		 * Step 3
		 * 
		 * Reduce the rows by subtracting the minimum value of each row from
		 * that row.
		 */
		for (int r = 0; r < manhattanDist.length; r++) {
			int min = minEle(manhattanDist[r]);
			for (int c = 0; c < manhattanDist.length; c++) {
				manhattanDist[r][c] = manhattanDist[r][c] - min;
			}
		}
		/*
		 * Step 4
		 * 
		 * Reduce the columns by subtracting the minimum value of each column
		 * from that column.
		 */
		for (int c = 0; c < manhattanDist.length; c++) {
			int min = minEleCol(manhattanDist, c);
			for (int r = 0; r < manhattanDist.length; r++) {
				manhattanDist[r][c] = manhattanDist[r][c] - min;
			}
		}
		/*
		 * Step 5
		 * 
		 * Cover the zero elements with the minimum number of lines it is
		 * possible to cover them with. (If the number of lines is equal to the
		 * number of rows then go to step 9)
		 */
		HashSet<Integer> coveredRows = new HashSet<Integer>();
		HashSet<Integer> coveredColumns = new HashSet<Integer>();
		for (int r = 0; r < manhattanDist.length; r++) {
			for (int c = 0; c < manhattanDist.length; c++) {
				if (manhattanDist[r][c] == 0) {
					if (coveredRows.contains(r)) {
						continue;
					} else if (coveredColumns.contains(c)) {
						continue;
					} else {
						if (countZerosRow(manhattanDist, c) < countZerosCol(
								manhattanDist, r)) {
							coveredRows.add(r);
						} else {
							coveredColumns.add(c);
						}
					}
				}
			}
		}
		if ((coveredColumns.size() + coveredRows.size()) != manhattanDist.length) {
			/*
			 * Step 6
			 * 
			 * Add the minimum uncovered element to every covered element. 
			 * If an element is covered twice, add the minimum element to it twice.
			 */
			do {
				int minCov = findMinCovered(manhattanDist, coveredRows,
						coveredColumns);
				manhattanDist = addToAll(manhattanDist, minCov, coveredRows,
						coveredColumns);
				/*
				 * Step 7
				 * 
				 * Subtract the minimum element from every element in the matrix.
				 */
				manhattanDist = removeFromAll(manhattanDist, minCov);
				/*
				 * Step 8
				 * 
				 * Cover the zero elements again. If the number of lines covering the zero 
				 * elements is not equal to the number of rows, return to step 6.
				 */
				coveredRows = new HashSet<Integer>();
				coveredColumns = new HashSet<Integer>();
				for (int r = 0; r < manhattanDist.length; r++) {
					for (int c = 0; c < manhattanDist.length; c++) {
						if (manhattanDist[r][c] == 0) {
							if (coveredRows.contains(r)) {
								continue;
							} else if (coveredColumns.contains(c)) {
								continue;
							} else {
								if (countZerosRow(manhattanDist, c) < countZerosCol(
										manhattanDist, r)) {
									coveredRows.add(r);
								} else {
									coveredColumns.add(c);
								}
							}
						}
					}
				}
				System.out.println("Cover: " + (coveredColumns.size() + coveredRows.size()));
				System.out.println("Number of boxes: " + manhattanDist.length);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} while ((coveredColumns.size() + coveredRows.size()) != manhattanDist.length);
		}
		/*
		 * Step 9
		 * Select a matching by choosing a set of zeros so that each row or column has only one selected.
		 */
		coveredRows = new HashSet<Integer>();
		coveredColumns = new HashSet<Integer>();
		List<Coordinate> assignment = new ArrayList<Coordinate>();
		for(int c = 0; c < manhattanDist.length; c++ ) {
			Coordinate first = null;
			boolean selected = false;
			for(int r = 0; r < manhattanDist.length; r++) {
				if(manhattanDist[r][c] == 0) {
					if(first == null)
						first = new Coordinate(c, r);
					if(((!coveredRows.contains(r)) && (!coveredColumns.contains(c)))) {
						assignment.add(new Coordinate(c,r));
						selected = true;
						coveredRows.add(r);
						coveredColumns.add(c);
						break;
					}
				}
			}
			if(!selected) {
				assignment.add(first);
			}
		}
		int value = 0;
		for(Coordinate cor : assignment) {
			value += manhattanDistOrg[cor.y][cor.x];
		}
		if(assignment.size() != manhattanDist.length) {
			System.out.println("AH MEN VA FAAAAN");
		}
		return value;
	}
	
	private static boolean containsOtherZeroesR(int row, int matrix, int currentCol) {
		
		return true;
	}
	
	private static boolean containsOtherZeroesC(int row, int matrix, int currentCol) {
		return false;
	}
	private static int[][] removeFromAll(int[][] matrix, int val) {
		for (int r = 0; r < matrix.length; r++) {
			for (int c = 0; c < matrix.length; c++) {
				matrix[r][c] = matrix[r][c] - val;
			}
		}
		return matrix;
	}

	private static int[][] addToAll(int[][] matrix, int min,
			HashSet<Integer> coveredRows, HashSet<Integer> coveredColumns) {
		for (int r = 0; r < matrix.length; r++) {
			for (int c = 0; c < matrix.length; c++) {
				if (coveredRows.contains(r)) {
					matrix[r][c] = matrix[r][c] + min;
				}
				if (coveredColumns.contains(c)) {
					matrix[r][c] = matrix[r][c] + min;
				}
			}
		}
		return matrix;
	}

	private static int findMinCovered(int[][] matrix, HashSet<Integer> coveredRows,
			HashSet<Integer> coveredColumns) {
		int minCov = Integer.MAX_VALUE;
		for (int r = 0; r < matrix.length; r++) {
			for (int c = 0; c < matrix.length; c++) {
				if (coveredRows.contains(r)) {
					if (matrix[r][c] < minCov) {
						minCov = matrix[r][c];
					}
				} else if (coveredColumns.contains(c)) {
					if (matrix[r][c] < minCov) {
						minCov = matrix[r][c];
					}
				}
			}
		}
		return minCov;
	}

	private static int countZerosCol(int[][] matrix, int col) {
		int zeros = 0;
		for (int r = 0; r < matrix.length; r++) {
			if (matrix[r][col] == 0)
				zeros++;
		}
		return zeros;
	}

	private static int countZerosRow(int[][] matrix, int row) {
		int zeros = 0;
		for (int c = 0; c < matrix.length; c++) {
			if (matrix[row][c] == 0)
				zeros++;
		}
		return zeros;
	}

	private static int minEleCol(int[][] matrix, int col) {
		int min = Integer.MAX_VALUE;
		for (int r = 0; r < matrix[col].length; r++) {
			if (matrix[r][col] < min)
				min = matrix[r][col];
		}
		return min;
	}

	private static int minEle(int[] list) {
		int min = Integer.MAX_VALUE;
		for (int i = 0; i < list.length; i++) {
			if (list[i] < min)
				min = list[i];
		}
		return min;
	}
}
