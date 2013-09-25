import java.util.ArrayList;
import java.util.List;


public class BoxList {
	private static final int X = 0, Y = 1, UP = 2, RIGHT = 3, DOWN = 4, LEFT = 5;
	private List<int[]> boxes;
	
	public BoxList() {
		boxes = new ArrayList<int[]>();
	}
	
	public void addBox(int x, int y) {
		addBox(x, y, 0, 0, 0, 0);
	}
	
	public void addBox(int x, int y, int up, int right, int down, int left) {
		int[] box = {x, y, up, right, down, left};
		boxes.add(box);
	}
	
	public int size() {
		return boxes.size();
	}
	
	public int getX(int box) {
		return boxes.get(box)[X];
	}
	public int getY(int box) {
		return boxes.get(box)[Y];
	}
	public int getUp(int box) {
		return boxes.get(box)[UP];
	}
	public int getRight(int box) {
		return boxes.get(box)[RIGHT];
	}
	public int getDown(int box) {
		return boxes.get(box)[DOWN];
	}
	public int getLeft(int box) {
		return boxes.get(box)[LEFT];
	}
}
