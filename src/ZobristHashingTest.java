import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;


public class ZobristHashingTest {

	@Test
	public void testSimpleHash() {
		ArrayList<Coordinate> boxes = new ArrayList<Coordinate>();
		Coordinate box1 = new Coordinate(1,1);
		boxes.add(box1);
		Coordinate box2 = new Coordinate(3,3);
		boxes.add(box2);
		Coordinate box3 = new Coordinate(2,4);
		boxes.add(box3);
		Coordinate player = new Coordinate(4,2);
		char[][] board = {{'#','#','#','#','#','#'},
						  {'#','$',' ',' ','@','#'},
						  {'#',' ',' ',' ',' ','#'},
						  {'#',' ',' ','$',' ','#'},
						  {'#',' ',' ',' ',' ','#'},
						  {'#','$',' ',' ',' ','#'},
						  {'#','#','#','#','#','#'}};
		
		ZobristHasher hasher = new ZobristHasher(board);
		int hash = hasher.hash(boxes, player);
		int hash2 = hasher.hash(boxes, player);
		assertEquals(hash, hash2);
		System.out.println("Zobrist hash: " + hash);
	}
	
	@Test
	public void testBasicMove() {
		ArrayList<Coordinate> boxes = new ArrayList<Coordinate>();
		Coordinate box1 = new Coordinate(1,1);
		boxes.add(box1);
		Coordinate box2 = new Coordinate(3,3);
		boxes.add(box2);
		Coordinate box3 = new Coordinate(2,4);
		boxes.add(box3);
		Coordinate player = new Coordinate(4,1);
		char[][] board = {{'#','#','#','#','#','#'},
						  {'#','$',' ',' ','@','#'},
						  {'#',' ',' ',' ','#'},
						  {'#',' ',' ','$',' ','#'},
						  {'#',' ',' ',' ',' ','#'},
						  {'#','$',' ',' ',' ','#'},
						  {'#','#','#','#','#','#'}};
		
		ZobristHasher hasher = new ZobristHasher(board);
		int hash = hasher.hash(boxes, player);
		int hash2 = hasher.movePlayer(hash, 4, 1, 3, 1);
		assertNotSame(hash2, hash);
		System.out.println("Hash: " + hash);
		System.out.println("Hash2: " + hash2);
		assertTrue(hash != hash2);
		int hash3 = hasher.movePlayer(hash2, 3, 1, 4, 1);
		System.out.println("Hash: " + hash);
		System.out.println("Hash3: " + hash3);
		assertEquals(hash, hash3);
	}

	@Test
	public void testAdvancedMove() {
		ArrayList<Coordinate> boxes = new ArrayList<Coordinate>();
		Coordinate box1 = new Coordinate(1,1);
		boxes.add(box1);
		Coordinate box2 = new Coordinate(3,3);
		boxes.add(box2);
		Coordinate box3 = new Coordinate(2,4);
		boxes.add(box3);
		Coordinate player = new Coordinate(4,3);
		char[][] board = {{'#','#','#','#','#','#'},
						  {'#','$',' ',' ',' ','#'},
						  {'#',' ',' ',' ',' ','#'},
						  {'#',' ','@','$',' ','#'},
						  {'#',' ',' ',' ',' ','#'},
						  {'#','$',' ',' ',' ','#'},
						  {'#','#','#','#','#','#'}};
		
		ZobristHasher hasher = new ZobristHasher(board);
		int initialHash = hasher.hash(boxes, player);
		int hashMove = hasher.moveBox(initialHash, 3, 3, 2, 3);
		hashMove = hasher.movePlayer(hashMove, 4, 3, 3, 3); //Move left
		hashMove = hasher.movePlayer(hashMove, 3, 3, 3, 4); //Move Down
		hashMove = hasher.movePlayer(hashMove, 3, 4, 2, 4); //Move left
		hashMove = hasher.movePlayer(hashMove, 2, 4, 1, 4); //move ledt
		hashMove = hasher.movePlayer(hashMove, 1, 4, 1, 3); //Move up
		hashMove = hasher.moveBox(hashMove, 2, 3, 3, 3); //Move box right
		hashMove = hasher.movePlayer(hashMove, 1, 3, 2, 3); //Move left
		hashMove = hasher.movePlayer(hashMove, 2, 3, 2, 2); //Move up --
		hashMove = hasher.movePlayer(hashMove, 2, 2, 3, 2); //Move right
		hashMove = hasher.movePlayer(hashMove, 3, 2, 4, 2); //Move right
		hashMove = hasher.movePlayer(hashMove, 4, 2, 4, 3); //Move Down

		assertEquals(hashMove, initialHash);
		hashMove = hasher.movePlayer(hashMove, 4, 3, 4, 4); //Move Down
		assertTrue(hashMove != initialHash);
		hashMove = hasher.movePlayer(hashMove, 4, 4, 4, 3); //Move Down
		assertEquals(hashMove, initialHash);


		
	}
}
