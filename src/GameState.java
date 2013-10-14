import java.util.ArrayList;
import java.util.List;

public class GameState implements Comparable<GameState> {
	private List<Coordinate> boxes;
	public int x, y, hashCode = -1;
	private String path;
	private Coordinate player;
	private int heuristic;

	public GameState(List<Coordinate> boxes, Coordinate player, int heuristic) {
		this.boxes = boxes;
		this.player = player;
		this.heuristic = heuristic;
		path = "";
	}

	private GameState(List<Coordinate> boxes, Coordinate player, String path, int heuristic) {
		this.boxes = boxes;
		this.player = player;
		this.path = path;
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
		for(Coordinate lbox : boxes) {
			if(lbox.x == x && y == lbox.y) {
				//System.out.println("Before removing box: " + boxes.size());
				boxes.remove(lbox);
				//System.out.println("After removing box: " + boxes.size());
				return true;
			}
		}
		return false;
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

	@Override
	public int hashCode() {
		if (hashCode == -1) {
			
		}
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
		return new GameState(nBoxes, player.clone(), path, heuristic);
	}

	/*
	 * //TODO: Is this actually correct?
	 * 
	 * @Override public int compareTo(GameState arg0) {
	 * if(heuristic.getValue(this) < heuristic.stupidHeuristic(arg0)) { return
	 * 1; } else if(heuristic.stupidHeuristic(this) >
	 * heuristic.stupidHeuristic(arg0)) { return -1; } return 0; }
	 */

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
