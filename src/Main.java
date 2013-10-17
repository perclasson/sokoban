import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class Main {
	private static ZobristHasher hasher;
	public static int yLength, xLength;
	Map<GameState, GameState> pullVisited = new ConcurrentHashMap<GameState, GameState>();
	Map<GameState, GameState> pushVisited = new ConcurrentHashMap<GameState, GameState>();
	private Semaphore threadBlocker = new Semaphore(1);
	private Solver pushSolver, pullSolver;
	private static char[][] pullBoard, pushBoard;

	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		pullBoard = readBoard();
		pushBoard = cloneMatrix(pullBoard);
		hasher = new ZobristHasher();
		long before = System.currentTimeMillis();

		new Thread() {
			public void run() {
				long before = System.currentTimeMillis();
				pushSolver = new PushSolver(threadBlocker, pullVisited, pushVisited, pushBoard);
				System.out.println(extractPath(pushSolver.solve()));
				 System.out.println("push! took " + (System.currentTimeMillis() - before));
				System.exit(0);
			}
		}.start();

		pullSolver = new PullSolver(threadBlocker, pullVisited, pushVisited, pullBoard);
		System.out.println(extractPath(pullSolver.solve()));
		System.out.println("pull! took " + (System.currentTimeMillis() - before));
		System.exit(0);

	}

	private String extractPath(GameState meetingPoint) {
		GameState pullMeetingPoint = pullVisited.get(meetingPoint);
		GameState pushMeetingPoint = pushVisited.get(meetingPoint);
		String pushPath = "";
		if (pushMeetingPoint != null)
			pushPath = pushSolver.recreateMergePath(pushMeetingPoint);
		String pullPath = "";
		if (pullMeetingPoint != null)
			pullPath = pullSolver.recreateMergePath(pullMeetingPoint);

		if (pushPath == "") {
			return pullSolver.recreateAlonePath(pullMeetingPoint);
		} else if (pullPath == "") {
			return pushSolver.recreateAlonePath(pushMeetingPoint);
		} else {
			String mergePath = new StringBuilder().append(BoardSearcher.findPath(pullMeetingPoint, pushMeetingPoint.getPlayer(), pullMeetingPoint.getPlayer())).reverse().toString().trim();
			pullMeetingPoint.setPlayer(pushMeetingPoint.getPlayer());
			return (pushPath + " " + mergePath + " " + pullPath).trim();
		}
	}

	public static ZobristHasher getHasher() {
		return hasher;
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

		char[][] board = new char[i][];
		yLength = i;
		for (int j = 0; j < board.length; j++) {
			board[j] = tmp.get(j);
			xLength = Math.max(xLength, board[j].length);
		}
		return board;
	}

	public static boolean isFreeSpace(GameState state, Coordinate coordinate) {
		return !state.containsBox(coordinate) && pullBoard[coordinate.y][coordinate.x] != Constants.WALL;
	}

	public static void printMatrix(char[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				System.err.print(matrix[i][j]);
			}
			System.err.println();
		}
	}

	public static void printState(GameState state, char[][] board) {
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

	private char[][] cloneMatrix(char[][] original) {
		char[][] clone = new char[original.length][];
		for (int i = 0; i < original.length; i++) {
			clone[i] = Arrays.copyOf(original[i], original[i].length);
		}
		return clone;
	}
}
