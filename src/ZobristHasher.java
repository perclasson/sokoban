import java.util.Random;
import java.util.Set;


public class ZobristHasher {
	
	private Random random;
	int[][][] table;
	private final int PLAYER = 0;
	private final int BOX = 1;
	
	public ZobristHasher(char[][] board) {
		random = new Random();
		initiate(board);
	}
	
	private void initiate(char[][] board) {
		int longestCol = Integer.MIN_VALUE;
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
	
	public int hash(Set<Coordinate> boxes, Coordinate player) {
		int hash = 0;
		for(Coordinate box : boxes) {
			hash = hash ^ table[box.y][box.x][BOX];
		}
		hash = hash ^ table[player.y][player.x][PLAYER];
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
	
	public int movePlayer(int hash, int fromX, int fromY, int toX, int toY) {
		return moveTile(hash, fromX, fromY, toX, toY, PLAYER);
	}
	
	/* 
	 * FLYTTA SPELARJÄVELN FÖRST!!!!!!Annars blir det kaos.
	 * 
	 */
	public int moveBox(int hash, int fromX, int fromY, int toX, int toY) {
		return moveTile(hash, fromX, fromY, toX, toY, BOX);
	}
}
