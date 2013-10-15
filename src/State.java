import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class State {
	private Set<Coordinate> boxes;
	private Set<Coordinate> goals;
	private int hashCode;
	private Coordinate player;
	private int value;
	private String pathFromParent;
	private State parent;
	private boolean playerOnGoal;

	public int getValue() {
		if (value != -1)
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

	public State(int hashCode, Coordinate player, Set<Coordinate> boxes, State parent) {
		this.hashCode = hashCode;
		this.player = player;
		this.boxes = boxes;
		pathFromParent = "";
		this.parent = parent;
		value = -1;
	}

	public State(int hashCode, Coordinate player, Set<Coordinate> boxes, State parent, boolean playerOnGoal) {
		this.hashCode = hashCode;
		this.boxes = boxes;
		this.parent = parent;
		this.player = player;
		this.playerOnGoal = playerOnGoal;
	}

	public void setParent(State parent) {
		this.parent = parent;
	}

	public void movePlayer(Coordinate player) {
		hashCode = Main.getHasher().movePlayer(hashCode, this.player.x, this.player.y, player.x, player.y);
		this.player = player;
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
		return new State(hashCode, player.clone(), newBoxes, parent);
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
}
