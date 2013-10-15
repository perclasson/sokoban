import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Tester {

	private static Scanner scanner;

	public static void main(String[] args) throws FileNotFoundException {
		boolean verbose = false;
		boolean fail = false;
		String source = null;
		for (int i = 0; i < args.length; i++) { // verbose - Print successful maps
			if (args[i].equals("-v")) {
				verbose = true;
			} else if (args[i].equals("-f")) { // stop if one test fails
				fail = true;
			} else {
				source = args[i];
			}
		}
		scanner = new Scanner(new File(source));
		scanner.useDelimiter(";");
		while (scanner.hasNext()) {
			String both = scanner.next();
			String level = both.split("\n")[0];
			String map = both.split("[0-9]+")[1];
			if (verbose) {
				System.out.println(level);
				System.out.println(map);
			}
			Main main = new Main(makeBoard(map));
			String path = main.solve();
			if(verbose) {
				System.out.println(path);
			}
			if(path.equals("No path")) {
				System.out.println();
				System.out.println("FFFFFAAAAAAAIIIIIIIIILLLLLL");
				System.out.println(level);
				System.err.println("MAP:");
				System.out.println(map);
				if(fail) {
					break;
				}
			}
		}

	}

	private static char[][] makeBoard(String map) {
		String[] list = map.split("\n");
		char[][] board = new char[list.length][];
		for (int i = 0; i < list.length; i++) {
			board[i] = list[i].toCharArray();
		}
		return board;
	}

}
