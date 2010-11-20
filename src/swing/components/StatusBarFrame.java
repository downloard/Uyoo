package swing.components;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.util.concurrent.Semaphore;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.BevelBorder;

import core.ExceptionHandler;;

@SuppressWarnings("serial")
public class StatusBarFrame extends JFrame {

	private JPanel             userStuff;
	private Container          statusBar;
	
	private JLabel             lblRight;
	private JLabel             lblMiddle;
	private JLabel             lblLeft;
    
    private JProgressBar       progressBar;
    
    private ShowHeadThread     gcThread;
	
	public StatusBarFrame(){
		super();
		init();
	}
	
	public StatusBarFrame(String title){
		super(title);
		init();
	}
	
	public void showHeapSize() {
		gcThread = new ShowHeadThread(this);
		gcThread.start();
	}
	
	private void init() {
		userStuff = new JPanel();	
		userStuff.setLayout(new BorderLayout());
		
		statusBar = getStatusBar();
		
		Container c = super.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(BorderLayout.CENTER, userStuff);
		c.add(BorderLayout.SOUTH, statusBar);
	}
	
	private JPanel getStatusBar() {
		JPanel result = new JPanel();
		
		lblLeft = new JLabel(" "); //first frame does not have hight 0
		lblLeft.setBorder(new BevelBorder(BevelBorder.LOWERED));
		lblMiddle = new JLabel();
		lblMiddle.setBorder(new BevelBorder(BevelBorder.LOWERED));
		lblRight = new JLabel();
		lblRight.setBorder(new BevelBorder(BevelBorder.LOWERED));
		
		result.setLayout(new GridLayout(1, 4));
		result.add(lblLeft, 0);
		result.add(lblMiddle, 1);
		result.add(lblRight, 2);
		
		return result;
	}
	
	public Container getContentPane() {
		return userStuff;
	}
	
	public void setContentPane(Container contentPane) {
		userStuff.add(contentPane, BorderLayout.CENTER);
	}
	
	public void setRightText(String text) {
		
		if (text == null || text.equals("")) {
			lblRight.setText(" ");
		} else {
			lblRight.setText(" " + text);
		}
	}

	public void setMidlleText(String text) {
		
		if (text == null || text.equals("")) {
			lblMiddle.setText(" ");
		} else {
			lblMiddle.setText(" " + text);
		}
	}

	public void setLeftText(String text) {
		
		if (text == null || text.equals("")) {
			lblLeft.setText(" ");
		} else {
			lblLeft.setText(" " + text);
		}
	
	}
    
    //TODO: could be two times initializes
    public void startProgressBar() {
    	if (progressBar == null) {
    		progressBar = new JProgressBar();
    	}
    	
    	progressBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
        progressBar.setStringPainted(true);
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setValue(0);
        
        statusBar.add(progressBar, 3);
        this.validate();
        this.repaint();
        
        
    }
    
    public void endProgressBar() {
        if ((gcThread == null) || (!gcThread.isAlive()))
        {
        	statusBar.remove(progressBar);
	        this.validate();
	        this.repaint();
        }
    }
    
    public JProgressBar getProgressBar() {
        return progressBar;
    }
    
    /**
     * Shows the current used application heap in
     */
    class ShowHeadThread extends Thread {
    	private StatusBarFrame  parent;
    	private boolean         running;
    	private Semaphore	    lockGuard;
    	
    	public ShowHeadThread(StatusBarFrame frame) {
			this.parent = frame;
			this.running = true;
			
			
			//	use this to block on loading
			lockGuard = new Semaphore(1);
		}
    	
    	/**
    	 * Note: could block the caller
    	 */
    	public void pauseUpdates() {
    		try {
    			lockGuard.acquire();
    		} catch (Exception e) {
    			ExceptionHandler.handleException(e);
			}
    	}
    	
    	public void resumeUpdates() {
    		lockGuard.release();
    	}
    	
    	public void stopRunning() {
    		running = false;
    	}
    	
    	@Override
    	public void run() {
    		
    		parent.startProgressBar();
			JProgressBar pb = parent.getProgressBar();
    		
    		while (running) {			
    			//lock 
    			try {
    				lockGuard.acquire();
    			} catch(InterruptedException ex) {
    				ExceptionHandler.handleException(ex);
    				break;
    			}
    			
    			long freeMB  = Runtime.getRuntime().freeMemory() / 1000000;
    			long totalMB = Runtime.getRuntime().totalMemory() / 1000000;
    			
    			pb.setString("Memory: " 
    					     + freeMB  + "MB" 
    					     + "/" 
    					     + totalMB + "MB");
    			
    			pb.setValue( (int) (freeMB*100/totalMB) );
    			
    			try {
    				lockGuard.release();
    				Thread.sleep(1000);
    			} catch (Exception ex) {
    				ExceptionHandler.handleException(ex);
    				break;
    			} finally {
    				try {
    					lockGuard.release();
    				} catch (Exception ex) {
    					//eat ex here
    				}
    			}
    		}
    		
    		parent.endProgressBar();
    	}
    }
}
