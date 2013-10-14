import java.util.ArrayList;
import java.util.List;

public class GameState implements Comparable<GameState> {
	private List<Coordinate> boxes;
	public int x, y, hashCode = -1;
	private String path;
	private Coordinate player;
	private int heuristic;

	public GameState(List<Coordinate> boxes, Coordinate player, int hashCode) {
		this(boxes, player, "", hashCode);
	}

	public GameState(List<Coordinate> boxes, Coordinate player, String path, int hashCode) {
		this(boxes, player, path, hashCode, -1);
	}
	public GameState(List<Coordinate> boxes, Coordinate player, String path, int hashCode, int heuristic) {
		this.boxes = boxes;
		this.player = player;
		this.path = path;
		this.hashCode = hashCode;
		this.heuristic = heuristic;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean containsBox(int x, int y) {
		for(Coordinate lbox : boxes) {
			if(lbox.x == x && lbox.y == y) {
				return true;
			}
		}
		return false;
	}
	
	public boolean removeBox(int x, int y) {
		return boxes.remove(new Coordinate(x, y));
	}

	public boolean addBox(int x, int y) {
		boxes.add(new Coordinate(x,y));
		return true;
	}
	@Override
	public boolean equals(Object o) {
		return hashCode == ((GameState) o).hashCode();
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}
	
	public void createHashCode(ZobristHasher hasher, Heuristic heuristic, int bX, int bY, int dX, int dY) {
		int newCode = hasher.movePlayer(hashCode, getPlayer().x, getPlayer().y, bX,bY);
		newCode = hasher.moveBox(newCode, bX, bY, bX+dX,bY+dY);
		setHashCode(newCode);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	public List<Coordinate> getBoxes() {
		return boxes;
	}

	public int numberOfBoxes() {
		return boxes.size();
	}

	@Override
	public Object clone() {
		List<Coordinate> nBoxes = new ArrayList<Coordinate>();
		for(Coordinate box : boxes) {
			nBoxes.add(box.clone());
		}
		return new GameState(nBoxes, player.clone(), path, hashCode);
	}


	public void setHeuristic(int heuristic) {
		this.heuristic = heuristic;
	}

	@Override
	public int compareTo(GameState arg0) {
		if (this.heuristic < arg0.heuristic) {
			return -1;
		} else if (this.heuristic > arg0.heuristic) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public Coordinate getPlayer() {
		return player;
	}

}
