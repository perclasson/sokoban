import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

		GameState root  = generateRoot(tmpBoard);

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
			renderer.renderState(board,gs);
		}
		else {
			
		}
	}

	private GameState search(GameState current) {
		if (visited.contains(current)) {
			return null;
		} else if (isCompleted(current)) {
			return current;
		}
		System.out.println(visited.size());
		List<GameState> possibleStates = findPossibleMoves(current);
		visited.add(current);

		for (GameState state : possibleStates) {
			GameState result = search(state);
			if (result != null)
				return result;
		}
		return null;
	}

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

		for (int y = 0; y < board.length; y++) { // TODO loopa över keyset av lådor istället
			for (int x = 0; x < board[y].length; x++) {
				if (state.containsBox(x, y)) {
					addValidMovesForBox(moves, state, x, y);
				}
			}
		}
		return moves;
	}

	/**
	 * Adds all valid moves for box represented by x and y. A valid move does

	 * not cause a deadlock and can be performed by the player.
	 */
	private void addValidMovesForBox(ArrayList<GameState> moves, GameState state, int x, int y) {

		// check above and below
		if (isFreeSpace(state, x, y - 1) && isFreeSpace(state, x, y + 1)) {
			addMove(moves, state, x, y, 0, -1);
			addMove(moves, state, x, y, 0, +1);
		}
		// check left and right
		if (isFreeSpace(state, x - 1, y) && isFreeSpace(state, x + 1, y)) {
			addMove(moves, state, x, y, -1, 0);
			addMove(moves, state, x, y, +1, 0);
		}
	}

	private void addMove(ArrayList<GameState> moves, GameState state, int fromX, int fromY, int dX, int dY) {
		GameState newState = (GameState) state.clone();
		makePush(state, newState, fromX, fromY, fromX + dX, fromY + dY);

		if (!isDeadlock(newState, fromX + dX, fromY + dY)) {
			String path = AStar.findPath(state, state.x, state.y, fromX - dX, fromY - dY);
			if (path != null) {
				if (dY > 0)
					path = "D " + path;
				else if (dY < 0)
					path = "U " + path;
				else if (dX > 0)
					path = "R " + path;
				else 
					path = "L " + path;

				newState.y = fromX;
				newState.y = fromY;
				newState.setPath(path);
				newState.setPreviousState(state);
				moves.add(newState);
			}
		}
	}

	/**
	 * pushes a box from fromX, fromY to toX, toY and stores result in
	 * afterPush. Does NOT check that the player reach the position requred to
	 * make the push, only determines if box and player is on goal or not.
	 */
	private void makePush(GameState state, GameState newState, int fromX, int fromY, int toX, int toY) {
		BoxList bl = newState.getBoxList();
		bl.removeBox(fromX, fromY);
		bl.addBox(toX, toY);
	}

	public static boolean isFreeSpace(GameState state, int x, int y) {
		return !state.containsBox(x, y) && board[y][x] != WALL;
	}

	private boolean isStuck(GameState state, int x, int y) {
		if (state.containsBox(x, y) && ((isFreeSpace(state, x - 1, y) && 
				isFreeSpace(state, x + 1, y)) || (isFreeSpace(state, x, y - 1) && isFreeSpace(state, x, y + 1)))) {
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
