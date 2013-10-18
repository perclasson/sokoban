import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;

public class PullSolver extends Solver {
	private Coordinate initialPosition;

	public PullSolver(Semaphore threadBlocker, Map<GameState, GameState> pullVisited, Map<GameState, GameState> pushVisited, char[][] board) {
		super(board, pullVisited, pushVisited, threadBlocker);
	}

	@Override
	public GameState solve() {
		Set<GameState> startStates = new HashSet<GameState>();
		for (Coordinate box : root.getBoxes()) {
			startStates.addAll(findStartPositions(root, box));
		}
		return findGoal(startStates);
	}

	private GameState search(Set<GameState> startingStates) {
		TreeSet<GameState> queue = new TreeSet<GameState>();
		for (GameState start : startingStates) {
			start.costTo = 0;
			start.totalCost = start.costTo + start.estimateGoalCost(manhattanCost) * Constants.GOAL_COST_SCALE;
		}
		queue.addAll(startingStates);
		while (!queue.isEmpty()) {
			GameState current = queue.pollFirst();
			pullVisited.put(current, current);

			if (isCompleted(current) && !isStuck(current)) {
				try {
					threadBlocker.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return current;
			}
			if (pushVisited.containsKey(current)) {
				try {
					threadBlocker.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return current;
			}
			List<GameState> nextMoves = findPossibleMoves(current);
			for (GameState neighbor : nextMoves) {
				int costTo = current.costTo + 1;
				int totalCost = costTo + neighbor.estimateGoalCost(manhattanCost) * Constants.GOAL_COST_SCALE;
				if (pullVisited.containsKey(neighbor) && totalCost >= neighbor.totalCost) {
					continue;
				}

				if (!queue.contains(neighbor)) {
					neighbor.costTo = costTo;
					neighbor.totalCost = totalCost;
					queue.add(neighbor);
				} else if (totalCost < neighbor.totalCost) {
					neighbor.costTo = costTo;
					neighbor.totalCost = totalCost;
				}
			}
		}
		return null;
	}

	private List<GameState> findStartPositions(GameState root, Coordinate position) {
		List<GameState> states = new ArrayList<GameState>();
		for (int i = 0; i < Constants.dx.length; i++) {
			Coordinate newPlayerPos = new Coordinate(position.x + Constants.dx[i], position.y + Constants.dy[i]);
			if (Main.isFreeSpace(root, newPlayerPos)) {
				GameState newState = root.clone();
				newState.movePlayer(newPlayerPos);
				states.add(newState);
			}
		}
		return states;
	}

	private GameState findGoal(Set<GameState> startStates) {
		GameState goal = search(startStates);
		if (goal == null) {
			return null;
		}
		return goal;
	}

	private boolean isStuck(GameState state) {
		return BoardSearcher.findPath(state, state.getPlayer(), initialPosition) == null;
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

	private List<GameState> findPossibleMoves(GameState state) {
		List<GameState> moves = new ArrayList<GameState>();
		for (Coordinate box : state.getBoxes()) {
			findMovesForBox(state, box, moves);
		}
		return moves;
	}

	private boolean isCompleted(GameState state) {
		for (Coordinate box : state.getBoxes()) {
			if (board[box.y][box.x] != Constants.GOAL) {
				return false;
			}
		}
		return true;
	}

	private GameState makeMove(GameState state, Coordinate box, int dx, int dy) {
		String path = getPath(state, box, new Coordinate(box.x + dx, box.y + dy));
		if (path == null) {
			return null;
		}
		GameState newState = state.clone();
		newState.setPath(path);
		newState.setParent(state);
		newState.moveBox(box, new Coordinate(box.x + dx, box.y + dy));
		newState.movePlayer(new Coordinate(box.x + 2 * dx, box.y + 2 * dy));
		return newState;
	}

	private String getPath(GameState state, Coordinate box, Coordinate to) {
		String path = BoardSearcher.findPath(state, state.getPlayer(), to);
		if (path == null) {
			return null;
		}
		if (to.x > box.x) {
			return "R " + path;
		}
		if (to.x < box.x) {
			return "L " + path;
		}
		if (to.y > box.y) {
			return "D " + path;
		}
		if (to.y < box.y) {
			return "U " + path;
		}
		return null;
	}

	private String invertPath(String path) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < path.length(); i++) {
			char toBeInverted = path.charAt(i);
			switch (toBeInverted) {
			case 'R':
				sb.append('L');
				break;
			case 'L':
				sb.append('R');
				break;
			case 'U':
				sb.append('D');
				break;
			case 'D':
				sb.append('U');
				break;
			default:
				sb.append(toBeInverted);
				break;
			}
		}
		return sb.toString();
	}

	@Override
	GameState extractRootState() {
		Set<Coordinate> boxes = new HashSet<Coordinate>();
		Coordinate player = null;
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[y].length; x++) {
				switch (board[y][x]) {
				case Constants.BOX: {
					board[y][x] = Constants.GOAL;
					goals.add(new Coordinate(x, y));
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
					boxes.add(new Coordinate(x, y));
					board[y][x] = Constants.SPACE;
					break;
				}
				case Constants.PLAYER_ON_GOAL: {
					boxes.add(new Coordinate(x, y));
					player = new Coordinate(x, y);
					board[y][x] = Constants.SPACE;
					break;
				}
				}
			}
		}
		initialPosition = player.clone();
		GameState s = new GameState(-1, player, boxes, null, goals);
		s.setHash(Main.getHasher().hash(s));
		return s;
	}

	private boolean isPossibleMove(GameState state, Coordinate box, int dx, int dy) {
		return Main.isFreeSpace(state, new Coordinate(box.x + dx, box.y + dy)) && Main.isFreeSpace(state, new Coordinate(box.x + 2 * dx, box.y + 2 * dy));
	}

	@Override
	public String recreateMergePath(GameState meetingPoint) {
		StringBuilder sb = new StringBuilder();

		while (meetingPoint != null) {
			if (meetingPoint.getPath() != null)
				sb.append(meetingPoint.getPath());
			meetingPoint = meetingPoint.getParent();
		}
		return invertPath(sb.toString());
	}

	@Override
	public String recreateAlonePath(GameState goal) {
		StringBuilder sb = new StringBuilder();
		String endPath = BoardSearcher.findPath(goal, goal.getPlayer(), initialPosition);

		while (goal != null) {
			if (goal.getPath() != null)
				sb.append(goal.getPath());
			goal = goal.getParent();
		}
		return invertPath(endPath + sb.toString());
	}

}
