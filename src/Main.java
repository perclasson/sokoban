import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

public class Main {
	public static final boolean TEST = false;
	public static boolean RENDER = false;
	public static final char SPACE = ' ';
	public static final char WALL = '#';
	public static final char GOAL = '.';
	public static final char PLAYER = '@';
	public static final char PLAYER_ON_GOAL = '+';
	public static final char BOX = '$';
	public static final char BOX_ON_GOAL = '*';
	public static final int[] dx = { -1, 1, 0, 0 };
	public static final int[] dy = { 0, 0, -1, 1 };
	public static final int[] bigdx = { -1, -1, -1, 0, 0, 1, 1, 1, 0 };
	public static final int[] bigdy = { -1, 0, 1, -1, 1, -1, 0, 1, 0 };

	private int hits = 0;

	private Set<GameState> visited;
	private RenderFrame renderer;
	public static char[][] board;

	public static void main(String[] args) {
		if (args.length != 0)
			if (args[0].contains("-g")) {
				RENDER = true;
			}
		new Main();
	}

	public Main() {
		visited = new HashSet<GameState>();
		if (RENDER) {
			renderer = new RenderFrame();
			renderer.pack();
		}
		BufferedReader in = getBufferedReader();
		List<String> tmpBoard = readBoard(in);

		GameState root = generateRoot(tmpBoard);
		System.out.println(findPath(root));
	}

	private GameState generateRoot(List<String> tmpBoard) {
		board = new char[tmpBoard.size()][];
		BoxList bl = new BoxList();
		int playerX = 0, playerY = 0;
		for (int y = 0; y < tmpBoard.size(); y++) {
			board[y] = tmpBoard.get(y).toCharArray();
			for (int x = 0; x < tmpBoard.get(y).toCharArray().length; x++) {
				switch (tmpBoard.get(y).charAt(x)) {
				case BOX:
					board[y][x] = SPACE;
					bl.addBox(x, y);
					break;
				case BOX_ON_GOAL:
					board[y][x] = GOAL;
					bl.addBox(x, y);
					break;
				case PLAYER:
					board[y][x] = SPACE;
					playerX = x;
					playerY = y;
					break;
				case PLAYER_ON_GOAL:
					board[y][x] = GOAL;
					playerX = x;
					playerY = y;
					break;
				default:
					board[y][x] = tmpBoard.get(y).charAt(x);
					break;
				}
			}
		}
		return new GameState(bl, playerX, playerY);
	}

	private String findPath(GameState root) {
		GameState goal = search(root);
		return recreatePath(goal).trim();
	}

	private String recreatePath(GameState goal) {
		GameState current = goal;
		StringBuilder sb = new StringBuilder();
		Stack<GameState> stack = new Stack<GameState>();

		while (current != null) {
			if (RENDER)
				stack.add(current);
			String path = current.getPath();
			if (path != null)
				sb.append(current.getPath());
			current = current.getPreviousState();
		}

		if (RENDER) {
			while (!stack.empty()) {
				printState(stack.pop());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.reverse().toString();
	}

	private void printState(GameState gs) {
		if (RENDER) {
			renderer.renderState(board, gs);
		} else {
			BoxList bl = gs.getBoxList();
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board[i].length; j++) {
					if (bl.containsBox(j, i)) {
						if (board[i][j] == '.') {
							System.out.print('*');
						} else {
							System.out.print("$");
						}
					} else if (gs.x == j && gs.y == i) {
						if (board[i][j] == '.') {
							System.out.print('+');
						} else {
							System.out.print("@");
						}
					} else {
						System.out.print(board[i][j]);
					}
				}
				System.out.println();
			}
		}
	}

	private GameState search(GameState current) {
		if (isCompleted(current)) {
			return current;
		}
		List<GameState> possibleStates = findPossibleMoves(current);
		visited.add(current);
		for (GameState state : possibleStates) {
			GameState result = search(state);

			if (result != null)
				return result;
		}
		return null;
	}

	/*
	 * private LinkedList<GameState> queue = new LinkedList<GameState>();
	 * 
	 * private GameState searchBFS(GameState current) { printState(current);
	 * 
	 * if (isCompleted(current)) { return current; }
	 * 
	 * visited.add(current); queue.addAll(findPossibleMoves(current));
	 * 
	 * return searchBFS(queue.pop()); }
	 */

	private boolean isDeadlock(GameState state, int bx, int by) {
		if (board[by][bx] == GOAL)
			return false;
		for (int i = 0; i < 9; i++) {
			if (!isStuck(state, bx + bigdx[i], by + bigdy[i])) {
				return false;
			}
		}
		return true;
	}

	private boolean isCompleted(GameState gs) {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] == GOAL && !gs.containsBox(j, i)) {
					return false;
				}
			}
		}
		return true;
	}

	private ArrayList<GameState> findPossibleMoves(GameState state) {
		ArrayList<GameState> moves = new ArrayList<GameState>();
		for (Map.Entry<Integer, int[]> entry : state.getBoxList().getEntrySet()) {
			addMovesForBox(moves, state, entry.getValue());
		}
		return moves;
	}

	private void addMovesForBox(ArrayList<GameState> moves, GameState state, int[] box) {
		int bX = box[0], bY = box[1];

		GameState newState = moveBox(state, bX, bY, 0, 1); // push down
		if (newState != null && !visited.contains(newState)) {
			moves.add(newState);
		}

		newState = moveBox(state, bX, bY, 0, -1); // push up
		if (newState != null && !visited.contains(newState)) {
			moves.add(newState);
		}

		newState = moveBox(state, bX, bY, -1, 0); // push left
		if (newState != null && !visited.contains(newState)) {
			moves.add(newState);
		}

		newState = moveBox(state, bX, bY, 1, 0); // push right
		if (newState != null && !visited.contains(newState)) {
			moves.add(newState);
		}
	}

	private GameState moveBox(GameState state, int bX, int bY, int dX, int dY) {
		if (tryMove(state, bX, bY, dX, dY)) { // Does a push result in a valid, non-deadlock state?
			String path = findPath(state, bX, bY, dX, dY);
			if (path != null) {
				GameState newState = (GameState) state.clone();
				newState.setPath(path);
				newState.setPreviousState(state);
				makePush(newState, bX, bY, dX, dY);
				return newState;
			}
		}
		return null;
	}

	private void makePush(GameState state, int bX, int bY, int dX, int dY) {
		state.x = bX;
		state.y = bY;
		BoxList bl = state.getBoxList();
		bl.removeBox(bX, bY);
		bl.addBox(bX + dX, bY + dY);
		int u = tryMove(state, bX + dX, bY + dY, 0, -1) && AStar.findPath(state, state.x, state.y, bX + dX, bY + dY + 1) != null ? 1 : 0;
		int d = tryMove(state, bX + dX, bY + dY, 0, 1) && AStar.findPath(state, state.x, state.y, bX + dX, bY + dY - 1) != null ? 1 : 0;
		int l = tryMove(state, bX + dX, bY + dY, -1, 0) && AStar.findPath(state, state.x, state.y, bX + dX + 1, bY + dY) != null ? 1 : 0;
		int r = tryMove(state, bX + dX, bY + dY, 1, 0) && AStar.findPath(state, state.x, state.y, bX + dX - 1, bY + dY) != null ? 1 : 0;
		bl.removeBox(bX + dX, bY + dY);
		bl.addBox(bX + dX, bY + dY, u, r, d, l); // TODO
	}

	private String findPath(GameState state, int bX, int bY, int dX, int dY) {
		String path = AStar.findPath(state, state.x, state.y, bX - dX, bY - dY);
		if (path == null)
			return null;
		if (dY > 0)
			return path = "D " + path;
		if (dY < 0)
			return path = "U " + path;
		if (dX > 0)
			return path = "R " + path;
		if (dX < 0)
			return path = "L " + path;
		return null;
	}

	private boolean tryMove(GameState state, int bX, int bY, int dX, int dY) {
		if (isFreeSpace(state, bX + dX, bY + dY) && isFreeSpace(state, bX - dX, bY - dY)) {
			GameState gs = (GameState) state.clone(); // TODO
			BoxList bl = gs.getBoxList();
			bl.removeBox(bX, bY);
			bl.addBox(bX + dX, bY + dY);
			gs.x = bX;
			gs.y = bY;
			if (!isDeadlock(gs, bX + dX, bY + dY)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isFreeSpace(GameState state, int x, int y) {
		return !state.containsBox(x, y) && board[y][x] != WALL;
	}

	private boolean isStuck(GameState state, int x, int y) {
		if (state.containsBox(x, y) && ((isFreeSpace(state, x - 1, y) && isFreeSpace(state, x + 1, y)) || (isFreeSpace(state, x, y - 1) && isFreeSpace(state, x, y + 1)))) {
			return false;
		}
		return true;
	}

	private BufferedReader getBufferedReader() {
		if (TEST) {
			try {
				return new BufferedReader(new FileReader("data/all-slc"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return new BufferedReader(new InputStreamReader(System.in));
	}

	private List<String> readBoard(BufferedReader in) {
		List<String> board = new ArrayList<String>();
		String line = null;

		try {
			if (TEST) {
				// Get the level name
				line = in.readLine();
			}
			// We read until we get a new LEVEL or we have reached the end of
			// file
			while ((line = in.readLine()) != null && line.charAt(0) != ';') {
				board.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return board;
	}
}
