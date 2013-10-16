import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Main {
	private static final int GOAL_COST_SCALE = 10;
	private static ZobristHasher hasher;
	public static char[][] pullBoard, pushBoard;
	public static int[][] pullManhattanCost, pushManhattanCost;
	private Coordinate initialPosition;
	private Set<Coordinate> pullGoals, pushGoals;
	Map<GameState, GameState> pullVisited = new HashMap<GameState, GameState>();
	Map<GameState, GameState> pushVisited = new HashMap<GameState, GameState>();

	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		pullGoals = new HashSet<Coordinate>();
		pushGoals = new HashSet<Coordinate>();
		pullBoard = readBoard();
		pushBoard = cloneMatrix(pullBoard);
		hasher = new ZobristHasher(pullBoard);
		 GameState pullRoot = extractPullRootState(pullBoard);
		long before = System.currentTimeMillis();
		pullManhattanCost = generateManhattancost(pullBoard, pullGoals);

		// new Thread() {
		// public void run() {
		GameState pushRoot = extractPushRootState(pushBoard);
		DeadlockHandler.addStaticDeadlocks(pushBoard);
		pushManhattanCost = generateManhattancost(pushBoard, pushGoals);
		System.out.println(pushSolve(pushRoot));
		System.out.println("took "+(System.currentTimeMillis()-before));
		System.exit(0);
		// }
		// }.start();

		// System.out.println(pullSolve(pullRoot));
		// System.exit(0);

	}

	public String pushSolve(GameState root) {
		return pushFindPath(root);
	}

	private String pushFindPath(GameState root) {
		GameState goal = pushSearch(root);
		if (goal == null) {
			return null;
		}
		return pushRecreatePath(goal);
	}

	public String pullSolve(GameState root) {
		Set<GameState> startStates = new HashSet<GameState>();
		for (Coordinate box : root.getBoxes()) {
			startStates.addAll(findStartPositions(root, box));
		}
		return pullFindPath(startStates);
	}

	private int[][] generateManhattancost(char[][] board, Set<Coordinate> goals) {
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

	private List<GameState> findStartPositions(GameState root, Coordinate position) {
		List<GameState> states = new ArrayList<GameState>();
		for (int i = 0; i < Constants.dx.length; i++) {
			Coordinate newPlayerPos = new Coordinate(position.x + Constants.dx[i], position.y + Constants.dy[i]);
			if (isFreeSpace(root, newPlayerPos)) {
				GameState newState = root.clone();
				newState.movePlayer(newPlayerPos);
				states.add(newState);
			}
		}
		return states;
	}

	private String pullFindPath(Set<GameState> startStates) {
		GameState goal = pullSearch(startStates);
		if (goal == null) {
			return null;
		}
		return pullRecreatePath(goal);
	}

	private GameState pullSearch(Set<GameState> startingStates) {
		PriorityQueue<GameState> queue = new PriorityQueue<GameState>();
		for (GameState start : startingStates) {
			start.costTo = 0;
			start.totalCost = start.costTo + start.estimateGoalCost(pullManhattanCost) * GOAL_COST_SCALE;
		}
		queue.addAll(startingStates);
		int depth = queue.peek().totalCost;
		List<GameState> IDLeaves = new ArrayList<GameState>();
		while (depth > 0) {
			while (!queue.isEmpty()) {
				GameState current = queue.poll();
				if (pushVisited.containsKey(current)) {
					// TODO: recreate path and return
				}
				if (pullIsCompleted(current) && !isStuck(current))
					return current;
				if (current.totalCost > depth) {
					IDLeaves.add(current);
					continue;
				}
				pullVisited.put(current, current);
				List<GameState> nextMoves = pullFindPossibleMoves(current);
				for (GameState neighbor : nextMoves) {
					int costTo = current.costTo + 1;
					int totalCost = costTo + neighbor.estimateGoalCost(pullManhattanCost) * GOAL_COST_SCALE;
					if (pullVisited.containsKey(neighbor) && totalCost >= neighbor.totalCost) {
						continue;
					}

					if (!queue.contains(neighbor) || totalCost < neighbor.totalCost) {
						neighbor.costTo = costTo;
						neighbor.totalCost = totalCost;
						if (!queue.contains(neighbor))
							queue.add(neighbor);
					}
				}
			}
			queue.addAll(IDLeaves);
			depth += 10;
			IDLeaves.clear();
		}
		return null;
	}

	private GameState pushSearch(GameState start) {
		PriorityQueue<GameState> queue = new PriorityQueue<GameState>();
		start.costTo = 0;
		start.totalCost = start.costTo + start.estimateGoalCost(pushManhattanCost) * GOAL_COST_SCALE;
		queue.add(start);
		int depth = start.totalCost;
		List<GameState> IDLeaves = new ArrayList<GameState>();
		while (depth > 0) {
			while (!queue.isEmpty()) {
				GameState current = queue.poll();
				if (pullVisited.containsKey(current)) {
					// TODO: recreate path and return;
				}
				if (pushIsCompleted(current))
					return current;
				if (current.totalCost > depth) {
					IDLeaves.add(current);
					continue;
				}
				pushVisited.put(current, current);
				List<GameState> nextMoves = pushFindPossibleMoves(current);
				for (GameState neighbor : nextMoves) {
					int costTo = current.costTo + 1;
					int totalCost = costTo + neighbor.estimateGoalCost(pushManhattanCost) * GOAL_COST_SCALE;
					if (pushVisited.containsKey(neighbor) && totalCost >= neighbor.totalCost) {
						continue;
					}

					if (!queue.contains(neighbor) || totalCost < neighbor.totalCost) {
						neighbor.costTo = costTo;
						neighbor.totalCost = totalCost;
						if (!queue.contains(neighbor))
							queue.add(neighbor);
					}
				}
			}
			queue.addAll(IDLeaves);
			depth += 10;
			IDLeaves.clear();
		}
		return null;
	}

	private boolean isStuck(GameState state) {
		return BoardSearcher.findPath(state, state.getPlayer(), initialPosition, pullBoard) == null;
	}

	public String pullRecreatePath(GameState goal) {
		StringBuilder sb = new StringBuilder();
		String endPath = BoardSearcher.findPath(goal, goal.getPlayer(), initialPosition, pullBoard);

		while (goal != null) {
			if (goal.getPath() != null)
				sb.append(goal.getPath());
			goal = goal.getParent();
		}
		return invertPath(endPath + sb.toString());
	}

	public String pushRecreatePath(GameState goal) {
		StringBuilder sb = new StringBuilder();

		while (goal != null) {
			if (goal.getPath() != null)
				sb.append(goal.getPath());
			goal = goal.getParent();
		}
		return sb.reverse().toString().trim();
	}

	private List<GameState> pullFindPossibleMoves(GameState state) {
		List<GameState> moves = new ArrayList<GameState>();
		for (Coordinate box : state.getBoxes()) {
			pullFindMovesForBox(state, box, moves);
		}
		return moves;
	}

	private List<GameState> pushFindPossibleMoves(GameState state) {
		List<GameState> moves = new ArrayList<GameState>();
		for (Coordinate box : state.getBoxes()) {
			pushFindMovesForBox(state, box, moves);
		}
		return moves;
	}

	private void pushFindMovesForBox(GameState state, Coordinate box, List<GameState> moves) {
		GameState newState = null;
		for (int i = 0; i < Constants.dx.length; i++) {
			if (pushIsPossibleMove(state, box, Constants.dx[i], Constants.dy[i])) {
				newState = makePush(state, box, Constants.dx[i], Constants.dy[i]);
				if (newState != null) {
					moves.add(newState);
				}
			}
		}
	}

	private void pullFindMovesForBox(GameState state, Coordinate box, List<GameState> moves) {
		GameState newState = null;
		for (int i = 0; i < Constants.dx.length; i++) {
			if (pullIsPossibleMove(state, box, Constants.dx[i], Constants.dy[i])) {
				newState = makePull(state, box, Constants.dx[i], Constants.dy[i]);
				if (newState != null) {
					moves.add(newState);
				}
			}
		}
	}

	private GameState makePush(GameState state, Coordinate box, int dx, int dy) {
		String path = pushGetPath(state, box, new Coordinate(box.x - dx, box.y - dy));
		if (path == null) {
			return null;
		}
		GameState newState = state.clone();
		newState.setPath(path);
		newState.setParent(state);
		newState.moveBox(box, new Coordinate(box.x + dx, box.y + dy));
		newState.movePlayer(new Coordinate(box.x, box.y));
		return newState;
	}

	private GameState makePull(GameState state, Coordinate box, int dx, int dy) {
		String path = pullGetPath(state, box, new Coordinate(box.x + dx, box.y + dy));
		if (path == null) {
			return null;
		}
		GameState newState = state.clone();
		newState.setPath(path);
		newState.setParent(state);
		newState.moveBox(box, new Coordinate(box.x + dx, box.y + dy));
		newState.movePlayer(new Coordinate(box.x + 2 * dx, box.y + 2 * dy));
		return newState;
	}

	private String pullGetPath(GameState state, Coordinate box, Coordinate to) {
		String path = BoardSearcher.findPath(state, state.getPlayer(), to, pullBoard);
		if (path == null) {
			return null;
		}
		if (to.x > box.x) {
			return "R " + path;
		}
		if (to.x < box.x) {
			return "L " + path;
		}
		if (to.y > box.y) {
			return "D " + path;
		}
		if (to.y < box.y) {
			return "U " + path;
		}
		return null;
	}

	private String pushGetPath(GameState state, Coordinate box, Coordinate to) {
		String path = BoardSearcher.findPath(state, state.getPlayer(), to, pushBoard);
		if (path == null) {
			return null;
		}
		if (to.x > box.x) {
			return "L " + path;
		}
		if (to.x < box.x) {
			return "R " + path;
		}
		if (to.y > box.y) {
			return "U " + path;
		}
		if (to.y < box.y) {
			return "D " + path;
		}
		return null;
	}

	private String invertPath(String path) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < path.length(); i++) {
			char toBeInverted = path.charAt(i);
			switch (toBeInverted) {
			case 'R':
				sb.append('L');
				break;
			case 'L':
				sb.append('R');
				break;
			case 'U':
				sb.append('D');
				break;
			case 'D':
				sb.append('U');
				break;
			default:
				sb.append(' ');
				break;
			}
		}
		return sb.toString();
	}

	private boolean pullIsPossibleMove(GameState state, Coordinate box, int dx, int dy) {
		return isFreeSpace(state, new Coordinate(box.x + dx, box.y + dy)) && isFreeSpace(state, new Coordinate(box.x + 2 * dx, box.y + 2 * dy));
	}

	private boolean pushIsPossibleMove(GameState state, Coordinate box, int dx, int dy) { //TODO: Detect deadlocks in a 3x3 grid around box
		return (pushBoard[box.y+dy][box.x+dx] == Constants.GOAL && !state.containsBox(new Coordinate(box.x+dx, box.y+dy))) ||
				(pushBoard[box.y + dy][box.x + dx] != Constants.DEADLOCK && 
					(isFreeSpace(state, new Coordinate(box.x + dx, box.y + dy)) && isFreeSpace(state, new Coordinate(box.x - dx, box.y - dy))));
	}

	public static ZobristHasher getHasher() {
		return hasher;
	}

	private boolean pullIsCompleted(GameState state) {
		for (Coordinate box : state.getBoxes()) {
			if (pullBoard[box.y][box.x] != Constants.GOAL) {
				return false;
			}
		}
		return true;
	}

	private boolean pushIsCompleted(GameState state) {
		for (Coordinate box : state.getBoxes()) {
			if (pushBoard[box.y][box.x] != Constants.GOAL) {
				return false;
			}
		}
		return true;
	}
	
	private GameState extractPullRootState(char[][] board) {
		Set<Coordinate> boxes = new HashSet<Coordinate>();
		Coordinate player = null;
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[y].length; x++) {
				switch (board[y][x]) {
				case Constants.BOX: {
					board[y][x] = Constants.GOAL;
					pullGoals.add(new Coordinate(x, y));
					break;
				}
				case Constants.BOX_ON_GOAL: {
					boxes.add(new Coordinate(x, y));
					board[y][x] = Constants.GOAL;
					pullGoals.add(new Coordinate(x, y));
					break;
				}
				case Constants.PLAYER: {
					player = new Coordinate(x, y);
					board[y][x] = Constants.SPACE;
					break;
				}
				case Constants.GOAL: {
					boxes.add(new Coordinate(x, y));
					board[y][x] = Constants.SPACE;
					break;
				}
				case Constants.PLAYER_ON_GOAL: {
					boxes.add(new Coordinate(x, y));
					player = new Coordinate(x, y);
					board[y][x] = Constants.SPACE;
					break;
				}
				}
			}
		}
		initialPosition = player.clone();
		GameState s = new GameState(-1, player, boxes, null, pullGoals);
		s.setHash(hasher.hash(s, player));
		return s;
	}

	private GameState extractPushRootState(char[][] board) {
		Set<Coordinate> boxes = new HashSet<Coordinate>();
		Coordinate player = null;
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[y].length; x++) {
				switch (board[y][x]) {
				case Constants.BOX: {
					boxes.add(new Coordinate(x, y));
					board[y][x] = Constants.SPACE;
					break;
				}
				case Constants.BOX_ON_GOAL: {
					boxes.add(new Coordinate(x, y));
					board[y][x] = Constants.GOAL;
					pushGoals.add(new Coordinate(x, y));
					break;
				}
				case Constants.PLAYER: {
					player = new Coordinate(x, y);
					board[y][x] = Constants.SPACE;
					break;
				}
				case Constants.GOAL: {
					pushGoals.add(new Coordinate(x, y));
					break;
				}
				case Constants.PLAYER_ON_GOAL: {
					board[y][x] = Constants.GOAL;
					player = new Coordinate(x, y);
					break;
				}
				}
			}
		}
		initialPosition = player.clone();
		GameState s = new GameState(-1, player, boxes, null, pushGoals);
		s.setHash(hasher.hash(s, player));
		return s;
	}

	private char[][] readBoard() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		List<char[]> tmp = new ArrayList<char[]>();
		int i = 0;

		try {
			for (i = 0; (line = in.readLine()) != null; i++) {
				char[] lineArray = line.toCharArray();
				tmp.add(lineArray);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		pullBoard = new char[i][];
		for (int j = 0; j < pullBoard.length; j++) {
			pullBoard[j] = tmp.get(j);
		}
		return pullBoard;
	}

	public static boolean isFreeSpace(GameState state, Coordinate coordinate) {
		return !state.containsBox(coordinate) && pullBoard[coordinate.y][coordinate.x] != Constants.WALL;
	}

	public static void printMatrix(char[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				System.err.print(matrix[i][j]);
			}
			System.err.println();
		}
	}

	public static void printState(GameState state, char[][] board) {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (state.containsBox(new Coordinate(j, i)) && board[i][j] == '.') {
					System.err.print('*');
				} else if (state.containsBox(new Coordinate(j, i))) {
					System.err.print('$');
				} else if (state.getPlayer().x == j && state.getPlayer().y == i && board[i][j] == '.') {
					System.err.print('+');
				} else if (state.getPlayer().x == j && state.getPlayer().y == i) {
					System.err.print('@');
				} else {
					System.err.print(board[i][j]);
				}
			}
			System.err.println();
		}
	}

	private char[][] cloneMatrix(char[][] original) {
		char[][] clone = new char[original.length][];
		for (int i = 0; i < original.length; i++) {
			clone[i] = Arrays.copyOf(original[i], original[i].length);
		}
		return clone;
	}
}
