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
	private static final Boolean TEST = true;
	private static final char SPACE = ' ';
	private static final char WALL = '#';
	private static final char GOAL = '.';
	private static final char PLAYER = '@';
	private static final char PLAYER_ON_GOAL = '+';
	private static final char BOX = '$';
	private static final char BOX_ON_GOAL = '*';
	
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

		for (int i = 0; i < tmpBoard.size(); i++) {
			board[i] = tmpBoard.get(i).toCharArray();
		}
		
		System.out.println(findPath());
	}

	private String findPath() {
		GameState root = new GameState(board, ' ', null);
		GameState goal = naiveSearch(root);
		return recreatePath(goal);
	}	
	
	
	private String recreatePath(GameState goal) {
		return null;
	}

	private GameState naiveSearch(GameState current) {
		if(visited.contains(current)) {
			return null;
		} else if(isCompleted(current)) {
			return current;
		} 
		
		
		return null;
	}
	
	
	private boolean isCompleted(GameState current) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean freeSpace(int x, int y) {
		return board[x][y] != SPACE && board[x][y] != GOAL && board[x][y] != PLAYER;
	}

	private boolean isStuck(int x, int y) {
		if ((freeSpace(x - 1, y) && freeSpace(x + 1, y))
				|| (freeSpace(x, y - 1) && freeSpace(x, y + 1))) {
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
