import java.util.LinkedList;
import java.util.Random;

public class ZobristHasher {

	private Random random;
	int[][][] table;
	private final int PLAYER = 0;
	private final int BOX = 1;

	public ZobristHasher() {
		random = new Random();
		initiate();
	}

	private void initiate() {
		table = new int[Main.yLength][Main.xLength][2];
		for (int r = 0; r < Main.yLength; r++) {
			for (int c = 0; c < Main.xLength; c++) {
				table[r][c][PLAYER] = randomBitString().hashCode();
				table[r][c][BOX] = randomBitString().hashCode();
			}
		}
	}

	public int hash(GameState state) {
		int hash = 0;
		for (Coordinate box : state.getBoxes()) {
			hash = hash ^ table[box.y][box.x][BOX];
		}
		Coordinate topMostLeft = findTopLeftmostPosition(state);
		hash = hash ^ table[topMostLeft.y][topMostLeft.x][PLAYER];
		state.setTopLeftmostPosition(topMostLeft);
		return hash;
	}

	private String randomBitString() {
		byte[] r = new byte[256];
		random.nextBytes(r);
		return new String(r);
	}

	private int moveTile(int hash, int fromX, int fromY, int toX, int toY, int tile) {
		hash = hash ^ table[fromY][fromX][tile];
		hash = hash ^ table[toY][toX][tile];
		return hash;
	}

	public int updatePlayerHash(GameState state) {
		Coordinate newTopLeftmost = findTopLeftmostPosition(state);
		Coordinate oldTopLeftmost = state.getTopLeftmost();
		state.setTopLeftmostPosition(newTopLeftmost);
		return moveTile(state.hashCode(), oldTopLeftmost.x, oldTopLeftmost.y, newTopLeftmost.x, newTopLeftmost.y, PLAYER);
	}

	public int moveBox(int hash, int fromX, int fromY, int toX, int toY) {
		return moveTile(hash, fromX, fromY, toX, toY, BOX);
	}

	private Coordinate findTopLeftmostPosition(GameState state) {
		char[][] visited = new char[Main.yLength][Main.xLength];
		LinkedList<Coordinate> queue = new LinkedList<Coordinate>();
		Coordinate current = null;

		Coordinate topLeftmost = state.getPlayer();
		visited[topLeftmost.y][topLeftmost.x] = 'V';
		queue.add(state.getPlayer());
		while (!queue.isEmpty()) {
			current = queue.pop();
			if ((current.x < topLeftmost.x) || (current.x == topLeftmost.x && current.y < topLeftmost.y)) {
				topLeftmost = current;
			}
			for (int i = 0; i < Constants.dx.length; i++) {
				int x = current.x + Constants.dx[i];
				int y = current.y + Constants.dy[i];
				Coordinate c = new Coordinate(x, y);
				if (visited[y][x] != 'V') {
					visited[y][x] = 'V';
					if (Main.isFreeSpace(state, c)) {
						queue.add(c);
					}
				}

			}
		}
		return topLeftmost;
	}

}
