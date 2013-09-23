import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JPanel;


public class RenderPanel extends JPanel{
	
	private char[][] board;
    public RenderPanel()
    {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(600,400));
        setLayout(null);
    }
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if(board == null)
        	return;
        setPreferredSize(new Dimension(board.length*30,board[0].length*30));

        for(int j = 0; j < board.length; j++) {
        	for(int i = 0; i < board[i].length; i++) {
        		if(board[i][j] == '#') {
        			g.drawImage(new ImageIcon(getClass().getResource("/images/wall.png")).getImage(), 30*i, 30*j, null);
        		} else if(board[i][j] == '.') {
        			g.drawImage(new ImageIcon(getClass().getResource("/images/goal.png")).getImage(), 30*i, 30*j, null);
        		} else if(board[i][j] == ' ') {
        			g.drawImage(new ImageIcon(getClass().getResource("/images/empty.png")).getImage(), 30*i, 30*j, null);
        		} else if(board[i][j] == '@') {
        			g.drawImage(new ImageIcon(getClass().getResource("/images/player.png")).getImage(), 30*i, 30*j, null);
        		} else if(board[i][j] == '$') {
        			g.drawImage(new ImageIcon(getClass().getResource("/images/box.png")).getImage(), 30*i, 30*j, null);
        		} else if(board[i][j] == '*') {
        			g.drawImage(new ImageIcon(getClass().getResource("/images/box_on_goal.png")).getImage(), 30*i, 30*j, null);
        		} else if(board[i][j] == '+') {
        			g.drawImage(new ImageIcon(getClass().getResource("/images/player_on_goal.png")).getImage(), 30*i, 30*j, null);
        		} 
        	}
        }
    }
    
    public void setBoard(char[][] board) {
    	this.board = board;
    }
}
