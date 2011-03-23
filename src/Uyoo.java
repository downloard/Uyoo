import javax.swing.UIManager;

import swing.MainFrame;
import core.UyooLogger;

public class Uyoo {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		UyooLogger.getLogger().info("Starting Uyoo");
		
		try { 
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() ); 
		} catch( Exception e ) { 
			e.printStackTrace(); 
		}
		
		new MainFrame().setVisible(true);
	}
}
