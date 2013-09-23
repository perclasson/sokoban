
public class GameState implements Cloneable {
	private char[][] board;
	private char directionTo;
	private GameState previousState;
	private int x;
	private int y;
	
	public GameState(char[][] board, char directionTo, GameState previousState, int x, int y) {
		this.board = board;
		this.directionTo = directionTo;
		this.previousState = previousState;
		this.x = x;
		this.y = y;
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

	public int getY() {
		return y;
	}

	public int getX() {
		return x;
	}

	@Override
	public int hashCode() {
		return board.hashCode();
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setX(int x) {
		this.x = x;
	}
	
	@Override
	public Object clone(){  
	    try{  
	        return super.clone();  
	    }catch(Exception e){ 
	        return null; 
	    }
	}
}
