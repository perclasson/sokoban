import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {
	private static ZobristHasher hasher;
	public static char[][] board;
	private Coordinate initialPosition;
	private Set<Coordinate> goals;
	public Map<State, Integer> f_score;

	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		goals = new HashSet<Coordinate>();
		board = readBoard();
		hasher = new ZobristHasher(board);
		long before = System.currentTimeMillis();
		solve();
		System.out.println("Took "+(System.currentTimeMillis()-before)+" ms");
	}

	private void solve() {
		State root = extractRootState(board);
		String path = "";
		if (root.isPlayerOnGoal()) {
			path = findPathForPlayerOnGoal(root);
		} else {
			path = findPath(root);
		}
		System.out.println(path);
	}

	private String findPathForPlayerOnGoal(State root) {
		List<State> rootStates = findStartPositions(root);
		for (State state : rootStates) {
			String path = findPath(state);
			if (path != null) {
				return path;
			}
		}
		return null;
	}

	private List<State> findStartPositions(State root) {
		List<State> states = new ArrayList<State>();
		for (int i = 0; i < Constants.dx.length; i++) {
			Coordinate newPlayerPos = new Coordinate(root.getPlayer().x + Constants.dx[i], root.getPlayer().y + Constants.dy[i]);
			if (isFreeSpace(root, newPlayerPos)) {
				State newState = root.clone();
				newState.movePlayer(newPlayerPos);
				states.add(newState);
			}
		}
		return states;
	}

	private String findPath(State root) {
		State goal = search(root);
		if (goal == null) {
			System.out.println("No path");
			System.exit(0);
		}
		return recreatePath(goal);
	}

	private State search(State start) {
		Set<State> visited = new HashSet<State>();
		Set<State> openSet = new HashSet<State>();
		openSet.add(start);
		Map<State, Integer> g_score = new HashMap<State, Integer>();
		g_score.put(start, 0);
		f_score = new HashMap<State, Integer>();
		f_score.put(start, g_score.get(start) + start.getValue());
		while (!openSet.isEmpty()) {
			State current = null;
			for (State s : openSet) { 
				if (current == null || f_score.get(s) < f_score.get(current)) {
					current = s;
				}
			}
			openSet.remove(current);

			if (isCompleted(current) && !isStuck(current))
				return current;
			visited.add(current);
			List<State> nextMoves = findPossibleMoves(current);
			for (State move : nextMoves) {
				int tentative_g_score = g_score.get(current) + 1;
				int tentative_f_score = tentative_g_score + move.getValue();
				if (visited.contains(move) && tentative_f_score >= f_score.get(move)) {
					continue;
				}

				if (!visited.contains(move) || tentative_f_score < f_score.get(move)) {
					g_score.put(move, tentative_g_score);
					f_score.put(move, tentative_f_score);
					openSet.add(move);
				}
			}

		}
		return null;
	}

	private boolean isStuck(State state) {
		return AStar.findPath(state, state.getPlayer(), initialPosition) == null;
	}

	public String recreatePath(State goal) {
		StringBuilder sb = new StringBuilder();
		String endPath = AStar.findPath(goal, goal.getPlayer(), initialPosition);

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
		String path = AStar.findPath(state, state.getPlayer(), to);
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
		boolean playerOnGoal = false;
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
					playerOnGoal = true;
					break;
				}
				}
			}
		}
		initialPosition = player.clone();
		State s = new State(-1, player, boxes, null, goals, playerOnGoal);
		s.setHash(hasher.hash(s, player));
		return s;
	}

	private char[][] readBoard() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		List<char[]> tmp = new ArrayList<char[]>();
		int i = 0;
		int breadth = 0;

		try {
			for (i = 0; (line = in.readLine()) != null; i++) {
				char[] lineArray = line.toCharArray();
				breadth = Math.max(breadth, line.length());
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

	private void printSTDState(State state) {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (state.containsBox(new Coordinate(j, i)) && board[i][j] == '.') {
					System.out.print('*');
				} else if (state.containsBox(new Coordinate(j, i))) {
					System.out.print('$');
				} else if (state.getPlayer().x == j && state.getPlayer().y == i && board[i][j] == '.') {
					System.out.print('+');
				} else if (state.getPlayer().x == j && state.getPlayer().y == i) {
					System.out.print('@');
				} else {
					System.out.print(board[i][j]);
				}
			}
			System.out.println();
		}
	}
}
