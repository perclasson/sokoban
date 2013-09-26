import java.util.Map.Entry;


public class GameState implements Comparable<GameState> {
	private BoxList boxList;
	public int x, y, hashCode = -1;
	private String path;
	private Heuristic heuristic;
	
	public GameState(BoxList boxList, int x, int y, Heuristic heuristic) {
		this.boxList = boxList;
		this.x = x;
		this.y = y;
		this.heuristic = heuristic;
		path = "";
	}
	private GameState(BoxList boxList, int x, int y, String path, Heuristic heuristic) {
		this.boxList = boxList;
		this.x = x;
		this.y = y;
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
		return boxList.containsBox(x, y);
	}
	
	@Override
	public boolean equals(Object o) {
		return hashCode == ((GameState) o).hashCode();
	}
	
	@Override
	public int hashCode() {
		if(hashCode == -1) {
			StringBuilder sb = new StringBuilder();
			for (Entry<Integer, int[]> e : boxList.getEntrySet())
				sb.append(e.getValue()[BoxList.X]).append(',').append(e.getValue()[BoxList.Y]).append(';').append(boxList.getDirectionString(e.getKey()));
			hashCode = sb.toString().hashCode();
		}
		return hashCode;
	}
	public BoxList getBoxList() {
		return boxList;
	}
	public int numberOfBoxes() {
		return boxList.size();
	}
	
	@Override
	public Object clone() {
		return new GameState((BoxList) boxList.clone(), x, y, path, heuristic);
	}
	
	//TODO: Is this actually correct?
	@Override
	public int compareTo(GameState arg0) {
		if(heuristic.stupidHeuristic(this) < heuristic.stupidHeuristic(arg0)) {
			return 1;
		} else if(heuristic.stupidHeuristic(this) > heuristic.stupidHeuristic(arg0)) {
			return -1;
		}
		return 0;
	}

	public void setHeuristic(Heuristic heuristic) {
		this.heuristic = heuristic;
	}
	
}
