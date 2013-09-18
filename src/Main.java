import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class Main {

	public static void main(String[] args) {
		new Main();
	}

	private ArrayList<String> board;
	private char[][] path;
	
	public Main() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		board = new ArrayList<String>();
		int matrixSize = 0;
		String line = "";

		try {
			while((line = in.readLine()) != null) {
				board.add(line);
				matrixSize = Math.max(matrixSize, line.length());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		matrixSize = Math.max(matrixSize, board.size());
		path = new char[matrixSize][matrixSize];
		System.out.println(findPath());
	}

	private String findPath() {
		return null;
	}

}
