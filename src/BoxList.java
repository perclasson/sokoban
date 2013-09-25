import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;


public class BoxList {
	public static final int X = 0, Y = 1, UP = 2, RIGHT = 3, DOWN = 4, LEFT = 5;
	private Map<Integer, int[]> boxes;
	public static final int X_SHIFT = 10000;
	
	public BoxList() {
		boxes = new TreeMap<Integer, int[]>();
	}
	
	public void addBox(int x, int y) {
		addBox(x, y, 0, 0, 0, 0);
	}
	
	public Set<Entry<Integer, int[]>> getEntrySet() {
		return boxes.entrySet();
	}
	
	public void addBox(int x, int y, int up, int right, int down, int left) {
		int[] box = {x, y, up, right, down, left};
		boxes.put(x*X_SHIFT+y,box);
	}
	
	public int size() {
		return boxes.size();
	}
	
	public int getX(String box) {
		return boxes.get(box)[X];
	}
	public int getY(String box) {
		return boxes.get(box)[Y];
	}
	public int getUp(String box) {
		return boxes.get(box)[UP];
	}
	public int getRight(String box) {
		return boxes.get(box)[RIGHT];
	}
	public int getDown(String box) {
		return boxes.get(box)[DOWN];
	}
	public int getLeft(String box) {
		return boxes.get(box)[LEFT];
	}
	public boolean containsBox(int x, int y) {
		return boxes.containsKey(x*X_SHIFT+y);
	}
}
