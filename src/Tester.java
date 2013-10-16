import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Tester {

	private static Scanner scanner;

	public static void main(String[] args) throws FileNotFoundException {
		boolean verbose = true;
		boolean fail = true;
		String source = null;
		boolean chooseLevel = false;
		String chosenLevel = "";
		for (int i = 0; i < args.length; i++) { // verbose - Print successful maps
			if (args[i].equals("-s")) {
				verbose = false;
			} else if (args[i].equals("-c")) { // continue even if a test fails
				fail = false;
			} else if(args[i].equals("-l")){ // choose level
				chooseLevel = true;
				chosenLevel = args[i+1];
			} else if(args[i].equals("-f")){ // source file
				source = args[i+1];
			}
		}
		scanner = new Scanner(new File(source));
		scanner.useDelimiter(";LEVEL ");
		int currentLevel = 0;
		while (scanner.hasNext()) {
			currentLevel++;
			String both = scanner.next();
			String map = both.split("[0-9]+")[1];

			if(chooseLevel && currentLevel != Integer.parseInt(chosenLevel)) {
				continue;
			}
			
			if (verbose) {
				System.out.println("LEVEL: "+currentLevel);
				System.out.println(map);
			}
			
			Main main = new Main(makeBoard(map));
			if(verbose) {
				System.out.println("Solving...");
			}
			String path = main.solve();
			if(verbose) {
				System.out.println("Finished! Path: " + path);
			}
			if(path == null) {
				System.out.println();
				System.out.println("FFFFFAAAAAAAIIIIIIIIILLLLLL");
				System.out.println(currentLevel);
				System.out.println("MAP:");
				System.out.println(map);
				if(fail) {
					break;
				}
			}
			if(chooseLevel) {
				System.exit(0);
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
