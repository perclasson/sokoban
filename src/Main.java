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
	Map<GameState, GameState> pullVisited;
	Map<GameState, GameState> pushVisited;
	private Semaphore threadBlocker;
	private Solver pushSolver, pullSolver;
	private static char[][] pullBoard, pushBoard;

	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		pullVisited = new ConcurrentHashMap<GameState, GameState>();
		pushVisited = new ConcurrentHashMap<GameState, GameState>();
		threadBlocker = new Semaphore(1);
		pullBoard = readBoard();
		pushBoard = cloneMatrix(pullBoard);
		hasher = new ZobristHasher();

		new Thread() {
			public void run() {
				pushSolver = new PushSolver(threadBlocker, pullVisited, pushVisited, pushBoard);
				System.out.println(extractPath(pushSolver.solve()));
				System.exit(0);
			}
		}.start();

		pullSolver = new PullSolver(threadBlocker, pullVisited, pushVisited, pullBoard);
		System.out.println(extractPath(pullSolver.solve()));
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

	private char[][] cloneMatrix(char[][] original) {
		char[][] clone = new char[original.length][];
		for (int i = 0; i < original.length; i++) {
			clone[i] = Arrays.copyOf(original[i], original[i].length);
		}
		return clone;
	}
}
