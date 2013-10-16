import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class Main {
	private static ZobristHasher hasher;
	public static char[][] board;
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
		System.out.println(solve());
		System.out.println("Took " + (System.currentTimeMillis() - before) + " ms");
	}

	public Main(char[][] b) {
		goals = new HashSet<Coordinate>();
		board = b;
		hasher = new ZobristHasher(board);
	}

	public String solve() {
		State root = extractRootState(board);
		String path = "";
		if (root.isPlayerOnGoal()) {
			path = findPathWithNewStartPos(root, root.getPlayer());
		} else {
			path = findPath(root);
		}
		if (path == null) {
			for (Coordinate box : root.getBoxes()) {
				path = findPathWithNewStartPos(root, box);
				if (path == null) {
					break;
				}
			}
		}
		return path;
	}
	

	private String findPathWithNewStartPos(State root, Coordinate position) {
		List<State> rootStates = findStartPositions(root, position);
		for (State state : rootStates) {
			String path = findPath(state);
			if (path != null) {
				return path;
			}
		}
		return null;
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

	private String findPath(State root) {
		State goal = search(root);
		if (goal == null) {
			return null;
		}
		return recreatePath(goal);
	}

	private State search(State start) {
		Set<State> visited = new HashSet<State>();
		PriorityQueue<State> openSet = new PriorityQueue<State>();
		start.gScore = 0;
		start.fScore = start.gScore + start.getValue();
		openSet.add(start);
		// int depth = 200;
		// List<State> newDepth = new ArrayList<State>();

		// while (depth < 5000) {
		while (!openSet.isEmpty()) {
			State current = openSet.poll();

			// if (current.getStepsTo() + current.getValue() > depth) {
			// newDepth.add(current);
			// continue;
			// }
			if (isCompleted(current))
				if(!isStuck(current))
					return current;
				else
					System.out.println("my dung id duck");
			visited.add(current);
			List<State> nextMoves = findPossibleMoves(current);
			for (State neighbor : nextMoves) {
				int tentative_g_score = current.gScore + neighbor.getStepsTo();
				int tentative_f_score = tentative_g_score + neighbor.getValue();
				if (visited.contains(neighbor) && tentative_f_score >= neighbor.fScore) {
					continue;
				}

				if (!visited.contains(neighbor) || tentative_f_score < neighbor.fScore) {
					neighbor.gScore = tentative_g_score;
					neighbor.fScore = tentative_f_score;
					openSet.add(neighbor);
				}
			}
		}
		// depth *= 2;
		// openSet.addAll(newDepth);
		// newDepth.clear();
		// }
		return null;
	}

	private boolean isStuck(State state) {
		return !BoardSearcher.pathExists(state, state.getPlayer(), initialPosition);
	}

	public String recreatePath2(State goal) {
		StringBuilder sb = new StringBuilder();
		String endPath = BoardSearcher.findPath(goal, goal.getPlayer(), initialPosition);
		
		State current = goal;
		while (current != null) {
			if (current.getPath() != null)
				sb.append(current.getPath());
			current = current.getParent();
		}
		return endPath + sb.toString();
	}
	
	public String recreatePath(State goal) {
		StringBuilder sb = new StringBuilder();
		String endPath = BoardSearcher.findPath(goal, goal.getPlayer(), initialPosition);
		State current = goal;
		while(current.getParent() != null) {
		String path = getPath(current.getParent(), current.getPlayer(), current.getPushPosition());
			sb.append(path);
			current = current.getParent();
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
		if(!BoardSearcher.pathExists(state, state.getPlayer(), new Coordinate(box.x + dx, box.y + dy))) {
			return null;
		}
		State newState = state.clone();
		newState.setParent(state);
		newState.moveBox(box, new Coordinate(box.x + dx, box.y + dy));
		newState.movePlayer(new Coordinate(box.x + 2 * dx, box.y + 2 * dy));
		newState.setPushPosition(new Coordinate(box.x + dx, box.y + dy));
		return newState;
	}


	private String getPath2(State state, Coordinate box, Coordinate to) {
		String path = BoardSearcher.findPath(state, state.getPlayer(), to);
		if (path == null) {
			return null;
		}
		if (to.x > box.x) {
			return  path + " L";
		}
		if (to.x < box.x) {
			return path + " R";
		}
		if (to.y > box.y) {
			return path + " U";
		}
		if (to.y < box.y) {
			return path + " D";
		}
		return null;
	}
	
	private String getPath(State state, Coordinate box, Coordinate to) {
		String path = BoardSearcher.findPath(state, state.getPlayer(), to);
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
			case ' ':
				sb.append(' ');
				break;
			default:
				sb.append(toBeInverted);
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
