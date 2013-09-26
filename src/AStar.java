import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AStar {

	public static String findPath(GameState state, int startX, int startY,
			int goalX, int goalY) {
		if(startX == goalX && startY == goalY) {
			return "Q";
		}
		
		Node start = new Node(startY, startX);
		Node goal = new Node(goalY, goalX);

		List<Node> closedSet = new ArrayList<Node>();
		List<Node> openSet = new LinkedList<Node>();

		openSet.add(start);
		start.setVisited();
		start.setApproxCost(estimateCost(start, goal));

		while (!openSet.isEmpty()) {
			Node current = openSet.remove(0);
			closedSet.add(current);
			
			if (current.equals(goal)) {
				return reconstructPath(start, current);
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
					n.setApproxCost(tentativeCost + estimateCost(n, goal));
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

	private static List<Node> getNeighbours(GameState state, Node node) {
		List<Node> neighbours = new LinkedList<Node>();
		// check above
		if (Main.isFreeSpace(state, node.getX(), node.getY() - 1)) {
			neighbours.add(new Node(node.getY() - 1, node.getX()));
		}
		// check left
		if (Main.isFreeSpace(state, node.getX() - 1, node.getY())) {
			addOrdered(neighbours, new Node(node.getY(), node.getX() - 1));
		}
		// check right
		if (Main.isFreeSpace(state, node.getX() + 1, node.getY())) {
			addOrdered(neighbours, new Node(node.getY(), node.getX() + 1));
		}
		// check below
		if (Main.isFreeSpace(state, node.getX(), node.getY() + 1)) {
			addOrdered(neighbours, new Node(node.getY() + 1, node.getX()));
		}
		return neighbours;
	}

	private static void addOrdered(List<Node> list, Node node) {
		int size = list.size();
		for (int i = 0; i < size; i++) {
			if (node.compareTo(list.get(i)) <= 0) { // node cost is smaller than or equal to list[i]
				list.add(i, node);
			}
		}
		list.add(node);
	}
}