import java.util.HashSet;
import java.util.Set;

public class GameState implements Comparable<GameState> {
	private Set<Coordinate> boxes;
	private int hashCode;
	private Coordinate player;
	private Coordinate topMostLeftPosition;
	private String pathFromParent;
	private GameState parent;
	private boolean playerOnGoal;
	private Set<Coordinate> goals;
	private int stepsTo = 0;
	private Coordinate pushPosition;
	public int totalCost, costTo;

	public int estimateGoalCost(int[][] manhattanCost) {
		int value = 0;
		for (Coordinate box : boxes) {
			value += manhattanCost[box.y][box.x];
		}
		return value;
	}

	public GameState(int hashCode, Coordinate player, Set<Coordinate> boxes, GameState parent, Set<Coordinate> goals) {
		this(hashCode, player, boxes, parent, goals, null);
	}

	public GameState(int hashCode, Coordinate player, Set<Coordinate> boxes, GameState parent, Set<Coordinate> goals, Coordinate topLeftmostPosition) {
		this.goals = goals;
		this.hashCode = hashCode;
		this.player = player;
		this.boxes = boxes;
		pathFromParent = "";
		this.parent = parent;
		this.topMostLeftPosition = topLeftmostPosition;
		if (parent != null)
			stepsTo = parent.getStepsTo() + 1;
	}

	public void setParent(GameState parent) {
		this.parent = parent;
	}

	public void movePlayer(Coordinate player) {
		this.player = player;
		hashCode = Main.getHasher().updatePlayerHash(this);
	}

	public void moveBox(Coordinate from, Coordinate to) {
		hashCode = Main.getHasher().moveBox(hashCode, from.x, from.y, to.x, to.y);
		boxes.remove(from);
		boxes.add(to);
	}

	@Override
	public GameState clone() {
		Set<Coordinate> newBoxes = new HashSet<Coordinate>();
		for (Coordinate box : boxes) {
			newBoxes.add(box.clone());
		}
		return new GameState(hashCode, player.clone(), newBoxes, parent, goals, topMostLeftPosition);
	}

	public Coordinate getPlayer() {
		return player;
	}

	public boolean isPlayerOnGoal() {
		return playerOnGoal;
	}

	public Set<Coordinate> getBoxes() {
		return boxes;
	}

	public GameState getParent() {
		return parent;
	}

	public String getPath() {
		return pathFromParent;
	}

	public void setPath(String path) {
		this.pathFromParent = path;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object o) {
		return o.hashCode() == hashCode;
	}

	public boolean containsBox(Coordinate box) {
		return boxes.contains(box);
	}

	public Coordinate getTopLeftmost() {
		return topMostLeftPosition;
	}

	public void setTopLeftmostPosition(Coordinate topMostLeftPosition) {
		this.topMostLeftPosition = topMostLeftPosition;
	}

	public void setPlayer(Coordinate player) {
		this.player = player;
	}

	public void setHash(int hash) {
		this.hashCode = hash;
	}

	@Override
	public int compareTo(GameState arg0) {
		if (equals(arg0)) {
			return 0;
		}
		int score = totalCost - arg0.totalCost;
		if (score == 0) {
			return 1;
		} else {
			return score;
		}
	}

	public int getStepsTo() {
		return stepsTo;
	}

	public Coordinate getPushPosition() {
		return pushPosition;
	}

	public void setPushPosition(Coordinate pushPosition) {
		this.pushPosition = pushPosition;
	}
}
