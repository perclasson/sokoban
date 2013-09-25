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
    	int numBoxes= currentState.numberOfBoxes();
    	int index = 0;
        super.paintComponent(g);
        if(board == null)
        	return;
        for(int i = 0; i < board.length; i++) {
        	for(int j = 0; j < board[i].length; j++) {
        		if(board[i][j] == '#') {
        			g.drawImage(new ImageIcon(getClass().getResource("/images/wall.png")).getImage(), 30*j, 30*i, null);
        		} else if(board[i][j] == '.') {
        			if(!(index == numBoxes)) {
	        			if(currentState.getBoxList().getY(index) == i && currentState.getBoxList().getX(index) == j) {
	            			g.drawImage(new ImageIcon(getClass().getResource("/images/box_on_goal.png")).getImage(), 30*j, 30*i, null);
	            			index++;
	        			} else	
	        				g.drawImage(new ImageIcon(getClass().getResource("/images/goal.png")).getImage(), 30*j, 30*i, null);
        			} else
        				g.drawImage(new ImageIcon(getClass().getResource("/images/goal.png")).getImage(), 30*j, 30*i, null);
        		} else if(board[i][j] == ' ') {
        			g.drawImage(new ImageIcon(getClass().getResource("/images/empty.png")).getImage(), 30*j, 30*i, null);
        		}
        	}
        }
        for(int i = index; index < numBoxes; i++) {
			g.drawImage(new ImageIcon(getClass().getResource("/images/box_on_goal.png")).getImage(), 30*currentState.getBoxList().getX(i), 30*currentState.getBoxList().getY(i), null);
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
}
