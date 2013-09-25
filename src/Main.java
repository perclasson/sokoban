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

	private int hit = 0;
	private int loops = 0;
	private Set<GameState> visited;
	private RenderFrame renderer;

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

		char[][] board = new char[tmpBoard.size()][];
		int x = 0, y = 0;

		for (int i = 0; i < tmpBoard.size(); i++) {
			board[i] = tmpBoard.get(i).toCharArray();
			int indx = tmpBoard.get(i).indexOf(PLAYER);
			indx = indx == -1 ? tmpBoard.get(i).indexOf(PLAYER_ON_GOAL) : indx;
			if (indx != -1) {
				x = indx;
				y = i;
			}
		}
		GameState root = new GameState(board, ' ', null, x, y);

		System.out.println(findPath(root));
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
			renderer.renderBoard(gs.getBoard());
		}
		else {
			for (int i = 0; i < gs.getBoard().length; i++) {
				for (int j = 0; j < gs.getBoard()[i].length; j++) {
					System.out.print(gs.getBoard()[i][j]);
				}
				System.out.print('\n');
			}
		}
	}

	private GameState search(GameState current) {
		// We have already visited this state, which means we can not find a
		// solution
		// Else if the the the game is completed, we return the current state
		loops++;
		if (visited.contains(current)) {
			hit++;
			return null;
		} else if (isCompleted(current)) {
			return current;
		}
		
		current.initHashBoard();
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
		if (state.getBoard()[by][bx] == BOX_ON_GOAL)
			return false;
		for (int i = 0; i < 9; i++) {
			if (!isStuck(state.getBoard(), bx + bigdx[i], by + bigdy[i])) {
				return false;
			}
		}
		return true;
	}

	private boolean isCompleted(GameState gs) {
		for (int i = 0; i < gs.getBoard().length; i++) {
			for (int j = 0; j < gs.getBoard()[i].length; j++) {
				if (gs.getBoard()[i][j] == GOAL || gs.getBoard()[i][j] == PLAYER_ON_GOAL) {
					return false;
				}
			}
		}
		return true;
	}

	private ArrayList<GameState> findPossibleMoves(GameState state) {
		ArrayList<GameState> moves = new ArrayList<GameState>();
		char[][] board = state.getBoard();

		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[y].length; x++) {
				if (board[y][x] == BOX || board[y][x] == BOX_ON_GOAL) {
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
		char[][] board = state.getBoard();
		
		// check above and below
		if (isFreeSpace(board[y - 1][x]) && isFreeSpace(board[y + 1][x])) {
			addMove(moves, state, x, y, 0, -1);
			addMove(moves, state, x, y, 0, +1);
		}
		// check left and right
		if (isFreeSpace(board[y][x - 1]) && isFreeSpace(board[y][x + 1])) {
			addMove(moves, state, x, y, -1, 0);
			addMove(moves, state, x, y, +1, 0);
		}
	}

	private void addMove(ArrayList<GameState> moves, GameState state, int fromX, int fromY, int dX, int dY) {
		GameState newState = (GameState) state.clone();
		makePush(state, newState, fromX, fromY, fromX + dX, fromY + dY);

		if (!isDeadlock(newState, fromX + dX, fromY + dY)) {
			String path = AStar.findPath(state.getBoard(), state.getX(), state.getY(), fromX - dX, fromY - dY);
			if (path != null) {
				if (dY > 0)
					path = "D " + path;
				else if (dY < 0)
					path = "U " + path;
				else if (dX > 0)
					path = "R " + path;
				else 
					path = "L " + path;
				
				newState.setX(fromX);
				newState.setY(fromY);
				newState.setPath(path);
				newState.setPreviousState(state);
				moves.add(newState);
				state.addToHashBoard(fromX + dY, fromY + dY);
			}
		}
	}

	/**
	 * pushes a box from fromX, fromY to toX, toY and stores result in
	 * afterPush. Does NOT check that the player reach the position requred to
	 * make the push, only determines if box and player is on goal or not.
	 */
	private void makePush(GameState state, GameState newState, int fromX, int fromY, int toX, int toY) {
		char[][] beforePush = state.getBoard();
		char[][] afterPush = newState.getBoard();
		
		// Remove player
		int playerY = state.getY();
		int playerX = state.getX();
		
		if (beforePush[playerY][playerX] == PLAYER) {
			afterPush[playerY][playerX] = SPACE;
		}
		else if (beforePush[playerY][playerX] == PLAYER_ON_GOAL) {
			afterPush[playerY][playerX] = GOAL;
		}
		
		if (beforePush[toY][toX] == GOAL || beforePush[toY][toX] == PLAYER_ON_GOAL) {
			afterPush[toY][toX] = BOX_ON_GOAL;
		} else {
			afterPush[toY][toX] = BOX;
		}

		if (beforePush[fromY][fromX] == BOX_ON_GOAL) {
			afterPush[fromY][fromX] = PLAYER_ON_GOAL;
		} else {
			afterPush[fromY][fromX] = PLAYER;
		}
		

	}

	public static boolean isFreeSpace(char node) {
		return node == SPACE || node == GOAL || node == PLAYER || node == PLAYER_ON_GOAL;
	}

	private boolean freeSpace(char[][] board, int x, int y) {
		char tile = board[y][x];
		return tile == SPACE || tile == GOAL || tile == PLAYER || tile == PLAYER_ON_GOAL;
	}

	private boolean isStuck(char[][] board, int x, int y) {
		if ((board[y][x] == BOX || board[y][x] == BOX_ON_GOAL) && ((freeSpace(board, x - 1, y) && freeSpace(board, x + 1, y)) || (freeSpace(board, x, y - 1) && freeSpace(board, x, y + 1)))) {
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
