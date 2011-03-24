package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import core.ExceptionHandler;
import core.UyooLogger;

public class LogFile implements Runnable {

	private File                     m_file;
	
	private Vector<ILogFileListener> m_listeners;
	
	private Vector<LogLine> m_lines;
	int                     m_readedLines;
	
	
	private Thread          m_fileWatcherThread;
	private boolean         m_runWorkerThread;
	
	
	public LogFile() {
		UyooLogger.getLogger().debug("ctor "  + this.getClass().getName());
		
		m_listeners = new Vector<ILogFileListener>();
		m_lines    = new Vector<LogLine>();
	}

	public void finalize() throws Throwable {
		closeFile();
	}
	
	public void openFile(File file) {
		
		//do some initialization if new file is not the old file
		if (file != null && (file.equals(m_file) == false)) {
			
			UyooLogger.getLogger().info("Open new file \"" + file.getName() + "\"");

			m_file = file;
					
			m_lines.clear();
			
			//stop old thread if necessary
			closeFile();	
			
			//start new thread
			m_runWorkerThread = true;
			m_fileWatcherThread = new Thread(this, "FileWatcherThread");
			m_fileWatcherThread.start();
			
			fireFileChanged();
		}
	}
	
	private synchronized void closeFile() {
		m_runWorkerThread = false;
		if (m_fileWatcherThread != null) {
			try {
				m_fileWatcherThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				m_fileWatcherThread = null;
			}
		}
	}
	
	@Override
	public void run() {
		UyooLogger.getLogger().debug("File Watcher started");
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(m_file));
			int lineNr = 1;
		
			while (m_runWorkerThread) {
				if (br.ready()) {			 				
				
					//now add the rows
					String nextLine = null;
					while (((nextLine = br.readLine()) != null) && m_runWorkerThread) {						
						LogLine next = new LogLine(nextLine, lineNr);						
						m_lines.add(next);
						
						lineNr++;
					}			
					
					if (m_runWorkerThread) {
						//TODO: data which lines was changed
						fireDataChanged();
					}
					
				} else {
					Thread.sleep(1000);
					//UyooLogger.getLogger().info(".");
				}				
			}
			
			br.close();
			
		} catch (IOException e) {			
			//TODO: maybe reload file
			//      doReset()
			
			e.printStackTrace();
			
		}  catch (InterruptedException e) {
			//Thread Sleep failed
			ExceptionHandler.handleException(e);
		} finally {
			UyooLogger.getLogger().debug("File Watcher finished");
		}
	}
	
	public int getLineCount() {
		if (m_lines != null) {
			return m_lines.size();
		} else {
			return 0;
		}
	}
	
	public LogLine getData(int lineNr) {
		return m_lines.get(lineNr);
	}

	public File getFile() {
		return m_file;
	}
	
	private void fireDataChanged() {	
		//UyooLogger.getLogger().debug("fire Data changed");
		for (ILogFileListener next : m_listeners) {
			next.dataAdded();
		}
	}
	
	private void fireFileChanged() {		
		UyooLogger.getLogger().debug("fireFileChanged");
		
		for (ILogFileListener next : m_listeners) {
			next.fileChanged(m_file);
		}
	}
	
	public void addListener(ILogFileListener listener) {
		m_listeners.add(listener);
	}
	
	public void removeListener(ILogFileListener listener) {
		m_listeners.remove(listener);
	}
}
