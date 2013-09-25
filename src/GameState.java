import java.util.Map.Entry;


public class GameState {
	private BoxList boxList;
	public int x, y;
	private String path;
	
	public GameState(BoxList boxList) {
		this.boxList = boxList;
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
	public int hashCode() {
		StringBuilder sb = new StringBuilder();
		for (Entry<Integer, int[]> e : boxList.getEntrySet())
			sb.append(e.getValue()[BoxList.X]).append(',').append(e.getValue()[BoxList.Y]).append(' ');
		return sb.toString().hashCode();
	}

	
}
