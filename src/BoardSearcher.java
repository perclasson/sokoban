import java.util.LinkedList;
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
	
	public static boolean pathExists(State state, Coordinate start, Coordinate goal) {
		if (start.equals(goal)) {
			return true;
		}
		char[][] visited = new char[Main.board.length][Main.board.length * 2];
		Queue<Coordinate> queue = new LinkedList<Coordinate>();
		queue.add(start);
		visited[start.y][start.x] = 'S';
		
		while (!queue.isEmpty()) {
			Coordinate curr = queue.poll();
			if (curr.equals(goal)) {
				return true;
			}
			for (int i = 0; i < Constants.dx.length; i++) {
				Coordinate c = new Coordinate(curr.x + Constants.dx[i], curr.y + Constants.dy[i]);
				if (Main.isFreeSpace(state, c) && visited[c.y][c.x] == '\u0000') {
					queue.add(c);
					visited[c.y][c.x] = 'v';
				}
			}
		}
		return false;
	}
}