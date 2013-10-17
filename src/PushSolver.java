import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class PushSolver extends Solver {

	private DeadlockHandler deadlockHandler;

	public PushSolver(Semaphore threadBlocker, Map<GameState, GameState> pullVisited, Map<GameState, GameState> pushVisited, char[][] board) {
		super(board, pullVisited, pushVisited, threadBlocker);
		deadlockHandler = new DeadlockHandler();
		DeadlockHandler.addStaticDeadlocks(board);
	}

	@Override
	public GameState solve() {
		return findPath(root);
	}

	@Override
	GameState extractRootState() {
		Set<Coordinate> boxes = new HashSet<Coordinate>();
		Coordinate player = null;
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[y].length; x++) {
				switch (board[y][x]) {
				case Constants.BOX: {
					boxes.add(new Coordinate(x, y));
					board[y][x] = Constants.SPACE;
					break;
				}
				case Constants.BOX_ON_GOAL: {
					boxes.add(new Coordinate(x, y));
					board[y][x] = Constants.GOAL;
					goals.add(new Coordinate(x, y));
					break;
				}
				case Constants.PLAYER: {
					player = new Coordinate(x, y);
					board[y][x] = Constants.SPACE;
					break;
				}
				case Constants.GOAL: {
					goals.add(new Coordinate(x, y));
					break;
				}
				case Constants.PLAYER_ON_GOAL: {
					board[y][x] = Constants.GOAL;
					goals.add(new Coordinate(x, y));
					player = new Coordinate(x, y);
					break;
				}
				}
			}
		}
		GameState s = new GameState(-1, player, boxes, null, goals);
		s.setHash(Main.getHasher().hash(s));
		return s;
	}

	private GameState findPath(GameState root) {
		GameState goal = search(root);
		if (goal == null) {
			return null;
		}
		return goal;
	}

	private GameState search(GameState start) {
		PriorityQueue<GameState> queue = new PriorityQueue<GameState>();
		start.costTo = 0;
		start.totalCost = start.costTo + start.estimateGoalCost(manhattanCost) * Constants.GOAL_COST_SCALE;
		queue.add(start);
		int depth = start.totalCost;
		List<GameState> IDLeaves = new ArrayList<GameState>();
		while (depth > 0) {
			while (!queue.isEmpty()) {
				GameState current = queue.poll();
				pushVisited.put(current, current);

				if (isCompleted(current)) {
					try {
						threadBlocker.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return current;
				}
				if (pullVisited.containsKey(current)) {
					try {
						threadBlocker.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return current;
				}
				if (current.totalCost > depth) {
					IDLeaves.add(current);
					continue;
				}
				List<GameState> nextMoves = findPossibleMoves(current);
				for (GameState neighbor : nextMoves) {
					int costTo = current.costTo + 1;
					int totalCost = costTo + neighbor.estimateGoalCost(manhattanCost) * Constants.GOAL_COST_SCALE;
					if (pushVisited.containsKey(neighbor) && totalCost >= neighbor.totalCost) {
						continue;
					}

					if (!queue.contains(neighbor) || totalCost < neighbor.totalCost) {
						neighbor.costTo = costTo;
						neighbor.totalCost = totalCost;
						if (!queue.contains(neighbor))
							queue.add(neighbor);
					}
				}
			}
			queue.addAll(IDLeaves);
			depth += 10;
			IDLeaves.clear();
		}
		return null;
	}

	public String recreatePath(GameState goal) {
		StringBuilder sb = new StringBuilder();

		while (goal != null) {
			if (goal.getPath() != null)
				sb.append(goal.getPath());
			goal = goal.getParent();
		}
		return sb.reverse().toString().trim();
	}

	private List<GameState> findPossibleMoves(GameState state) {
		List<GameState> moves = new ArrayList<GameState>();
		for (Coordinate box : state.getBoxes()) {
			findMovesForBox(state, box, moves);
		}
		return moves;
	}

	private void findMovesForBox(GameState state, Coordinate box, List<GameState> moves) {
		GameState newState = null;
		for (int i = 0; i < Constants.dx.length; i++) {
			if (isPossibleMove(state, box, Constants.dx[i], Constants.dy[i])) {
				newState = makeMove(state, box, Constants.dx[i], Constants.dy[i]);
				if (newState != null) {
					moves.add(newState);
				}
			}
		}
	}

	private GameState makeMove(GameState state, Coordinate box, int dx, int dy) {
		String path = getPath(state, box, new Coordinate(box.x - dx, box.y - dy));
		if (path == null) {
			return null;
		}
		GameState newState = state.clone();
		newState.setPath(path);
		newState.setParent(state);
		newState.moveBox(box, new Coordinate(box.x + dx, box.y + dy));
		newState.movePlayer(new Coordinate(box.x, box.y));
		return newState;
	}

	private String getPath(GameState state, Coordinate box, Coordinate to) {
		String path = BoardSearcher.findPath(state, state.getPlayer(), to);
		if (path == null) {
			return null;
		}
		if (to.x > box.x) {
			return "L " + path;
		}
		if (to.x < box.x) {
			return "R " + path;
		}
		if (to.y > box.y) {
			return "U " + path;
		}
		if (to.y < box.y) {
			return "D " + path;
		}
		return null;
	}

	private boolean isPossibleMove(GameState state, Coordinate box, int dx, int dy) {
		boolean easyCheck = (board[box.y + dy][box.x + dx] == Constants.GOAL && !state.containsBox(new Coordinate(box.x + dx, box.y + dy))) || (board[box.y + dy][box.x + dx] != Constants.DEADLOCK && (Main.isFreeSpace(state, new Coordinate(box.x + dx, box.y + dy)) && Main.isFreeSpace(state, new Coordinate(box.x - dx, box.y - dy))));
		if (!easyCheck) {
			return false;
		}
		if (matchesDatabase(state, box, dx, dy))
			return false;

		return true;
	}

	private boolean matchesDatabase(GameState state, Coordinate box, int dx, int dy) {
		Coordinate midPoint = new Coordinate(box.x + dx, box.y + dy);
		Coordinate curr;
		DeadlockState deadlock = null;
		for (int i = 0; i < Constants.bigdx.length; i++) {
			curr = new Coordinate(midPoint.x + Constants.bigdx[i], midPoint.y + Constants.bigdy[i]);
			if (board[curr.y][curr.x] == Constants.WALL)
				continue;
			deadlock = new DeadlockState(board[curr.y - 1][curr.x - 1], board[curr.y - 1][curr.x], board[curr.y - 1][curr.x + 1], board[curr.y][curr.x - 1], board[curr.y][curr.x], board[curr.y][curr.x + 1], board[curr.y + 1][curr.x - 1], board[curr.y + 1][curr.x], board[curr.y + 1][curr.x + 1]);
			for (int j = 0; j < Constants.bigdx.length; j++) {
				if (state.containsBox(new Coordinate(curr.x + Constants.bigdx[j], curr.y + Constants.bigdy[j]))) {
					board[curr.y + Constants.bigdy[j]][curr.x + Constants.bigdx[j]] = Constants.BOX;
				}
			}
			if (deadlockHandler.isKnownDeadlock(deadlock)) {
				System.out.println("yes");
				return true;
			}
		}
		return false;
	}

	private boolean isCompleted(GameState state) {
		for (Coordinate box : state.getBoxes()) {
			if (board[box.y][box.x] != Constants.GOAL) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String recreateMergePath(GameState meetingPoint) {
		return recreatePath(meetingPoint);
	}

	@Override
	public String recreateAlonePath(GameState goal) {
		return recreatePath(goal);
	}
}
