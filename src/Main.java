import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class Main {

	public static void main(String[] args) {
		new Main();
	}

	private char[][] board;
	
	public Main() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		List<String> tmpBoard = new ArrayList<String>();
		int matrixSize = 0;
		String line = "";

		try {
			while((line = in.readLine()) != null) {
				tmpBoard.add(line);
				matrixSize = Math.max(matrixSize, line.length());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		matrixSize = Math.max(matrixSize, tmpBoard.size());
		board = new char[matrixSize][matrixSize];
		
		for(int i = 0 ; i < tmpBoard.size() ; i++) {
			board[i] = tmpBoard.get(i).toCharArray();
		}
		
		System.out.println(findPath());
	}

	private String findPath() {
		return null;
	}
	
	private boolean freeSpace(int x, int y) {
		return board[x][y] != ' ' && board[x][y] != '.' && board[x][y] != '@';
	}
	
	private boolean isStuck(int x, int y) {
		if ((freeSpace(x-1, y) && freeSpace(x+1, y)) ||
				(freeSpace(x, y-1) && freeSpace(x, y+1))) {
			return false;
		}
		return true;
	}

}
