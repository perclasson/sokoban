
public class GameState {
	private BoxList boxList;
	public int x, y;
	
	public GameState(BoxList boxList) {
		this.boxList = boxList;
	}
	
	@Override
	public int hashCode() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0 ; i < boxList.size() ; i++) {
			sb.append(boxList.getX(i)).append(',').append(boxList.getY(i)).append(' ');
		}
		return sb.toString().hashCode();
	}
	
}
