public class DeadlockHandler {

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

}
