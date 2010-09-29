import javax.swing.UIManager;

public class Uyoo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		try { 
		  UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() ); 
		} catch( Exception e ) { 
			e.printStackTrace(); 
		}
		
		new MainFrame().setVisible(true);
	}	
}
