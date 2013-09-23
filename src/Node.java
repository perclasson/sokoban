
public class Node implements Comparable<Node>{
	
	private final int x;
	private final int y;
	private int cost;
	private int approxCost;
	private Node parent;
	private boolean visited;
	
	public Node(int y, int x) {
		this.x = x;
		this.y = y;
		cost = 0;
		approxCost = 0;
		visited = false;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	public Node getParent() {
		return parent;
	}
	
	public void setCost(int cost) {
		this.cost = cost;
	}
	
	public int getCost() {
		return cost;
	}
	
	public boolean isVisited() {
		return visited;
	}
	
	public void setVisited() {
		visited = true;
	}
	
	public void setApproxCost(int cost) {
		approxCost = cost;
	}
	
	public int getApproxCost() {
		return approxCost;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(!(obj instanceof Node)) {
			return false;
		} 
		if(((Node)obj).x == this.x && ((Node)obj).y == this.y) {
			return true;
		}
		return false;
	}

	@Override
	public int compareTo(Node n) {
		
		if(this.approxCost < n.getApproxCost()) {
			return -1;
		}
		if(this.approxCost > n.getApproxCost()) {
			return 1;
		}
		return 0;
	}
}
