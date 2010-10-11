package core;

import java.io.File;
import java.util.TimerTask;

/*
 * HOW TO USE
 * 
 * public static void main(String args[]) {
 *   // monitor a single file
 *   TimerTask task = new FileWatcher( new File("c:/temp/text.txt") ) {
 *     protected void onChange( File file ) {
 *       // here we code the action on a change
 *       System.out.println( "File "+ file.getName() +" have change !" );
 *     }
 *   };
 *
 *   Timer timer = new Timer();
 *   // repeat the check every second
 *   timer.schedule( task , new Date(), 1000 );
 * }
 */

public abstract class FileWatcher extends TimerTask {
	private long m_timeStamp;
	private File m_file;

	public FileWatcher(File file) {
		setFile(file);
	}
	
	public FileWatcher() {
	}
	
	public void setFile(File file) {
		this.m_file = file;
		this.m_timeStamp = file.lastModified();
	}

	public final void run() {
		long timeStamp = m_file.lastModified();

		if (this.m_timeStamp != timeStamp) {
			this.m_timeStamp = timeStamp;
			onChange(m_file);
		}
	}

	protected abstract void onChange(File file);
}
