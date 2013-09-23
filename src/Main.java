import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
	public static final Boolean TEST = false;
	public static final Boolean RENDER = false;
	public static final char SPACE = ' ';
	public static final char WALL = '#';
	public static final char GOAL = '.';
	public static final char PLAYER = '@';
	public static final char PLAYER_ON_GOAL = '+';
	public static final char BOX = '$';
	public static final char BOX_ON_GOAL = '*';
	public static final int[] dx = {-1, 1, 0, 0};
	public static final int[] dy = {0, 0, -1, 1};
	public static final int[] bigdx = {-1, -1, -1, 0, 0, 1, 1, 1};
	public static final int[] bigdy = {-1, 0, 1, -1, 1, -1, 0, 1};
	
	private Set<GameState> visited;
	private RenderFrame renderer;

	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		visited = new HashSet<GameState>();
		if (RENDER) {
			renderer = new RenderFrame();
		}
		BufferedReader in = getBufferedReader();
		List<String> tmpBoard = readBoard(in);

		char[][] board = new char[tmpBoard.size()][];
		int x = 0, y = 0;

		for (int i = 0; i < tmpBoard.size(); i++) {
			board[i] = tmpBoard.get(i).toCharArray();
			int indx = tmpBoard.get(i).indexOf('@');
			if (indx != -1) {
				x = indx;
				y = i;
			}
		}
		GameState root = new GameState(board, ' ', null, x, y);

		System.out.println(findPath(root));
	}

	private String findPath(GameState root) {
		GameState goal = naiveSearch(root);
		return recreatePath(goal);

	}

	private String recreatePath(GameState goal) {
		StringBuilder sb = new StringBuilder();
		while (goal != null) {
//			printState(goal);
			sb.append(goal.getDirectionTo());
			goal = goal.getPreviousState();
		}
		return sb.reverse().toString();
	}

	private void printState(GameState gs) {
		for (int i = 0; i < gs.getBoard().length; i++) {
			for (int j = 0; j < gs.getBoard()[i].length; j++) {
				System.out.print(gs.getBoard()[i][j]);
			}
			System.out.println();
		}
	}

	private GameState naiveSearch(GameState current) {
		if (visited.contains(current)) {
			return null;
		} else if (isCompleted(current)) {
			return current;
		}
//		printState(current);
		visited.add(current);
		List<GameState> possibleStates = new ArrayList<GameState>();
		GameState tmp = (GameState) current.clone();
		for (int i = 0; i < 4; i++) {
			current = (GameState) tmp.clone();
			if (isOutOfBounds(current, current.getX() + dx[i], current.getY() + dy[i])) {
				continue;
			}
			char tile = current.getBoard()[current.getY() + dy[i]][current.getX() + dx[i]];
			if (tile != WALL) {
				if (tile == BOX || tile == BOX_ON_GOAL) {
					if (freeSpace(current.getBoard(), current.getX() + dx[i] * 2, current.getY() + dy[i] * 2)) {
						char direction = getDirection(dx[i], dy[i]);
						GameState nextState = new GameState(current.getBoard().clone(), direction, current, current.getX(), current.getY());
						if (!movePlayer(nextState, dx[i], dy[i])) {
							continue;
						}
						if (!isDeadlock(nextState, current.getX() + dx[i] * 2, current.getY() + dy[i] * 2)) {
							possibleStates.add(nextState);
						}
					}
				} else {
					char direction = getDirection(dx[i], dy[i]);
					GameState nextState = new GameState(current.getBoard().clone(), direction, current, current.getX(), current.getY());
					if (!movePlayer(nextState, dx[i], dy[i])) {
						continue;
					}
					possibleStates.add(nextState);
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
		if (isStuck(state.getBoard(), bx, by)) {
			return true;
		}
		for (int i = 0; i < 8; i++) {
			if (isStuck(state.getBoard(), bx + bigdx[i], by + bigdy[i])) {
				return true;
			}
		}
		return false;
	}

	private boolean movePlayer(GameState state, int dx, int dy) {
		int x = state.getX();
		int y = state.getY();
		if (isOutOfBounds(state, x + dx, y + dy)) {
			return false;
		}
		if (state.getBoard()[y + dy][x + dx] == BOX && freeSpace(state.getBoard(), x + dx * 2, y + dy * 2)) {
			if (state.getBoard()[y + dy * 2][x + dx * 2] == GOAL) {
				state.getBoard()[y + dy * 2][x + dx * 2] = BOX_ON_GOAL;
			} else {
				state.getBoard()[y + dy * 2][x + dx * 2] = BOX;
			}
		}
		if (state.getBoard()[y + dy][x + dx] == GOAL) {
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

	private boolean freeSpace(char[][] board, int x, int y) {
		char tile = board[y][x];
		return tile == SPACE || tile == GOAL || tile == PLAYER;
	}

	private boolean isStuck(char[][] board, int x, int y) {
		if (board[y][x] == BOX_ON_GOAL)
			return false;
		if (board[y][x] != BOX)
			return false;
		if ((freeSpace(board, x - 1, y) && freeSpace(board, x + 1, y)) || (freeSpace(board, x, y - 1) && freeSpace(board, x, y + 1))) {
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
