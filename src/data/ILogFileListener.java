package data;

/**
 * 
 * Interface for receiving changes from a log file
 *
 */
public interface ILogFileListener {
	/**
	 * Structure of log file data changed (e.g. group count changed)
	 */
	public void structureChanged();
	
	/**
	 * New data available in log file
	 */
	public void dataChanged();
}