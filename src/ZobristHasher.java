import java.util.LinkedList;
import java.util.Random;
import java.util.Set;


public class ZobristHasher {
	
	private Random random;
	int[][][] table;
	private final int PLAYER = 0;
	private final int BOX = 1;
	private int longestCol;
	private char[][] board;
	
	public ZobristHasher(char[][] board) {
		this.board = board;
		random = new Random();
		initiate(board);
	}
	
	private void initiate(char[][] board) {
		longestCol = Integer.MIN_VALUE;
		table = new int[board.length][][];
		for(int r = 0; r < board.length; r++) {
			if(board[r].length > longestCol) {
				longestCol = board[r].length;
			}
		}
		table = new int[board.length][longestCol][2];
		for(int r = 0; r <  board.length; r++) {
			for(int c = 0; c < longestCol; c++) {
				table[r][c][PLAYER] = randomBitString().hashCode();
				table[r][c][BOX] = randomBitString().hashCode();
			}
		}
	}
	
	public int hash(State state, Coordinate player) {
		int hash = 0;
		for(Coordinate box : state.getBoxes()) {
			hash = hash ^ table[box.y][box.x][BOX];
		}
		Coordinate topMostLeft = findTopLeftmostPosition(state);
		hash = hash ^ table[topMostLeft.y][topMostLeft.x][PLAYER];
		state.setTopLeftmostPosition(topMostLeft);
		return hash;
	}
		
	private String randomBitString() {
		byte[] r = new byte[256]; //Means 2048 bit
		random.nextBytes(r);
		return new String(r);
	}
	
	private int moveTile(int hash, int fromX, int fromY, int toX, int toY, int tile)  {
		hash = hash ^ table[fromY][fromX][tile];
		hash = hash ^ table[toY][toX][tile];
		return hash;
	}
	
	public int updatePlayerHash(State state) {
		System.err.println(state.getPlayer().toString());
		Main.printState(state);
		Coordinate newTopLeftmost = findTopLeftmostPosition(state);
		Coordinate oldTopLeftmost = state.getTopLeftmost();
		state.setTopLeftmostPosition(newTopLeftmost);
		return moveTile(state.hashCode(), oldTopLeftmost.x, oldTopLeftmost.y, newTopLeftmost.x, newTopLeftmost.y, PLAYER);
	}
	
	/* 
	 * FLYTTA SPELARJÄVELN FÖRST!!!!!!Annars blir det kaos.
	 * 
	 */
	public int moveBox(int hash, int fromX, int fromY, int toX, int toY) {
		return moveTile(hash, fromX, fromY, toX, toY, BOX);
	}
	
	private Coordinate findTopLeftmostPosition(State state) {
		char[][] visited = new char[board.length][longestCol];
		LinkedList<Coordinate> queue = new LinkedList<Coordinate>();
		Coordinate current = null;
		Coordinate topMostleft = state.getPlayer();
		int topMostLeftSum = topMostleft.x + topMostleft.y;
		queue.add(state.getPlayer());
		while(!queue.isEmpty()) {
			current = queue.pop();
			visited[current.y][current.x] = 'V';
			if(current.x + current.y < topMostLeftSum) {
				topMostleft = current;
				topMostLeftSum = current.x + current.y;
			}
			for(int i = 0; i < Constants.dx.length; i++) {
				int x = current.x + Constants.dx[i];
				int y = current.y + Constants.dy[i];
				Coordinate c = new Coordinate(x, y);
				if(visited[y][x] != 'V' && Main.isFreeSpace(state, c)) {
					queue.add(c);
				}
			}
		}
		return topMostleft;
	}
}
