import java.util.LinkedList;
import java.util.Random;


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
	
	public int hash(GameState state, Coordinate player) {
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
	
	public int updatePlayerHash(GameState state) {
		Coordinate newTopLeftmost = findTopLeftmostPosition(state);
		Coordinate oldTopLeftmost = state.getTopLeftmost();
		state.setTopLeftmostPosition(newTopLeftmost);
		return moveTile(state.hashCode(), oldTopLeftmost.x, oldTopLeftmost.y, newTopLeftmost.x, newTopLeftmost.y, PLAYER);
	}
	
	public int moveBox(int hash, int fromX, int fromY, int toX, int toY) {
		return moveTile(hash, fromX, fromY, toX, toY, BOX);
	}
	
	private Coordinate findTopLeftmostPosition(GameState state) {
		char[][] visited = new char[board.length][longestCol];
		LinkedList<Coordinate> queue = new LinkedList<Coordinate>();
		Coordinate current = null;
		
		Coordinate topLeftmost = state.getPlayer();
		visited[topLeftmost.y][topLeftmost.x] = 'V';
		queue.add(state.getPlayer());
		while(!queue.isEmpty()) {
			current = queue.pop();
			if((current.x < topLeftmost.x) || (current.x == topLeftmost.x && current.y < topLeftmost.y)) {
				topLeftmost = current;
			}
			for(int i = 0; i < Constants.dx.length; i++) {
				int x = current.x + Constants.dx[i];
				int y = current.y + Constants.dy[i];
				Coordinate c = new Coordinate(x, y);
				if(visited[y][x] != 'V') {
					visited[y][x] = 'V';
					if(Main.isFreeSpace(state, c)) {
						queue.add(c);
					}
				}
				
			}
		}
		return topLeftmost;
	}
	
}
