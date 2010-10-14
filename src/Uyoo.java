import javax.swing.UIManager;

public class Uyoo {

	public static final String PROGRAM_NAME = "Uyoo";
	public static final String VERSION      = Uyoo.class.getPackage().getImplementationVersion();
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try { 
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() ); 
		} catch( Exception e ) { 
			e.printStackTrace(); 
		}
		
		new MainFrame(PROGRAM_NAME + " - " + VERSION).setVisible(true);
	}
}
