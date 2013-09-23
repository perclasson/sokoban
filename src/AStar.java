import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AStar {

	public static String findPath(char[][] board, int startY, int startX,
			int goalY, int goalX) {
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
			List<Node> neighbours = getNeighbours(board, current);
			
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
		return "fail";
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
		path.deleteCharAt(path.length() - 1);
		return path.reverse().toString();
	}

	private static List<Node> getNeighbours(char[][] board, Node node) {
		List<Node> neighbours = new LinkedList<Node>();
		// check above
		if (node.getY() >= 0
				&& isValidMove(board[node.getY() - 1][node.getX()])) {
			neighbours.add(new Node(node.getY() - 1, node.getX()));
		}
		// check left
		if (node.getX() >= 0
				&& isValidMove(board[node.getY()][node.getX() - 1])) {
			addOrdered(neighbours, new Node(node.getY(), node.getX() - 1));
		}
		// check right
		if (node.getX() < board[node.getY()].length - 1
				&& isValidMove(board[node.getY()][node.getX() + 1])) {
			addOrdered(neighbours, new Node(node.getY(), node.getX() + 1));
		}
		// check below
		if (node.getY() < board.length - 1
				&& isValidMove(board[node.getY() + 1][node.getX()])) {
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
	
	private static boolean isValidMove(char node) {
		if(node == Main.SPACE || node == Main.GOAL) {
			return true;
		} else {
			return false;
		}
	}
	
	
	
	
	
	
	
//////////////<TEST SUPPORT>  ////////////////////
//////////////<TEST SUPPORT>  ////////////////////	
//////////////<TEST SUPPORT>  ////////////////////
////////////// <TEST SUPPORT>  ////////////////////
	
	public static void main(String[] args) {

		BufferedReader in = getBufferedReader();
		List<String> tmpBoard = readBoard(in);

		char[][] board = new char[tmpBoard.size()][];

		for (int i = 0; i < tmpBoard.size(); i++) {
			board[i] = tmpBoard.get(i).toCharArray();
		}
		System.out.println(findPath(board, 1, 1, 2, 6));
		
	/*	System.out.println(findPath(board, Integer.parseInt(args[0]),
				Integer.parseInt(args[1]), Integer.parseInt(args[2]),
				Integer.parseInt(args[3])));*/
	}

	private static BufferedReader getBufferedReader() {
			try {
				return new BufferedReader(new FileReader("test"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		return new BufferedReader(new InputStreamReader(System.in));
	}

	private static List<String> readBoard(BufferedReader in) {
		List<String> board = new ArrayList<String>();
		String line = null;

		try {
			while ((line = in.readLine()) != null && line.charAt(0) != ';') {
				board.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return board;
	}
	
	/////////////////// </TEST SUPPORT> ///////////////////
	
}