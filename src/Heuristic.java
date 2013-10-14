import java.util.LinkedList;
import java.util.List;

public class Heuristic {
	private char[][] board;
	private int[][] boardValues;
	private int[][] visited;

	public Heuristic(char[][] board) {
		this.board = board;
		visited = new int[board.length][];
		boardValues = new int[board.length][];
		for(int i = 0; i < board.length; i++) {
			visited[i] = new int[board[i].length];
			boardValues[i] = new int[board[i].length];
		}
		setBoardValues();
	}

	private void setBoardValues() {
		boolean hasEncounteredWall = false;
		for(int r = 0; r < boardValues.length; r++) {
			for(int c = 0; c < boardValues[r].length; c++) {
				if(board[r][c] == '#')
					hasEncounteredWall = true;
				boardValues[r][c] = -1;
				if(board[r][c] != '#' && hasEncounteredWall) {
					boardValues[r][c] = Integer.MAX_VALUE;
					search(new Coordinate(c, r));
					resetVisited();
				}
			}
			hasEncounteredWall = false;
		}
	}
	
	private void resetVisited() {
		for(int i = 0; i < board.length; i++) {
			visited[i] = new int[board[i].length];
		}
	}
	public int getValue(List<Coordinate> boxes) {
		int value = 0;
		for (Coordinate box : boxes) {
			value += boardValues[box.y][box.x];
		}
		return value;
	}

	public void search(Coordinate root) {
		if(board[root.y][root.x] == '.') {
			boardValues[root.y][root.x] = 0;
			return;
		}
		LinkedList<Coordinate> Q = new LinkedList<Coordinate>();
		Q.addLast(root);
		root.parent = null;
		Coordinate u = null;
		int[] dY = {1, 0, -1, 0};
		int[] dX = {0, 1, 0, -1};
		while (!Q.isEmpty()) {
			u = Q.pop();
			for(int i = 0; i < 4; i++) {
				char nextTile = board[u.y+dY[i]][u.x+dX[i]];
				if(nextTile == '.') {
					int dist = backTrackDistance(u) +1;
					if(dist < boardValues[root.y][root.x]) {
						boardValues[root.y][root.x]  = dist;
					}
				} else if (nextTile != '#' && !(visited[u.y+dY[i]][u.x+dX[i]] == 1)) {
					Coordinate next = new Coordinate(u.x+dX[i], u.y+dY[i]);
					next.parent = u;
					Q.addLast(next);
					visited[u.y+dY[i]][u.x+dX[i]] = 1;
				}
			}
		}
	}
	
	public void printBoard() {
		for(int r = 0; r < board.length; r++) {
			for(int c = 0; c < board[r].length; c++) {
				System.out.print(board[r][c] + " ");
			}
			System.out.println();
		}
	}
	
	public void printBoardValues() {
		for(int r = 0; r < boardValues.length; r++) {
			for(int c = 0; c < boardValues[r].length; c++) {
				System.out.print(boardValues[r][c] + " ");
			}
			System.out.println();
		}
	}
		
	public int backTrackDistance(Coordinate last) {
		int dist = 0;
		while(last.parent != null) {
			dist++;
			last = last.parent;
		}
		return dist;
	}
}
