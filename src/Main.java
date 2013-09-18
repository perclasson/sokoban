import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class Main {

	public static void main(String[] args) {
		new Main();
	}

	private ArrayList<String> board;
	
	public Main() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		board = new ArrayList<String>();

		try {
			while(in.ready()) {
				board.add(in.readLine());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(findPath());
	}

	private String findPath() {
		return null;
	}

}
