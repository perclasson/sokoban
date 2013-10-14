import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

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

	private Queue<GameState> queue;
	private Set<GameState> visited;
	private RenderFrame renderer;
	private List<Coordinate> goalList;
	private Heuristic heuristic;
	private ZobristHasher hasher;
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
		if (RENDER) {
			renderer = new RenderFrame();
			renderer.pack();
		}
		BufferedReader in = getBufferedReader();
		List<String> tmpBoard = readBoard(in);
		GameState root  = generateRoot(tmpBoard);
		long b = System.currentTimeMillis();
		System.out.println(findPath(root));
		long a = System.currentTimeMillis();
		
		System.out.println(a - b);
	}
	
	private GameState generateRoot(List<String> tmpBoard) {
		board = new char[tmpBoard.size()][];
		List<Coordinate> boxes = new ArrayList<Coordinate>();
		int playerX = 0, playerY = 0;
		for (int y = 0; y < tmpBoard.size(); y++) {
			board[y] = tmpBoard.get(y).toCharArray();
			for (int x = 0; x < tmpBoard.get(y).toCharArray().length; x++) {
				switch (tmpBoard.get(y).charAt(x)) {
				case BOX:
					board[y][x] = SPACE;
					boxes.add(new Coordinate(x, y));
					break;
				case BOX_ON_GOAL:
					board[y][x] = GOAL;
					boxes.add(new Coordinate(x, y));
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

						} else if (board[y][x] != WALL || (board[y + 1][x] != SPACE && board[y + 1][x] != DEADLOCK)) {
							upperLeftX = -1;
						}
					} else if (downLeftX > 0) {
						if (board[y][x] == WALL && board[y + 1][x] == WALL) {
							for (int xi = downLeftX; xi < x; xi++) {
								board[y][xi] = DEADLOCK;
							}
							downLeftX = -1;

						} else if ((board[y][x] != SPACE && board[y][x] != DEADLOCK) || board[y + 1][x] != WALL) {
							downLeftX = -1;
						}
					} else if (board[y][x] == WALL && board[y + 1][x] == WALL && board[y][x + 1] == WALL && (board[y + 1][x + 1] == SPACE || board[y + 1][x + 1] == DEADLOCK)) {
						// We found a corner
						board[y + 1][x + 1] = DEADLOCK;
						upperLeftX = x + 1;
					} else if (board[y][x] == WALL && board[y + 1][x] == WALL && (board[y][x + 1] == SPACE || board[y][x + 1] == DEADLOCK) && board[y + 1][x + 1] == WALL) {
						// We found a corner
						board[y][x + 1] = DEADLOCK;
						downLeftX = x + 1;
					} else if ((board[y][x] == SPACE || board[y][x] == DEADLOCK) && board[y+1][x] == WALL && board[y][x+1] == WALL && board[y+1][x+1] == WALL) {
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

						} else if (board[y][x] != WALL || (board[y][x + 1] != SPACE && board[y][x + 1] != DEADLOCK)) {
							upperLeftY = -1;
						}
					} else if (upperRightY > 0) {
						if (board[y][x] == WALL && board[y][x + 1] == WALL) {
							for (int yi = upperRightY; yi < y; yi++) {
								board[yi][x] = DEADLOCK;
							}
							upperRightY = -1;

						} else if ((board[y][x] != SPACE && board[y][x] != DEADLOCK) || board[y][x + 1] != WALL) {
							upperRightY = -1;
						}
					} else if (board[y][x] == WALL && board[y + 1][x] == WALL && board[y][x + 1] == WALL && (board[y + 1][x + 1] == SPACE || board[y + 1][x + 1] == DEADLOCK)) {
						// We found a corner
						board[y + 1][x + 1] = DEADLOCK;
						upperLeftY = y + 1;
					} else if (board[y][x] == WALL && (board[y + 1][x] == SPACE || board[y + 1][x] == DEADLOCK) && board[y][x + 1] == WALL && board[y + 1][x + 1] == WALL) {
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
		hasher = new ZobristHasher(board);
		heuristic = new Heuristic(board);
		GameState root = new GameState(boxes, new Coordinate(playerX, playerY), heuristic.getValue(boxes));
		root.setHashCode(hasher.hash(boxes, new Coordinate(playerX, playerY)));
		return root;
	}

	private String findPath(GameState root) {
		GameState goal = search(root, START_DEPTH);
		if(goal != null) {
			return recreatePath(goal).trim();
		}
		String s = null;
		while (goal != null) {
			s = findPath(goal);
			goal = queue.poll();
			if (s != null) {
				return s;
			}
		}
		return null;
	}

	private String recreatePath(GameState goal) {
		return new StringBuilder(goal.getPath()).reverse().toString();
	}
	private void printState(GameState gs) {
		if (RENDER) {
			renderer.renderState(board, gs);
		} else {
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board[i].length; j++) {
					if (gs.containsBox(j, i)) {
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
		System.out.println("visited: " + visited.size());
		List<GameState> possibleStates = findPossibleMoves(current);
		System.out.println("Possible states: " + possibleStates.size());
		visited.add(current);
		if (depth <= 0) {
			for(GameState gs : possibleStates) {
				if(!visited.contains(gs)) {
					queue.add(gs);
				}
			}
			return null;
		}
		//Collections.sort(possibleStates, heuristic);
		for (GameState state : possibleStates) {
			GameState result = search(state, depth-1);
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

	private List<GameState> findPossibleMoves(GameState state) {
		List<GameState> moves = new ArrayList<GameState>();
		Coordinate[] boxes = new Coordinate[state.getBoxes().size()];
		state.getBoxes().toArray(boxes);
		for(int i = 0; i < boxes.length; i++) {
			//System.out.println("box: " + boxes[i].x + " " + boxes[i].y);
			addMovesForBox(moves, state, boxes[i]);
		}
		return moves;
	}

	private void addMovesForBox(List<GameState> moves, GameState state, Coordinate box) {
		GameState newState = null;
		
		for(int i = 0 ; i < dx.length ; i++) {
			newState = moveBox(state, box.x, box.y, dx[i], dy[i]);
			if (newState != null && !visited.contains(newState)) {
				moves.add(newState);
			}
		}
	}

	private GameState moveBox(GameState state, int bX, int bY, int dX, int dY) {
		if (tryMove(state, bX, bY, dX, dY)) { // Does a push result in a valid, non-deadlock state?
			String path = getPath(state, bX, bY, dX, dY);
			if (path != null) {
				GameState newState = (GameState) state.clone();
				newState.setPath(path + state.getPath());
				makePush(newState, bX, bY, dX, dY);
				return newState;
			}
		}
		return null;
	}

	private void makePush(GameState state, int bX, int bY, int dX, int dY) {
		int newCode = hasher.movePlayer(state.hashCode, state.getPlayer().x, state.getPlayer().y, bX,bY);
		state.getPlayer().x = bX;
		state.getPlayer().y = bY;
		newCode = hasher.moveBox(newCode, bX, bY, bX+dX,bY+dY);
		//System.out.println("Before: " + state.getBoxes().size());
		//System.out.println("bX: " + bX + ", bY: " + bY);
		state.removeBox(bX, bY);
		//System.out.println("After: " + state.getBoxes().size());

		state.addBox(bX+dX, bY+dY);
		state.setHashCode(newCode);
		state.setHeuristic(heuristic.getValue(state.getBoxes()));
		/*
		int u = tryMove(state, bX + dX, bY + dY, 0, -1) && AStar.findPath(state, state.x, state.y, bX + dX, bY + dY + 1) != null ? 1 : 0;
		int d = tryMove(state, bX + dX, bY + dY, 0, 1) && AStar.findPath(state, state.x, state.y, bX + dX, bY + dY - 1) != null ? 1 : 0;
		int l = tryMove(state, bX + dX, bY + dY, -1, 0) && AStar.findPath(state, state.x, state.y, bX + dX + 1, bY + dY) != null ? 1 : 0;
		int r = tryMove(state, bX + dX, bY + dY, 1, 0) && AStar.findPath(state, state.x, state.y, bX + dX - 1, bY + dY) != null ? 1 : 0;
		
		state.removeBox(bX + dX, bY + dY);
		state.addBox(bX + dX, bY + dY); // TODO behövs det här?
		*/
	}

	private String getPath(GameState state, int bX, int bY, int dX, int dY) {
		String path = AStar.findPath(state, state.getPlayer().x, state.getPlayer().y, bX - dX, bY - dY);
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

	private boolean tryMove(GameState state, int bX, int bY, int dX, int dY) {
		if (isFreeSpace(state, bX + dX, bY + dY) && isFreeSpace(state, bX - dX, bY - dY)) {
			GameState gs = (GameState) state.clone(); // TODO
			gs.removeBox(bX, bY);
			gs.addBox(bX + dX, bY + dY);
			gs.getPlayer().x = bX;
			gs.getPlayer().y = bY;
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
