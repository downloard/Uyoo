package data;

import java.io.File;

/**
 * 
 * Interface for receiving changes from a log file
 *
 */
public interface ILogFileListener {
	
	/**
	 * New data available in log file
	 */
	public void dataAdded();
	
	/**
	 * 
	 */
	public void fileChanged(File newFile);
}
