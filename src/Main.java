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
	private static final Boolean TEST = false;
	private static final char SPACE = ' ';
	private static final char WALL = '#';
	private static final char GOAL = '.';
	private static final char PLAYER = '@';
	private static final char PLAYER_ON_GOAL = '+';
	private static final char BOX = '$';
	private static final char BOX_ON_GOAL = '*';
	private static final int[] dx = {-1, 1, 0, 0};
	private static final int[] dy = {0, 0, -1, 1};
	private static final int[] bigdx = {-1, -1, -1, 0, 0, 1, 1, 1};
	private static final int[] bigdy = {-1, 0, 1, -1, 1, -1, 0, 1};
	
	private char[][] board;
	private Set<GameState> visited;
	
	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		visited = new HashSet<GameState>();
		BufferedReader in = getBufferedReader();
		List<String> tmpBoard = readBoard(in);

		board = new char[tmpBoard.size()][];
		int x = 0, y = 0;

		for (int i = 0; i < tmpBoard.size(); i++) {
			board[i] = tmpBoard.get(i).toCharArray();
			int indx = tmpBoard.get(i).indexOf('@');
			if(indx != -1) {
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
		while(goal != null) {
			sb.append(goal.getDirectionTo());
			goal = goal.getPreviousState();
		}
		return sb.reverse().toString();
	}

	private GameState naiveSearch(GameState current) {
		if(visited.contains(current)) {
			return null;
		} else if(isCompleted(current)) {
			return current;
		} 
		visited.add(current);
		List<GameState> possibleStates = new ArrayList<GameState>();
		for(int i = 0 ; i < 4 ; i++) {
			char tile = board[current.getX()+dx[i]][current.getY()+dy[i]];
			if(tile != WALL) {
				if(tile == BOX || tile == BOX_ON_GOAL) {
					if(freeSpace(current.getBoard(), current.getX()+dx[i]*2, current.getY()+dy[i]*2)) {
						char direction = getDirection(dx[i], dy[i]);
						GameState nextState = new GameState(board, direction, current, current.getX()+dx[i], current.getY()+dy[i]);
						movePlayer(nextState, dx[i], dy[i]);
						if(!isDeadlock(nextState, current.getX()+dx[i]*2, current.getY()+dy[i]*2)) {
							possibleStates.add(nextState);
						}
					}
				} else {
					char direction = getDirection(dx[i], dy[i]);
					GameState nextState = new GameState(board, direction, current, current.getX()+dx[i], current.getY()+dy[i]);
					movePlayer(nextState, dx[i], dy[i]);
					possibleStates.add(nextState);
				}
			}
		}
		
		for(GameState state : possibleStates) {
			GameState result = naiveSearch(state);
			if(result != null)
				return result;
		}
		
		return null;
	}

	private boolean isDeadlock(GameState state, int bx, int by) {
		if(isStuck(state.getBoard(), bx, by)) {
			return true;
		}
		for(int i = 0 ; i < 8 ; i++) {
			if(isStuck(state.getBoard(), bx+bigdx[i], by+bigdy[i])) {
				return true;
			}
		}
		return false;
	}

	private void movePlayer(GameState state, int dx, int dy) {
		int x = state.getX();
		int y = state.getY();
		char tile = state.getBoard()[x+dx][y+dy];
		if(tile == BOX){
			if(state.getBoard()[x+dx*2][y+dy*2] == GOAL) {
				state.getBoard()[x+dx*2][y+dy*2] = BOX_ON_GOAL;
			} else {
				state.getBoard()[x+dx*2][y+dy*2] = BOX;
			}
		}
		if(state.getBoard()[x+dx][y+dy] == GOAL) {
			state.getBoard()[x+dx][y+dy] = PLAYER_ON_GOAL;
		} else {
			state.getBoard()[x+dx][y+dy] = PLAYER;
		}
		if(state.getBoard()[x][y] == PLAYER_ON_GOAL) {
			state.getBoard()[x][y] = GOAL;
		} else {
			state.getBoard()[x][y] = SPACE;
		}
	}

	private char getDirection(int x, int y) {
		if(x == -1) {
			return 'L';
		} else if(x == 1) {
			return 'R';
		} else if(y == 1) {
			return 'D';
		} else if(y == -1){
			return 'U'; 
		} else {
			throw new IllegalArgumentException("Invalid direction"); 
		}
	}

	private boolean isCompleted(GameState current) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean freeSpace(char[][] board, int x, int y) {
		return board[x][y] != SPACE && board[x][y] != GOAL && board[x][y] != PLAYER;
	}

	private boolean isStuck(char[][] board, int x, int y) {
		if ((freeSpace(board, x - 1, y) && freeSpace(board, x + 1, y))
				|| (freeSpace(board, x, y - 1) && freeSpace(board, x, y + 1))) {
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
			// We read until we get a new LEVEL or we have reached the end of file
			while ((line = in.readLine()) != null && line.charAt(0) != ';') {
				board.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return board;
	}

}
