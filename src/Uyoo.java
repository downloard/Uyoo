import javax.swing.UIManager;

import core.UyooLogger;

import swing.MainFrame;

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
