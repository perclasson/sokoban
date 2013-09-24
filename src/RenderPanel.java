import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JPanel;


public class RenderPanel extends JPanel{
	
	private char[][] board;
    public RenderPanel()
    {
        setBackground(Color.WHITE);
      setPreferredSize(new Dimension(1000,1000));
        setLayout(null);
    }
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if(board == null)
        	return;
        for(int i = 0; i < board.length; i++) {
        	for(int j = 0; j < board[i].length; j++) {
        		if(board[i][j] == '#') {
        			g.drawImage(new ImageIcon(getClass().getResource("/images/wall.png")).getImage(), 30*j, 30*i, null);
        		} else if(board[i][j] == '.') {
        			g.drawImage(new ImageIcon(getClass().getResource("/images/goal.png")).getImage(), 30*j, 30*i, null);
        		} else if(board[i][j] == ' ') {
        			g.drawImage(new ImageIcon(getClass().getResource("/images/empty.png")).getImage(), 30*j, 30*i, null);
        		} else if(board[i][j] == '@') {
        			g.drawImage(new ImageIcon(getClass().getResource("/images/player.png")).getImage(), 30*j, 30*i, null);
        		} else if(board[i][j] == '$') {
        			g.drawImage(new ImageIcon(getClass().getResource("/images/box.png")).getImage(), 30*j, 30*i, null);
        		} else if(board[i][j] == '*') {
        			g.drawImage(new ImageIcon(getClass().getResource("/images/box_on_goal.png")).getImage(), 30*j, 30*i, null);
        		} else if(board[i][j] == '+') {
        			g.drawImage(new ImageIcon(getClass().getResource("/images/player_on_goal.png")).getImage(), 30*j, 30*i, null);
        		} 
        	}
        }
    }
    
    public void setBoard(char[][] board) {
    	this.board = board;
    	int max = 0;
    	for(int i = 0; i < board.length; i++) {
    		if(board[i].length > max)
    			max = board[i].length;
    	}
        setPreferredSize(new Dimension(max*30,board.length*30));
    }
}
