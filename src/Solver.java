import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;


public abstract class Solver {
	char[][] board;
	Map<GameState, GameState> pullVisited;
	Map<GameState, GameState> pushVisited;
	Semaphore threadBlocker;
	int[][] manhattanCost;
	Set<Coordinate> goals;
	GameState root;
	
	public Solver(char[][] board, Map<GameState, GameState> pullVisited, Map<GameState, GameState> pushVisited, Semaphore threadBlocker) {
		this.board = board;
		goals = new HashSet<Coordinate>();
		this.pullVisited = pullVisited;
		this.pushVisited = pushVisited;
		this.threadBlocker = threadBlocker;
		root = extractRootState();
		manhattanCost = generateManhattancost(board, goals);
	}
	
	public abstract String recreateMergePath(GameState meetingPoint);
	public abstract String recreateAlonePath(GameState goal);
	
	public abstract GameState solve();
	
	abstract GameState extractRootState();
	
	int[][] generateManhattancost(char[][] board, Set<Coordinate> goals) {
		int[][] manhattanCost = new int[board.length][];
		for (int y = 0; y < board.length; y++) {
			manhattanCost[y] = new int[board[y].length];
			for (int x = 0; x < board[y].length; x++) {
				int min = Integer.MAX_VALUE;
				for (Coordinate goal : goals) {
					int manhattan = Math.abs(goal.x - x) + Math.abs(goal.y - y);
					if (min > manhattan)
						min = manhattan;
				}
				manhattanCost[y][x] = min;
			}
		}
		return manhattanCost;
	}
}
