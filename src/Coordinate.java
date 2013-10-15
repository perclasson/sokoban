
public class Coordinate {
	public int x, y;
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public int hashCode() {
		return 100000*x+y;
	}

	@Override
	public boolean equals(Object o) {
		return o.hashCode() == hashCode();
	}
	
	@Override
	public Coordinate clone() {
		return new Coordinate(x,y);
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
