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
        
        for(int j = 0; j < board.length; j++) {
        	for(int i = 0; i < board[0].length; i++) {
        		if(board[i][j] == '#') {
        			g.drawImage(new ImageIcon(getClass().getResource("/images/wall.png")).getImage(), 50*i, 50*j, null);
        		} else if(board[i][j] == '.') {
        			g.drawImage(new ImageIcon(getClass().getResource("/images/goal.png")).getImage(), 50*i, 50*j, null);
        		} else if(board[i][j] == ' ') {
        			g.drawImage(new ImageIcon(getClass().getResource("/images/empty.png")).getImage(), 50*i, 50*j, null);
        		} else if(board[i][j] == '@') {
        			g.drawImage(new ImageIcon(getClass().getResource("/images/player.png")).getImage(), 50*i, 50*j, null);
        		} else if(board[i][j] == '$') {
        			g.drawImage(new ImageIcon(getClass().getResource("/images/box.png")).getImage(), 50*i, 50*j, null);
        		} 
        	}
        }
    }
    
    public void setBoard(char[][] board) {
    	this.board = board;
    }
}
