import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BoardSearcher {

	public static String findPath(State state, Coordinate start, Coordinate goal) {
		if (start.equals(goal)) {
			return "";
		}
		char[][] visited = new char[Main.board.length][Main.board.length * 2];
		Queue<Coordinate> queue = new LinkedList<Coordinate>();
		queue.add(start);
		visited[start.y][start.x] = 'S';
		
		while (!queue.isEmpty()) {
			Coordinate curr = queue.poll();
			if (curr.equals(goal)) {
				return reconstruct(start, goal, visited);
			}
			for (int i = 0; i < Constants.dx.length; i++) {
				Coordinate c = new Coordinate(curr.x + Constants.dx[i], curr.y + Constants.dy[i]);
				if (Main.isFreeSpace(state, c) && visited[c.y][c.x] == '\u0000') {
					queue.add(c);
					if (Constants.dx[i] > 0) {
						visited[c.y][c.x] = 'R';
					} else if (Constants.dx[i] < 0) {
						visited[c.y][c.x] = 'L';
					} else if (Constants.dy[i] > 0) {
						visited[c.y][c.x] = 'D';
					} else if(Constants.dy[i] < 0){
						visited[c.y][c.x] = 'U';
					} else {
						System.err.println("This should never happen");
						System.exit(1);
					}
				}
			}
		}
		return null;
	}

	private static String reconstruct(Coordinate start, Coordinate goal, char[][] visited) {
		Coordinate curr = goal;
		StringBuilder sb = new StringBuilder();
		while (!curr.equals(start)) {
			sb.append(visited[curr.y][curr.x]).append(' ');
			switch (visited[curr.y][curr.x]) {
			case 'L':
				curr = new Coordinate(curr.x + 1, curr.y);
				break;
			case 'R':
				curr = new Coordinate(curr.x - 1, curr.y);
				break;
			case 'D': 
				curr = new Coordinate(curr.x, curr.y - 1);
				break;
			case 'U': 
				curr = new Coordinate(curr.x, curr.y + 1);
				break;
			}
		}
		return sb.toString();
	}

	public static String findPathASTAR(State state, Coordinate start, Coordinate goal) {
		if (start.x == goal.x && start.y == goal.y) {
			return "";
		}

		Node startNode = new Node(start);
		Node goalNode = new Node(goal);

		List<Node> closedSet = new ArrayList<Node>();
		List<Node> openSet = new LinkedList<Node>();

		openSet.add(startNode);
		startNode.setVisited();
		startNode.setApproxCost(estimateCost(startNode, goalNode));

		while (!openSet.isEmpty()) {
			Node current = openSet.remove(0);
			closedSet.add(current);

			if (current.equals(goalNode)) {
				return reconstructPath(startNode, current);
			}
			List<Node> neighbours = getNeighbours(state, current);

			for (Node n : neighbours) {
				int tentativeCost = current.getCost() + 1;
				if (closedSet.contains(n) && tentativeCost >= n.getCost()) {
					continue;
				}
				if (!n.isVisited() || tentativeCost < n.getCost()) {
					n.setVisited();
					n.setParent(current);
					n.setCost(tentativeCost);
					n.setApproxCost(tentativeCost + estimateCost(n, goalNode));
					if (!openSet.contains(n)) {
						addOrdered(openSet, n);
					}
				}
			}
		}

		return null;
	}

	private static int estimateCost(Node start, Node goal) {
		int dX = Math.abs(start.getX() - goal.getX());
		int dY = Math.abs(start.getY() - goal.getY());
		return dX + dY;
	}

	private static String reconstructPath(Node start, Node goal) {
		Node current = goal;
		StringBuilder path = new StringBuilder();
		while (!current.equals(start)) {
			if (current.getParent().getX() > current.getX()) {
				path.append("L ");
			} else if (current.getParent().getX() < current.getX()) {
				path.append("R ");
			} else if (current.getParent().getY() < current.getY()) {
				path.append("D ");
			} else if (current.getParent().getY() > current.getY()) {
				path.append("U ");
			}
			current = current.getParent();
		}
		return path.toString();
	}

	private static List<Node> getNeighbours(State state, Node node) {
		List<Node> neighbours = new LinkedList<Node>();
		// check above
		if (Main.isFreeSpace(state, new Coordinate(node.getX(), node.getY() - 1))) {
			neighbours.add(new Node(node.getX(), node.getY() - 1));
		}
		// check left
		if (Main.isFreeSpace(state, new Coordinate(node.getX() - 1, node.getY()))) {
			addOrdered(neighbours, new Node(node.getX() - 1, node.getY()));
		}
		// check right
		if (Main.isFreeSpace(state, new Coordinate(node.getX() + 1, node.getY()))) {
			addOrdered(neighbours, new Node(node.getX() + 1, node.getY()));
		}
		// check below
		if (Main.isFreeSpace(state, new Coordinate(node.getX(), node.getY() + 1))) {
			addOrdered(neighbours, new Node(node.getX(), node.getY() + 1));
		}
		return neighbours;
	}

	private static void addOrdered(List<Node> list, Node node) {
		int size = list.size();
		for (int i = 0; i < size; i++) {
			if (node.compareTo(list.get(i)) <= 0) { // node cost is smaller than
				// or equal to list[i]
				list.add(i, node);
			}
		}
		list.add(node);
	}

	private static class Node extends Coordinate implements Comparable<Node> {
		private int cost;
		private int approxCost;
		private Node parent;
		private boolean visited;

		public Node(int x, int y) {
			super(x, y);
			cost = 0;
			approxCost = 0;
			visited = false;
		}

		public Node(Coordinate c) {
			this(c.x, c.y);
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
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Node)) {
				return false;
			}
			if (((Node) obj).x == this.x && ((Node) obj).y == this.y) {
				return true;
			}
			return false;
		}

		@Override
		public int compareTo(Node n) {

			if (this.approxCost < n.getApproxCost()) {
				return -1;
			}
			if (this.approxCost > n.getApproxCost()) {
				return 1;
			}
			return 0;
		}
	}

}