import java.util.HashSet;
import java.util.Set;

public class State implements Comparable<State> {
	private Set<Coordinate> boxes;
	private int hashCode;
	private Coordinate player;
	private Coordinate topMostLeftPosition;
	private int value;
	private String pathFromParent;
	private State parent;
	private boolean playerOnGoal;
	private Set<Coordinate> goals;
	private int stepsTo = 0;
	public int totalCost, costTo;
	
	public int estimateGoalCost() {
		int value = 0;
		for (Coordinate box : boxes) {
			value += Main.manhattanCost[box.y][box.x];
		}
		return value;
	}

	public int getValueSofisticated() {
		if (value != Integer.MIN_VALUE)
			return value;
		int N = boxes.size();
		int[][] values = new int[N][N];

		int b = 0;
		for (Coordinate box : boxes) {
			int g = 0;
			for (Coordinate goal : goals) {
				values[b][g] = Math.abs(goal.x - box.x + goal.y - goal.y);
				g++;
			}
			b++;
		}

		int[] matchBox = new int[N]; // matchBox[box] = goal
		int[] matchGoal = new int[N]; // matchGoal[goal] = box

		// naive bipartite match
		for (int i = 0; i < N; i++) {
			matchBox[i] = i;
			matchGoal[i] = i;
		}

		// better bipartite match
		for (int newBox = 0; newBox < N; newBox++) {
			for (int newGoal = 0; newGoal < N; newGoal++) {
				if (values[newBox][newGoal] >= values[newBox][matchBox[newBox]])
					continue;
				int currentBox = matchGoal[newGoal];
				int currentGoal = matchBox[newBox];
				if ((values[currentBox][newGoal] + values[newBox][currentGoal]) >
					(values[newBox][newGoal] + values[currentGoal][currentGoal])) {
					matchBox[newBox] = newGoal;
					matchGoal[newGoal] = newBox;
					matchBox[currentBox] = currentGoal;
					matchGoal[currentGoal] = currentBox;
					newGoal = 0;
				}
			}
		}
		
		int value = 0;
		for(int i = 0; i < N; i++) {
			value += values[i][matchBox[i]];
		}
		
		return value;
	}

	public State(int hashCode, Coordinate player, Set<Coordinate> boxes, State parent, Set<Coordinate> goals) {
		this(hashCode, player, boxes, parent, goals, null);
	}
	
	public State(int hashCode, Coordinate player, Set<Coordinate> boxes, State parent, Set<Coordinate> goals, Coordinate topLeftmostPosition) {
		this.goals = goals;
		this.hashCode = hashCode;
		this.player = player;
		this.boxes = boxes;
		pathFromParent = "";
		this.parent = parent;
		this.topMostLeftPosition = topLeftmostPosition;
		value = Integer.MIN_VALUE;
		if(parent != null)
			stepsTo = parent.getStepsTo() + 1;
	}

	public void setParent(State parent) {
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
	public State clone() {
		Set<Coordinate> newBoxes = new HashSet<Coordinate>();
		for (Coordinate box : boxes) {
			newBoxes.add(box.clone());
		}
		return new State(hashCode, player.clone(), newBoxes, parent, goals, topMostLeftPosition);
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

	public State getParent() {
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
	public int compareTo(State arg0) {
		if(equals(arg0)) {
			return 0;
		}
		int score = totalCost - arg0.totalCost;
		if(score == 0) {
			return 1;
		} else {
			return score;
		}
	}

	
	public int getStepsTo() {
		return stepsTo;
	}
}
