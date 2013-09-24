public class GameState implements Cloneable {
	private char[][] board;
	private char directionTo;

	private GameState previousState;
	private int x;
	private int y;
	private int hashCode = -1;
	private String path;
	
	public GameState(char[][] board, char directionTo, GameState previousState, int x, int y) {
		this.board = board;
		this.directionTo = directionTo;
		this.previousState = previousState;
		this.x = x;
		this.y = y;
	}
	
	public GameState(char[][] board, GameState previousState, int x, int y) {
		this.board = board;
		this.previousState = previousState;
		this.x = x;
		this.y = y;
	}

	public void setDirectionTo(char directionTo) {
		this.directionTo = directionTo;
	}
	
	public char[][] getBoard() {
		return board;
	}

	public char getDirectionTo() {
		return directionTo;
	}

	public GameState getPreviousState() {
		return previousState;
	}

	public void setPreviousState(GameState previousState) {
		this.previousState = previousState;
	}
	
	public int getY() {
		return y;
	}

	public int getX() {
		return x;
	}

	@Override
	public int hashCode() {
		if (hashCode == -1) {
			StringBuilder sb = new StringBuilder();
			for(int i = 0 ; i < board.length ; i++) {
				for(int j = 0 ; j < board[i].length ; j++) {
					sb.append(board[i][j]);
				}
			}
			hashCode = sb.toString().hashCode();
		}
		return hashCode;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public static char[][] copyArray(char[][] matrix) {
		char[][] copy = new char[matrix.length][];
		for (int i = 0; i < matrix.length; i++) {
			char[] innerOriginal = matrix[i];
			copy[i] = new char[innerOriginal.length];
			System.arraycopy(innerOriginal, 0, copy[i], 0, innerOriginal.length);
		}
		return copy;
	}

	@Override
	public Object clone() {
		try {
			return new GameState(copyArray(board), directionTo, previousState, x, y);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean equals(Object o) {
		return ((GameState) o).hashCode() == hashCode();
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
	
}
