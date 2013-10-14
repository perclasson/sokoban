import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class State {
	private Set<Coordinate> boxes;
	private int hashCode;
	private Coordinate player;

	private String pathFromParent;
	private State parent;
	private boolean playerOnGoal;

	public State(int hashCode, Coordinate player, Set<Coordinate> boxes, State parent) {
		boxes = new HashSet<Coordinate>();
		this.hashCode = hashCode;
		this.player = player;
		this.boxes = boxes;
		pathFromParent = "";
		this.parent = parent;
	}
	
	public State(int hashCode, Coordinate player, Set<Coordinate> boxes, State parent, boolean playerOnGoal) {
		this(hashCode, player, boxes, parent);
		this.playerOnGoal = playerOnGoal;
	}

	public void setParent(State parent) {
		this.parent = parent;
	}

	public void movePlayer(Coordinate player) {
		hashCode = Main.getHasher().movePlayer(hashCode,this.player.x, this.player.y, player.x, player.y);
		this.player = player;
	}
	
	public void moveBox(Coordinate from, Coordinate to) {
		hashCode = Main.getHasher().moveBox(hashCode,from.x, from.y, to.x, to.y);
		boxes.remove(from);
		boxes.add(to);
	}
	
	@Override
	public State clone() {
		Set<Coordinate> newBoxes = new HashSet<Coordinate>();
		for(Coordinate box : boxes) {
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
