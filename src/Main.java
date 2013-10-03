import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
	public static final char DEADLOCK = 'D';
	public static final int[] dx = { -1, 1, 0, 0 };
	public static final int[] dy = { 0, 0, -1, 1 };
	public static final int[] bigdx = { -1, -1, -1, 0, 0, 1, 1, 1, 0 };
	public static final int[] bigdy = { -1, 0, 1, -1, 1, -1, 0, 1, 0 };
	private static final int START_DEPTH = 30;

	private Set<GameState> visited;
	private RenderFrame renderer;
	private List<Coordinate> goalList;
	private Heuristic heuristic;
<<<<<<< HEAD
	private Queue<GameState> queue; 
	private SortedSet<GameState> nextStates;
=======
>>>>>>> 64dd3410572370275ed9bd6aa986aaa2d7790f78
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
		queue = new LinkedList<GameState>();
		nextStates = new TreeSet<GameState>();
		if (RENDER) {
			renderer = new RenderFrame();
			renderer.pack();
		}
		BufferedReader in = getBufferedReader();
		List<String> tmpBoard = readBoard(in);
<<<<<<< HEAD
		GameState root = generateRoot(tmpBoard);
		heuristic = new Heuristic(board);
		root.setHeuristic(heuristic);
=======

		GameState root  = generateRoot(tmpBoard);
		heuristic = new Heuristic(board);
>>>>>>> 64dd3410572370275ed9bd6aa986aaa2d7790f78
		System.out.println(findPath(root));
		System.out.println(visited.size());
	}
<<<<<<< HEAD

=======
	
>>>>>>> 64dd3410572370275ed9bd6aa986aaa2d7790f78
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

		int upperLeftX = -1, downLeftX = -1, maxWidth = 0;

		// Mark horizontal deadlocks
		for (int y = 0; y < board.length; y++) {
			maxWidth = Math.max(board[y].length, maxWidth);
			for (int x = 0; x < board[y].length; x++) {
				try {
					if (upperLeftX > 0) {
						if (board[y][x] == WALL && board[y + 1][x] == WALL) {
							for (int xi = upperLeftX; xi < x; xi++) {
								board[y + 1][xi] = DEADLOCK;
							}
							upperLeftX = -1;

						} else if (board[y][x] != WALL
								|| (board[y + 1][x] != SPACE && board[y + 1][x] != DEADLOCK)) {
							upperLeftX = -1;
						}
					} else if (downLeftX > 0) {
						if (board[y][x] == WALL && board[y + 1][x] == WALL) {
							for (int xi = downLeftX; xi < x; xi++) {
								board[y][xi] = DEADLOCK;
							}
							downLeftX = -1;

						} else if ((board[y][x] != SPACE && board[y][x] != DEADLOCK)
								|| board[y + 1][x] != WALL) {
							downLeftX = -1;
						}
					} else if (board[y][x] == WALL
							&& board[y + 1][x] == WALL
							&& board[y][x + 1] == WALL
							&& (board[y + 1][x + 1] == SPACE || board[y + 1][x + 1] == DEADLOCK)) {
						// We found a corner
						board[y + 1][x + 1] = DEADLOCK;
						upperLeftX = x + 1;
					} else if (board[y][x] == WALL
							&& board[y + 1][x] == WALL
							&& (board[y][x + 1] == SPACE || board[y][x + 1] == DEADLOCK)
							&& board[y + 1][x + 1] == WALL) {
						// We found a corner
						board[y][x + 1] = DEADLOCK;
						downLeftX = x + 1;
					} else if ((board[y][x] == SPACE || board[y][x] == DEADLOCK)
							&& board[y + 1][x] == WALL
							&& board[y][x + 1] == WALL
							&& board[y + 1][x + 1] == WALL) {
						// We found a corner
						board[y][x] = DEADLOCK;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					continue;
				}
			}
			upperLeftX = -1;
			downLeftX = -1;
		}

		int upperLeftY = -1, upperRightY = -1;

		// Mark vertical deadlocks
		for (int x = 0; x < maxWidth; x++) {
			for (int y = 0; y < board.length; y++) {
				try {
					if (upperLeftY > 0) {
						if (board[y][x] == WALL && board[y][x + 1] == WALL) {
							for (int yi = upperLeftY; yi < y; yi++) {
								board[yi][x + 1] = DEADLOCK;
							}
							upperLeftY = -1;

						} else if (board[y][x] != WALL
								|| (board[y][x + 1] != SPACE && board[y][x + 1] != DEADLOCK)) {
							upperLeftY = -1;
						}
					} else if (upperRightY > 0) {
						if (board[y][x] == WALL && board[y][x + 1] == WALL) {
							for (int yi = upperRightY; yi < y; yi++) {
								board[yi][x] = DEADLOCK;
							}
							upperRightY = -1;

						} else if ((board[y][x] != SPACE && board[y][x] != DEADLOCK)
								|| board[y][x + 1] != WALL) {
							upperRightY = -1;
						}
					} else if (board[y][x] == WALL
							&& board[y + 1][x] == WALL
							&& board[y][x + 1] == WALL
							&& (board[y + 1][x + 1] == SPACE || board[y + 1][x + 1] == DEADLOCK)) {
						// We found a corner
						board[y + 1][x + 1] = DEADLOCK;
						upperLeftY = y + 1;
					} else if (board[y][x] == WALL
							&& (board[y + 1][x] == SPACE || board[y + 1][x] == DEADLOCK)
							&& board[y][x + 1] == WALL
							&& board[y + 1][x + 1] == WALL) {
						// We found a corner
						board[y + 1][x] = DEADLOCK;
						upperRightY = y + 1;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					continue;
				}
			}
			upperLeftY = -1;
			upperRightY = -1;
		}

		return new GameState(bl, playerX, playerY, heuristic);
	}

	private String findPath(GameState root) {
		GameState goal = search(root, START_DEPTH);

		if(goal != null) {
			return recreatePath(goal).trim();
		}
		String s = null;
		goal = queue.poll();
		while (goal != null) {
			s = findPath(goal);
			if (s != null) {
				return recreatePath(goal).trim();
			}
			goal = queue.poll();
		}
		return null;
	}

	private String recreatePath(GameState goal) {
		return new StringBuilder(goal.getPath()).reverse().toString();
	}
<<<<<<< HEAD

=======
>>>>>>> 64dd3410572370275ed9bd6aa986aaa2d7790f78
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


	private GameState search(GameState current, int depth) {
		if (isCompleted(current)) {
			return current;
		}
		List<GameState> possibleStates = findPossibleMoves(current);
		visited.add(current);

		if (depth <= 0) {
			for (GameState gs : possibleStates) {
				if (!visited.contains(gs)) {
					queue.add(gs);
				}
			}
			return null;
		}
<<<<<<< HEAD
		nextStates.addAll(possibleStates);
		GameState result = null;
		for (GameState state : new TreeSet<GameState>(nextStates)) {
			if (!visited.contains(state)) {
				result = search(state, depth - 1);
			}
=======
//		Collections.sort(possibleStates, heuristic);
		for (GameState state : possibleStates) {
			GameState result = search(state, depth-1);
>>>>>>> 64dd3410572370275ed9bd6aa986aaa2d7790f78
			if (result != null)
				return result;
		}
		return null;
	}

	private boolean isDeadlock(GameState state, int bx, int by) {
		if (board[by][bx] == GOAL)
			return false;
		else if (board[by][bx] == DEADLOCK)
			return true;
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

<<<<<<< HEAD
	private ArrayList<GameState> findPossibleMoves(GameState state) {
		ArrayList<GameState> moves = new ArrayList<GameState>();

		for (int y = 0; y < board.length; y++) { // TODO loopa över keyset av
													// lådor istället
			for (int x = 0; x < board[y].length; x++) {
				if (state.containsBox(x, y)) {
					addValidMovesForBox(moves, state, x, y);
				}
			}
=======
	private List<GameState> findPossibleMoves(GameState state) {
		List<GameState> moves = new ArrayList<GameState>();
		for (Map.Entry<Integer, int[]> entry : state.getBoxList().getEntrySet()) {
			addMovesForBox(moves, state, entry.getValue());
>>>>>>> 64dd3410572370275ed9bd6aa986aaa2d7790f78
		}
		return moves;
	}

<<<<<<< HEAD
	/**
	 * Adds all valid moves for box represented by x and y. A valid move does
	 * 
	 * not cause a deadlock and can be performed by the player.
	 */
	private void addValidMovesForBox(ArrayList<GameState> moves,
			GameState state, int x, int y) {
=======
	private void addMovesForBox(List<GameState> moves, GameState state, int[] box) {
		int bX = box[0], bY = box[1];
>>>>>>> 64dd3410572370275ed9bd6aa986aaa2d7790f78

		GameState newState = null;
		
		for(int i = 0 ; i < dx.length ; i++) {
			newState = moveBox(state, bX, bY, dx[i], dy[i]);
			if (newState != null && !visited.contains(newState)) {
				moves.add(newState);
			}
		}
	}

<<<<<<< HEAD
	private void addMove(ArrayList<GameState> moves, GameState state,
			int fromX, int fromY, int dX, int dY) {
		GameState newState = (GameState) state.clone();
		makePush(state, newState, fromX, fromY, fromX + dX, fromY + dY);

		if (!isDeadlock(newState, fromX + dX, fromY + dY)) {
			String path = AStar.findPath(state, state.x, state.y, fromX - dX,
					fromY - dY);
			if (path != null) {
				if (dY > 0)
					path = "D " + path;
				else if (dY < 0)
					path = "U " + path;
				else if (dX > 0)
					path = "R " + path;
				else
					path = "L " + path;

				newState.x = fromX;
				newState.y = fromY;
=======
	private GameState moveBox(GameState state, int bX, int bY, int dX, int dY) {
		if (tryMove(state, bX, bY, dX, dY)) { // Does a push result in a valid, non-deadlock state?
			String path = getPath(state, bX, bY, dX, dY);
			if (path != null) {
				GameState newState = (GameState) state.clone();
>>>>>>> 64dd3410572370275ed9bd6aa986aaa2d7790f78
				newState.setPath(path + state.getPath());
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

	private String getPath(GameState state, int bX, int bY, int dX, int dY) {
		String path = AStar.findPath(state, state.x, state.y, bX - dX, bY - dY);
		if (path == null || path.equals(""))
			return null;
		if(path.equals("Q")) {
			path = "";
		}
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

<<<<<<< HEAD
	/**
	 * pushes a box from fromX, fromY to toX, toY and stores result in
	 * afterPush. Does NOT check that the player reach the position requred to
	 * make the push, only determines if box and player is on goal or not.
	 */
	private void makePush(GameState state, GameState newState, int fromX,
			int fromY, int toX, int toY) {
		BoxList bl = newState.getBoxList();
		bl.removeBox(fromX, fromY);
		bl.addBox(toX, toY);
=======
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
>>>>>>> 64dd3410572370275ed9bd6aa986aaa2d7790f78
	}

	public static boolean isFreeSpace(GameState state, int x, int y) {
		return !state.containsBox(x, y) && board[y][x] != WALL;
	}

	private boolean isStuck(GameState state, int x, int y) {
<<<<<<< HEAD
		if (state.containsBox(x, y)
				&& ((isFreeSpace(state, x - 1, y) && isFreeSpace(state, x + 1,
						y)) || (isFreeSpace(state, x, y - 1) && isFreeSpace(
						state, x, y + 1)))) {
=======
		if (state.containsBox(x, y) && ((isFreeSpace(state, x - 1, y) && isFreeSpace(state, x + 1, y)) || (isFreeSpace(state, x, y - 1) && isFreeSpace(state, x, y + 1)))) {
>>>>>>> 64dd3410572370275ed9bd6aa986aaa2d7790f78
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
