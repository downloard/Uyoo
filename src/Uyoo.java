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
		
//		try {
//			BufferedReader br = new BufferedReader(new FileReader("build.xml"));
//			
//			//now add the rows
//			String nextLine = null;
//			while (true) {//int lineNr=1; (nextLine = br.readLine()) != null; lineNr++) {
//				nextLine = br.readLine();
//				if (nextLine != null) {
//					System.out.println(nextLine);
//				} else {
//					Thread.sleep(100);
//				}
//			}				
//			
//			//br.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
