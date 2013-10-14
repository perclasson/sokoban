import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JPanel;


public class RenderPanel extends JPanel{
	
	private char[][] board;
	GameState currentState;
    public RenderPanel()
    {
        setBackground(Color.WHITE);
      setPreferredSize(new Dimension(1000,1000));
        setLayout(null);
    }
    public void paintComponent(Graphics g)
    {
    	if(currentState == null) {
    		System.out.println("SLUTA SKICKA IN NULL TILL MIG NÄR JAG RENDERAR!!!");
    		return;
    	}
//    	BoxList boxList = currentState.getBoxes();
        super.paintComponent(g);
        if(board == null)
        	return;
        for(int i = 0; i < board.length; i++) {
        	for(int j = 0; j < board[i].length; j++) {
        		if(board[i][j] == '#') {
        			g.drawImage(new ImageIcon(getClass().getResource("/images/wall.png")).getImage(), 30*j, 30*i, null);
        		} else if(board[i][j] == '.') {
//        			if(boxList.containsBox(j, i)) {
//	            			g.drawImage(new ImageIcon(getClass().getResource("/images/box_on_goal.png")).getImage(), 30*j, 30*i, null);
//	        			} else if(currentState.x == j && currentState.y == i) {
//	            			g.drawImage(new ImageIcon(getClass().getResource("/images/player.png")).getImage(), 30*j, 30*i, null);
//	        			}else	
//	        				g.drawImage(new ImageIcon(getClass().getResource("/images/goal.png")).getImage(), 30*j, 30*i, null);
//        		} else if(board[i][j] == ' ') {
//        			if(boxList.containsBox(j, i)) {
//            			g.drawImage(new ImageIcon(getClass().getResource("/images/box.png")).getImage(), 30*j, 30*i, null);
//        			} else if(currentState.x == j && currentState.y == i) {
//            			g.drawImage(new ImageIcon(getClass().getResource("/images/player.png")).getImage(), 30*j, 30*i, null);
//        			}
//        			else
//        			g.drawImage(new ImageIcon(getClass().getResource("/images/empty.png")).getImage(), 30*j, 30*i, null);
        		}
        	}
        }
  
    	
    }
    
    public void setBoard(char[][] board, GameState state) {
    	this.board = board;
    	currentState = state;
    	int max = 0;
    	for(int i = 0; i < board.length; i++) {
    		if(board[i].length > max)
    			max = board[i].length;
    	}
        setPreferredSize(new Dimension(max*30,board.length*30));
    }
    
    void printInTerminal(GameState gs) {
//    	BoxList bl = gs.getBoxes();
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[i].length; j++) {
//				if(bl.containsBox(j, i)) {
//					if(board[i][j] == '.') {
//						System.out.print('*');
//					} else {
//						System.out.print("$");
//					}
//				} else if(gs.x == j && gs.y == i) {
//					if(board[i][j] == '.') {
//						System.out.print('+');
//					} else {
//						System.out.print("@");
//					}
//				} else {
//					System.out.print(board[i][j]);
//				}
			}
			System.out.println();
		}
    }
    
}
