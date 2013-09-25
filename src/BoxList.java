import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;


public class BoxList implements Cloneable {
	public static final int X = 0, Y = 1, UP = 2, RIGHT = 3, DOWN = 4, LEFT = 5;
	private Map<Integer, int[]> boxes;
	public static final int X_SHIFT = 10000;
	
	public BoxList() {
		boxes = new TreeMap<Integer, int[]>();
	}
	
	public BoxList(Map<Integer, int[]> boxes) {
		this.boxes = boxes;
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
	
	public int getX(Integer box) {
		return boxes.get(box)[X];
	}
	public int getY(Integer box) {
		return boxes.get(box)[Y];
	}
	public int getUp(Integer box) {
		return boxes.get(box)[UP];
	}
	public int getRight(Integer box) {
		return boxes.get(box)[RIGHT];
	}
	public int getDown(Integer box) {
		return boxes.get(box)[DOWN];
	}
	public int getLeft(Integer box) {
		return boxes.get(box)[LEFT];
	}
	public boolean containsBox(int x, int y) {
		return boxes.containsKey(x*X_SHIFT+y);
	}
	
	public String getDirectionString(Integer box) {
		String res = "";
		res += getUp(box) != 0 ? 1 : 0 + getRight(box) != 0 ? 1 : 0 + getDown(box) != 0 ? 1 : 0 + getLeft(box) != 0 ? 1 : 0;
		return res;
	}
	
	public Object clone() {
		Map<Integer, int[]> newMap = new TreeMap<Integer, int[]>();
		for (Entry<Integer, int[]> e : getEntrySet())
			newMap.put(e.getKey(), e.getValue());
		return new BoxList(newMap);
	}
}