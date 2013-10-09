import java.util.ArrayList;
import java.util.Comparator;



public class Heuristic implements Comparator<GameState>{
	char[][] board;
	
	public Heuristic(char[][] board) {
		this.board = board;
	}
	
	public int stupidHeuristic(GameState state) {
		ArrayList<Coordinate> boxes = new ArrayList<Coordinate>();
		ArrayList<Coordinate> goals = new ArrayList<Coordinate>();
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[y].length; x++) {
				if (state.containsBox(y, x)) {
					boxes.add(new Coordinate(x, y));
				} else if (board[y][x] == '.' || board[y][x] == '*'
						|| board[y][x] == '+') {
					goals.add(new Coordinate(x, y));
				}
			}
		}
		int total = 0;
		int dist = 0;
		for (Coordinate box : boxes) {
			int min = Integer.MAX_VALUE;
			for (Coordinate goal : goals) {
				dist = Math.abs(box.x - goal.x) + Math.abs(box.x - goal.x);
				if(dist < min) {
					min = dist;
				}
			}
			total += min;
		}
		return total;
	}


	@Override
	public int compare(GameState state0, GameState state1) {
		if(stupidHeuristic(state0) < stupidHeuristic(state1)) {
			return 1;
		} else if(stupidHeuristic(state0) > stupidHeuristic(state1)) {
			return -1;
		}
		return 0;
	}
}
