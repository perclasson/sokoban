
public class Coordinate implements Cloneable{
	public int x, y;
	public Coordinate parent;
	
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Coordinate clone() {
		return new Coordinate(x,y);
	}

	@Override
	public boolean equals(Object c) {
		Coordinate cor = (Coordinate) c;
		if(cor.x == x && cor.y == y)
			return true;
		else
			return false;
	}
}
