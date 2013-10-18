import java.util.HashSet;
import java.util.Set;

public class DeadlockHandler {

	private Set<DeadlockState> known3x3Deadlocks, known2x2Deadlocks;
	private final char BOX = Constants.BOX, WALL = Constants.WALL, SPACE = Constants.SPACE;

	public DeadlockHandler() {
		known3x3Deadlocks = new HashSet<DeadlockState>();
		known2x2Deadlocks = new HashSet<DeadlockState>();
		generateDatabase();
	}

	public static void addStaticDeadlocks(char[][] board) {
		int upperLeftX = -1, downLeftX = -1, maxWidth = 0;
		// Mark horizontal deadlocks
		for (int y = 0; y < board.length; y++) {
			maxWidth = Math.max(board[y].length, maxWidth);
			for (int x = 0; x < board[y].length; x++) {
				try {
					if (upperLeftX > 0) {
						if (board[y][x] == Constants.WALL && board[y + 1][x] == Constants.WALL) {
							for (int xi = upperLeftX; xi < x; xi++) {
								board[y + 1][xi] = Constants.DEADLOCK;
							}
							upperLeftX = -1;

						} else if (board[y][x] != Constants.WALL || (board[y + 1][x] != Constants.SPACE && board[y + 1][x] != Constants.DEADLOCK)) {
							upperLeftX = -1;
						}
					} else if (downLeftX > 0) {
						if (board[y][x] == Constants.WALL && board[y + 1][x] == Constants.WALL) {
							for (int xi = downLeftX; xi < x; xi++) {
								board[y][xi] = Constants.DEADLOCK;
							}
							downLeftX = -1;

						} else if ((board[y][x] != Constants.SPACE && board[y][x] != Constants.DEADLOCK) || board[y + 1][x] != Constants.WALL) {
							downLeftX = -1;
						}
					} else if (board[y][x] == Constants.WALL && board[y + 1][x] == Constants.WALL && board[y][x + 1] == Constants.WALL && (board[y + 1][x + 1] == Constants.SPACE || board[y + 1][x + 1] == Constants.DEADLOCK)) {
						// We found a corner
						board[y + 1][x + 1] = Constants.DEADLOCK;
						upperLeftX = x + 1;
					} else if (board[y][x] == Constants.WALL && board[y + 1][x] == Constants.WALL && (board[y][x + 1] == Constants.SPACE || board[y][x + 1] == Constants.DEADLOCK) && board[y + 1][x + 1] == Constants.WALL) {
						// We found a corner
						board[y][x + 1] = Constants.DEADLOCK;
						downLeftX = x + 1;
					} else if ((board[y][x] == Constants.SPACE || board[y][x] == Constants.DEADLOCK) && board[y + 1][x] == Constants.WALL && board[y][x + 1] == Constants.WALL && board[y + 1][x + 1] == Constants.WALL) {
						// We found a corner
						board[y][x] = Constants.DEADLOCK;
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
						if (board[y][x] == Constants.WALL && board[y][x + 1] == Constants.WALL) {
							for (int yi = upperLeftY; yi < y; yi++) {
								board[yi][x + 1] = Constants.DEADLOCK;
							}
							upperLeftY = -1;

						} else if (board[y][x] != Constants.WALL || (board[y][x + 1] != Constants.SPACE && board[y][x + 1] != Constants.DEADLOCK)) {
							upperLeftY = -1;
						}
					} else if (upperRightY > 0) {
						if (board[y][x] == Constants.WALL && board[y][x + 1] == Constants.WALL) {
							for (int yi = upperRightY; yi < y; yi++) {
								board[yi][x] = Constants.DEADLOCK;
							}
							upperRightY = -1;

						} else if ((board[y][x] != Constants.SPACE && board[y][x] != Constants.DEADLOCK) || board[y][x + 1] != Constants.WALL) {
							upperRightY = -1;
						}
					} else if (board[y][x] == Constants.WALL && board[y + 1][x] == Constants.WALL && board[y][x + 1] == Constants.WALL && (board[y + 1][x + 1] == Constants.SPACE || board[y + 1][x + 1] == Constants.DEADLOCK)) {
						// We found a corner
						board[y + 1][x + 1] = Constants.DEADLOCK;
						upperLeftY = y + 1;
					} else if (board[y][x] == Constants.WALL && (board[y + 1][x] == Constants.SPACE || board[y + 1][x] == Constants.DEADLOCK) && board[y][x + 1] == Constants.WALL && board[y + 1][x + 1] == Constants.WALL) {
						// We found a corner
						board[y + 1][x] = Constants.DEADLOCK;
						upperRightY = y + 1;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					continue;
				}
			}
			upperLeftY = -1;
			upperRightY = -1;
		}
	}

	public boolean isKnownDeadlock(DeadlockState state) {
		if(state.is3x3())
			return known3x3Deadlocks.contains(state);
		else
			return known2x2Deadlocks.contains(state);
	}

	private void generateDatabase() {
		
		known2x2Deadlocks.add(new DeadlockState(WALL, BOX, WALL, BOX));
		known2x2Deadlocks.add(new DeadlockState(BOX, WALL, BOX, WALL));
		known2x2Deadlocks.add(new DeadlockState(WALL, WALL, BOX, BOX));
		known2x2Deadlocks.add(new DeadlockState(BOX, BOX, WALL, WALL));
		
		known2x2Deadlocks.add(new DeadlockState(BOX, BOX, BOX, WALL));
		known2x2Deadlocks.add(new DeadlockState(BOX, BOX, WALL, BOX));
		known2x2Deadlocks.add(new DeadlockState(BOX, WALL, BOX, BOX));
		known2x2Deadlocks.add(new DeadlockState(WALL, BOX, BOX, BOX));
		
		known2x2Deadlocks.add(new DeadlockState(BOX, BOX, BOX, BOX));
		
		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, SPACE, WALL, SPACE, WALL, WALL, BOX, WALL));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, WALL, WALL, SPACE, BOX, SPACE, WALL, WALL));

		known3x3Deadlocks.add(new DeadlockState(WALL, BOX, WALL, WALL, SPACE, WALL, SPACE, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(WALL, WALL, SPACE, BOX, SPACE, WALL, WALL, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, SPACE, WALL, SPACE, WALL, WALL, BOX, BOX));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, BOX, WALL, SPACE, BOX, SPACE, WALL, WALL));

		known3x3Deadlocks.add(new DeadlockState(BOX, BOX, WALL, WALL, SPACE, WALL, SPACE, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(WALL, WALL, SPACE, BOX, SPACE, WALL, BOX, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, SPACE, WALL, SPACE, WALL, BOX, BOX, WALL));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, WALL, WALL, SPACE, BOX, SPACE, WALL, BOX));

		known3x3Deadlocks.add(new DeadlockState(WALL, BOX, BOX, WALL, SPACE, WALL, SPACE, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(BOX, WALL, SPACE, BOX, SPACE, WALL, WALL, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, SPACE, WALL, SPACE, BOX, WALL, BOX, WALL));

		known3x3Deadlocks.add(new DeadlockState(SPACE, BOX, WALL, WALL, SPACE, BOX, SPACE, WALL, WALL));

		known3x3Deadlocks.add(new DeadlockState(WALL, BOX, WALL, BOX, SPACE, WALL, SPACE, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(WALL, WALL, SPACE, BOX, SPACE, WALL, WALL, BOX, SPACE));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, SPACE, BOX, SPACE, WALL, WALL, BOX, WALL));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, WALL, WALL, SPACE, BOX, SPACE, BOX, WALL));

		known3x3Deadlocks.add(new DeadlockState(WALL, BOX, WALL, WALL, SPACE, BOX, SPACE, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(WALL, BOX, SPACE, BOX, SPACE, WALL, WALL, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, SPACE, WALL, SPACE, BOX, WALL, BOX, BOX));

		known3x3Deadlocks.add(new DeadlockState(SPACE, BOX, BOX, WALL, SPACE, BOX, SPACE, WALL, WALL));

		known3x3Deadlocks.add(new DeadlockState(BOX, BOX, WALL, BOX, SPACE, WALL, SPACE, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(WALL, WALL, SPACE, BOX, SPACE, WALL, BOX, BOX, SPACE));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, SPACE, BOX, SPACE, WALL, BOX, BOX, WALL));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, WALL, WALL, SPACE, BOX, SPACE, BOX, BOX));

		known3x3Deadlocks.add(new DeadlockState(WALL, BOX, BOX, WALL, SPACE, BOX, SPACE, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(BOX, BOX, SPACE, BOX, SPACE, WALL, WALL, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, SPACE, WALL, SPACE, WALL, BOX, BOX, BOX));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, BOX, WALL, SPACE, BOX, SPACE, WALL, BOX));

		known3x3Deadlocks.add(new DeadlockState(BOX, BOX, BOX, WALL, SPACE, WALL, SPACE, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(BOX, WALL, SPACE, BOX, SPACE, WALL, BOX, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, SPACE, WALL, SPACE, BOX, BOX, BOX, BOX));

		known3x3Deadlocks.add(new DeadlockState(SPACE, BOX, BOX, WALL, SPACE, BOX, SPACE, WALL, BOX));

		known3x3Deadlocks.add(new DeadlockState(BOX, BOX, BOX, BOX, SPACE, WALL, SPACE, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(BOX, WALL, SPACE, BOX, SPACE, WALL, BOX, BOX, SPACE));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, SPACE, BOX, SPACE, WALL, BOX, BOX, BOX));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, BOX, WALL, SPACE, BOX, SPACE, BOX, BOX));

		known3x3Deadlocks.add(new DeadlockState(BOX, BOX, BOX, WALL, SPACE, BOX, SPACE, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(BOX, BOX, SPACE, BOX, SPACE, WALL, BOX, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(SPACE, BOX, WALL, WALL, BOX, SPACE, SPACE, SPACE, SPACE));

		known3x3Deadlocks.add(new DeadlockState(WALL, SPACE, SPACE, BOX, BOX, SPACE, SPACE, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(SPACE, SPACE, SPACE, SPACE, BOX, WALL, WALL, BOX, SPACE));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, SPACE, SPACE, BOX, BOX, SPACE, SPACE, WALL));

		known3x3Deadlocks.add(new DeadlockState(WALL, BOX, SPACE, SPACE, BOX, WALL, SPACE, SPACE, SPACE));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, SPACE, BOX, BOX, SPACE, WALL, SPACE, SPACE));

		known3x3Deadlocks.add(new DeadlockState(SPACE, SPACE, SPACE, WALL, BOX, SPACE, SPACE, BOX, WALL));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, SPACE, BOX, BOX, SPACE, WALL, SPACE, SPACE));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, SPACE, BOX, SPACE, BOX, BOX, BOX, BOX));

		known3x3Deadlocks.add(new DeadlockState(SPACE, BOX, BOX, WALL, SPACE, BOX, SPACE, BOX, BOX));

		known3x3Deadlocks.add(new DeadlockState(BOX, BOX, BOX, BOX, SPACE, BOX, SPACE, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(BOX, BOX, SPACE, BOX, SPACE, WALL, BOX, BOX, SPACE));

		known3x3Deadlocks.add(new DeadlockState(WALL, WALL, SPACE, WALL, SPACE, WALL, SPACE, BOX, WALL));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, WALL, WALL, SPACE, BOX, WALL, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(WALL, BOX, SPACE, WALL, SPACE, WALL, SPACE, WALL, WALL));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, WALL, BOX, SPACE, WALL, WALL, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, WALL, WALL, SPACE, WALL, WALL, BOX, SPACE));

		known3x3Deadlocks.add(new DeadlockState(WALL, WALL, SPACE, BOX, SPACE, WALL, SPACE, WALL, WALL));

		known3x3Deadlocks.add(new DeadlockState(SPACE, BOX, WALL, WALL, SPACE, WALL, WALL, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(WALL, WALL, SPACE, WALL, SPACE, BOX, SPACE, WALL, WALL));

		known3x3Deadlocks.add(new DeadlockState(WALL, WALL, SPACE, WALL, SPACE, BOX, SPACE, BOX, BOX));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, WALL, BOX, SPACE, WALL, BOX, BOX, SPACE));

		known3x3Deadlocks.add(new DeadlockState(BOX, BOX, SPACE, BOX, SPACE, WALL, SPACE, WALL, WALL));

		known3x3Deadlocks.add(new DeadlockState(SPACE, BOX, BOX, WALL, SPACE, BOX, WALL, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(BOX, BOX, SPACE, BOX, SPACE, BOX, SPACE, BOX, BOX));

		known3x3Deadlocks.add(new DeadlockState(SPACE, BOX, BOX, BOX, SPACE, BOX, BOX, BOX, SPACE));

		known3x3Deadlocks.add(new DeadlockState(WALL, BOX, SPACE, BOX, SPACE, BOX, SPACE, BOX, BOX));

		known3x3Deadlocks.add(new DeadlockState(SPACE, BOX, WALL, BOX, SPACE, BOX, BOX, BOX, SPACE));

		known3x3Deadlocks.add(new DeadlockState(BOX, BOX, SPACE, BOX, SPACE, BOX, SPACE, BOX, WALL));

		known3x3Deadlocks.add(new DeadlockState(SPACE, BOX, BOX, BOX, SPACE, BOX, WALL, BOX, SPACE));

		known3x3Deadlocks.add(new DeadlockState(BOX, BOX, SPACE, WALL, SPACE, BOX, SPACE, BOX, BOX));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, BOX, BOX, SPACE, BOX, BOX, BOX, SPACE));

		known3x3Deadlocks.add(new DeadlockState(BOX, BOX, SPACE, BOX, SPACE, WALL, SPACE, BOX, BOX));

		known3x3Deadlocks.add(new DeadlockState(SPACE, BOX, BOX, BOX, SPACE, BOX, BOX, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(SPACE, BOX, BOX, BOX, SPACE, WALL, BOX, BOX, SPACE));

		known3x3Deadlocks.add(new DeadlockState(BOX, BOX, SPACE, BOX, SPACE, BOX, SPACE, WALL, BOX));

		known3x3Deadlocks.add(new DeadlockState(SPACE, BOX, BOX, WALL, SPACE, BOX, BOX, BOX, SPACE));

		known3x3Deadlocks.add(new DeadlockState(BOX, WALL, SPACE, BOX, SPACE, BOX, SPACE, BOX, BOX));

		known3x3Deadlocks.add(new DeadlockState(BOX, WALL, SPACE, WALL, SPACE, BOX, SPACE, BOX, BOX));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, BOX, BOX, SPACE, WALL, BOX, BOX, SPACE));

		known3x3Deadlocks.add(new DeadlockState(BOX, BOX, SPACE, BOX, SPACE, WALL, SPACE, WALL, BOX));

		known3x3Deadlocks.add(new DeadlockState(SPACE, BOX, BOX, WALL, SPACE, BOX, BOX, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(WALL, BOX, SPACE, WALL, SPACE, BOX, SPACE, BOX, BOX));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, WALL, BOX, SPACE, BOX, BOX, BOX, SPACE));

		known3x3Deadlocks.add(new DeadlockState(BOX, BOX, SPACE, BOX, SPACE, WALL, SPACE, BOX, WALL));

		known3x3Deadlocks.add(new DeadlockState(SPACE, BOX, BOX, BOX, SPACE, BOX, WALL, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(SPACE, BOX, WALL, BOX, SPACE, WALL, BOX, BOX, SPACE));

		known3x3Deadlocks.add(new DeadlockState(BOX, BOX, SPACE, BOX, SPACE, BOX, SPACE, WALL, WALL));

		known3x3Deadlocks.add(new DeadlockState(SPACE, BOX, BOX, WALL, SPACE, BOX, WALL, BOX, SPACE));

		known3x3Deadlocks.add(new DeadlockState(WALL, WALL, SPACE, BOX, SPACE, BOX, SPACE, BOX, BOX));

		known3x3Deadlocks.add(new DeadlockState(WALL, WALL, SPACE, WALL, SPACE, WALL, SPACE, BOX, BOX));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, WALL, BOX, SPACE, WALL, BOX, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(BOX, BOX, SPACE, WALL, SPACE, WALL, SPACE, WALL, WALL));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, BOX, WALL, SPACE, BOX, WALL, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, WALL, WALL, SPACE, WALL, BOX, BOX, SPACE));

		known3x3Deadlocks.add(new DeadlockState(BOX, WALL, SPACE, BOX, SPACE, WALL, SPACE, WALL, WALL));

		known3x3Deadlocks.add(new DeadlockState(SPACE, BOX, BOX, WALL, SPACE, WALL, WALL, WALL, SPACE));

		known3x3Deadlocks.add(new DeadlockState(WALL, WALL, SPACE, WALL, SPACE, BOX, SPACE, WALL, BOX));

		known3x3Deadlocks.add(new DeadlockState(WALL, WALL, SPACE, WALL, SPACE, BOX, SPACE, BOX, WALL));

		known3x3Deadlocks.add(new DeadlockState(SPACE, WALL, WALL, BOX, SPACE, WALL, WALL, BOX, SPACE));

		known3x3Deadlocks.add(new DeadlockState(WALL, BOX, SPACE, BOX, SPACE, WALL, SPACE, WALL, WALL));

		known3x3Deadlocks.add(new DeadlockState(SPACE, BOX, WALL, WALL, SPACE, BOX, WALL, WALL, SPACE));
	}
}
