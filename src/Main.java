import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class Main {
	private static final int GOAL_COST_SCALE = 10;
	private static ZobristHasher hasher;
	public static char[][] board;
	public static int[][] manhattanCost;
	private Coordinate initialPosition;
	private Set<Coordinate> goals;

	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		goals = new HashSet<Coordinate>();
		board = readBoard();
		hasher = new ZobristHasher(board);
		long before = System.currentTimeMillis();
		String answer = solve();
		if (answer == null)
			throw new RuntimeException();
		System.out.println(answer);
//		System.out.println("Took " + (System.currentTimeMillis() - before) + " ms");
	}

	public Main(char[][] b) {
		goals = new HashSet<Coordinate>();
		board = b;
		hasher = new ZobristHasher(board);
	}

	public String solve() {
		State root = extractRootState(board);
		manhattanCost = generateManhattancost();
		Set<State> startStates = new HashSet<State>();
		for (Coordinate box : root.getBoxes()) {
			startStates.addAll(findStartPositions(root, box));
		}
		return findPath(startStates);
	}

	private int[][] generateManhattancost() {
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

	private List<State> findStartPositions(State root, Coordinate position) {
		List<State> states = new ArrayList<State>();
		for (int i = 0; i < Constants.dx.length; i++) {
			Coordinate newPlayerPos = new Coordinate(position.x + Constants.dx[i], position.y + Constants.dy[i]);
			if (isFreeSpace(root, newPlayerPos)) {
				State newState = root.clone();
				newState.movePlayer(newPlayerPos);
				states.add(newState);
			}
		}
		return states;
	}

	private String findPath(Set<State> startStates) {
		State goal = search(startStates);
		if (goal == null) {
			return null;
		}
		return recreatePath(goal);
	}

	private State search(Set<State> startingStates) {
		Set<State> visited = new HashSet<State>();
		PriorityQueue<State> queue = new PriorityQueue<State>();
		for (State start : startingStates) {
			start.costTo = 0;
			start.totalCost = start.costTo + start.estimateGoalCost() * GOAL_COST_SCALE;
		}
		queue.addAll(startingStates);
		int depth = queue.peek().totalCost;
		List<State> IDLeaves = new ArrayList<State>();
		while (depth > 0) {
			while (!queue.isEmpty()) {
				State current = queue.poll();
				if (isCompleted(current) && !isStuck(current))
					return current;
				if(current.totalCost > depth) {
					IDLeaves.add(current);
					continue;
				}
				visited.add(current);
				List<State> nextMoves = findPossibleMoves(current);
				for (State neighbor : nextMoves) {
					int costTo = current.costTo + 1;
					int totalCost = costTo + neighbor.estimateGoalCost() * GOAL_COST_SCALE;
					if (visited.contains(neighbor) && totalCost >= neighbor.totalCost) {
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

	private boolean isStuck(State state) {
		return BoardSearcher.findPath(state, state.getPlayer(), initialPosition) == null;
	}

	public String recreatePath(State goal) {
		StringBuilder sb = new StringBuilder();
		String endPath = BoardSearcher.findPath(goal, goal.getPlayer(), initialPosition);

		while (goal != null) {
			if (goal.getPath() != null)
				sb.append(goal.getPath());
			goal = goal.getParent();
		}
		return invertPath(endPath + sb.toString());
	}

	private List<State> findPossibleMoves(State state) {
		List<State> moves = new ArrayList<State>();
		for (Coordinate box : state.getBoxes()) {
			findMovesForBox(state, box, moves);
		}
		return moves;
	}

	private void findMovesForBox(State state, Coordinate box, List<State> moves) {
		State newState = null;
		for (int i = 0; i < Constants.dx.length; i++) {
			if (isPossibleMove(state, box, Constants.dx[i], Constants.dy[i])) {
				newState = makeMove(state, box, Constants.dx[i], Constants.dy[i]);
				if (newState != null) {
					moves.add(newState);
				}
			}
		}
	}

	private State makeMove(State state, Coordinate box, int dx, int dy) {
		String path = getPath(state, box, new Coordinate(box.x + dx, box.y + dy));
		if (path == null) {
			return null;
		}
		State newState = state.clone();
		newState.setPath(path);
		newState.setParent(state);
		newState.moveBox(box, new Coordinate(box.x + dx, box.y + dy));
		newState.movePlayer(new Coordinate(box.x + 2 * dx, box.y + 2 * dy));
		return newState;
	}

	private String getPath(State state, Coordinate box, Coordinate to) {
		String path = BoardSearcher.findPath(state, state.getPlayer(), to);
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

	private boolean isPossibleMove(State state, Coordinate box, int dx, int dy) {
		return isFreeSpace(state, new Coordinate(box.x + dx, box.y + dy)) && isFreeSpace(state, new Coordinate(box.x + 2 * dx, box.y + 2 * dy));
	}

	public static ZobristHasher getHasher() {
		return hasher;
	}

	private boolean isCompleted(State state) {
		for (Coordinate box : state.getBoxes()) {
			if (board[box.y][box.x] != Constants.GOAL) {
				return false;
			}
		}
		return true;
	}

	private State extractRootState(char[][] board) {
		Set<Coordinate> boxes = new HashSet<Coordinate>();
		Coordinate player = null;
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[y].length; x++) {
				switch (board[y][x]) {
				case Constants.BOX: {
					board[y][x] = Constants.GOAL;
					goals.add(new Coordinate(x, y));
					break;
				}
				case Constants.BOX_ON_GOAL: {
					boxes.add(new Coordinate(x, y));
					board[y][x] = Constants.GOAL;
					goals.add(new Coordinate(x, y));
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
		State s = new State(-1, player, boxes, null, goals);
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

		board = new char[i][];
		for (int j = 0; j < board.length; j++) {
			board[j] = tmp.get(j);
		}
		return board;
	}

	public static boolean isFreeSpace(State state, Coordinate coordinate) {
		return !state.containsBox(coordinate) && board[coordinate.y][coordinate.x] != Constants.WALL;
	}

	public static void printMatrix(char[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				System.err.print(matrix[i][j]);
			}
			System.err.println();
		}
	}

	public static void printState(State state) {
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
}
