import java.util.Map.Entry;


public class GameState {
	private BoxList boxList;
	public int x, y, hashCode = -1;
	private String path;
	private GameState previousState;
	
	public GameState(BoxList boxList, int x, int y) {
		this.boxList = boxList;
		this.x = x;
		this.y = y;
	}
	private GameState(BoxList boxList, int x, int y, int hashCode, String path) {
		this.boxList = boxList;
		this.x = x;
		this.y = y;
		this.hashCode = hashCode;
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public BoxList getBoxList() {
		return boxList;
	}
	
	public boolean containsBox(int x, int y) {
		return boxList.containsBox(x, y);
	}
	
	public void setPreviousState(GameState state) {
		previousState = state;
	}
	
	public GameState getPreviousState() {
		return previousState;
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
	
	@Override
	public Object clone() {
		return new GameState((BoxList) boxList.clone(), x, y, hashCode, path);
	}
	
}
