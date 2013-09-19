
public class GameState {
	private char[][] board;
	private char directionTo;
	private GameState previousState;
	
	public GameState(char[][] board, char directionTo, GameState previousState) {
		this.board = board;
		this.directionTo = directionTo;
		this.previousState = previousState;
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

	
}
