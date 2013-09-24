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
	private long before;

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

		System.out.println(lessNaiveFindPath(root));
	}

	private String findPath(GameState root) {
		before = System.currentTimeMillis();
		GameState goal = naiveSearch(root);
		if(RENDER)
			System.out.println("Took "+(System.currentTimeMillis()-before) + " ms");
		return recreatePath(goal);

	}

	private String lessNaiveFindPath(GameState root) {
		GameState goal = lessNaiveSearch(root);
		return lessNaiveRecreatePath(goal);
	}

	private String lessNaiveRecreatePath(GameState goal) {
		GameState current = goal;
		StringBuilder sb = new StringBuilder();
		Stack<GameState> stack = new Stack<GameState>();

		while (current != null) {
			if (RENDER)
				stack.add(goal);
			sb.append(current.getPath());
			current = current.getPreviousState();
		}

		if (RENDER) {
			while (!stack.empty()) {
				printState(stack.pop());
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return sb.reverse().toString();
	}

	private String recreatePath(GameState goal) {
		StringBuilder sb = new StringBuilder();
		Stack<GameState> stack = new Stack<GameState>();
		while (goal != null) {
			if (RENDER)
				stack.add(goal);
			sb.append(goal.getDirectionTo());
			goal = goal.getPreviousState();
		}
		if (RENDER) {
			while (!stack.empty()) {
				printState(stack.pop());
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return sb.reverse().toString().trim();
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
			System.out.print('\n');
		}
	}

	private GameState lessNaiveSearch(GameState current) {
		// We have already visited this state, which means we can not find a
		// solution
		// Else if the the the game is completed, we return the current state
		if (visited.contains(current)) {
			return null;
		} else if (isCompleted(current)) {
			return current;
		}
		
		printState(current);
		
		visited.add(current);
		List<GameState> possibleStates = findPossibleMoves(current);

		for (GameState state : possibleStates) {
			GameState result = lessNaiveSearch(state);
			if (result != null)
				return result;
		}

		return null;
	}

	private GameState naiveSearch(GameState current) {
		if (isCompleted(current)) {
			return current;
		}
		visited.add(current);
		List<GameState> possibleStates = new ArrayList<GameState>();
		for (int i = 0; i < 4; i++) {
			if (isOutOfBounds(current, current.getX() + dx[i], current.getY() + dy[i])) {
				continue;
			}
			char tile = current.getBoard()[current.getY() + dy[i]][current.getX() + dx[i]];
			if (tile != WALL) {
				if (tile == BOX || tile == BOX_ON_GOAL) {
					if (freeSpace(current.getBoard(), current.getX() + dx[i] * 2, current.getY() + dy[i] * 2)) {
						char direction = getDirection(dx[i], dy[i]);
						GameState nextState = new GameState(GameState.copyArray(current.getBoard()), direction, current, current.getX(), current.getY());
						if (!movePlayer(nextState, dx[i], dy[i])) {
							continue;
						}
						if (!isDeadlock(nextState, current.getX() + dx[i] * 2, current.getY() + dy[i] * 2) && !visited.contains(nextState)) {
							possibleStates.add(nextState);
						}
					}
				} else {
					char direction = getDirection(dx[i], dy[i]);
					GameState nextState = new GameState(GameState.copyArray(current.getBoard()), direction, current, current.getX(), current.getY());

					if (!movePlayer(nextState, dx[i], dy[i])) {
						continue;
					}
					if (!visited.contains(nextState)) {
						possibleStates.add(nextState);
					}
				}
			}
		}
		for (GameState state : possibleStates) {
			GameState result = naiveSearch(state);
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

	private boolean movePlayer(GameState state, int dx, int dy) {
		int x = state.getX();
		int y = state.getY();
		if (isOutOfBounds(state, x + dx, y + dy)) {
			return false;
		}
		if ((state.getBoard()[y + dy][x + dx] == BOX || state.getBoard()[y + dy][x + dx] == BOX_ON_GOAL) && freeSpace(state.getBoard(), x + dx * 2, y + dy * 2)) {
			if (state.getBoard()[y + dy * 2][x + dx * 2] == GOAL) {
				state.getBoard()[y + dy * 2][x + dx * 2] = BOX_ON_GOAL;
			} else {
				state.getBoard()[y + dy * 2][x + dx * 2] = BOX;
			}
		}
		if (state.getBoard()[y + dy][x + dx] == GOAL || state.getBoard()[y + dy][x + dx] == BOX_ON_GOAL) {
			state.getBoard()[y + dy][x + dx] = PLAYER_ON_GOAL;
		} else {
			state.getBoard()[y + dy][x + dx] = PLAYER;
		}
		if (state.getBoard()[y][x] == PLAYER_ON_GOAL) {
			state.getBoard()[y][x] = GOAL;
		} else {
			state.getBoard()[y][x] = SPACE;
		}
		state.setX(x + dx);
		state.setY(y + dy);
		return true;
	}

	private boolean isOutOfBounds(GameState state, int x, int y) {
		if (y >= state.getBoard().length || y < 0) {
			return true;
		}

		if (x >= state.getBoard()[y].length || x < 0) {
			return true;
		}
		return false;
	}

	private char getDirection(int x, int y) {
		if (x == -1) {
			return 'L';
		} else if (x == 1) {
			return 'R';
		} else if (y == 1) {
			return 'D';
		} else if (y == -1) {
			return 'U';
		} else {
			throw new IllegalArgumentException("Invalid direction");
		}
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
		char[][] beforePush = state.getBoard();
		char[][] afterPush = newState.getBoard();
		
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
		
		// Remove player
		int playerY = state.getY();
		int playerX = state.getX();
		
		if (beforePush[playerY][playerX] == PLAYER) {
			afterPush[playerY][playerX] = SPACE;
		}
		else if (beforePush[playerY][playerX] == PLAYER_ON_GOAL) {
			afterPush[playerY][playerX] = GOAL;
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
