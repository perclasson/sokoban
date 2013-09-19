import javax.swing.JFrame;

public class RenderFrame extends JFrame{
	private RenderPanel panel;
	
	public RenderFrame() { 
        super("Sokoban");
        setVisible(true);
        panel = new RenderPanel();
        add(panel);
       // add(new renderPanel); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        pack(); 
        setLocationRelativeTo(null); 
        setResizable(false); 
        setVisible(true); 
    } 
	
	public void renderBoard(char[][] board) {
		panel.setBoard(board);
		panel.repaint();
	}
}
