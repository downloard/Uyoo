package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.ExceptionHandler;
import core.UyooLogger;

public class LogFile implements Runnable {

	private File                     m_file;
	
	private Vector<ILogFileListener> m_listeners;
	private LogViewport              m_viewport;
	
	String 					m_usedPattern;
	private String          m_configuredPattern;
	private LogFileFilter   m_currentFilter;
	private boolean         m_isCaseSensitive;	
	private int             m_groupCount;
	
	private Vector<LogLine> m_lines;
	int                     m_readedLines;
	
	
	private Thread          m_fileWatcherThread;
	private boolean         m_runWorkerThread;
	
	
	public LogFile() {
		UyooLogger.getLogger().debug("ctor "  + this.getClass().getName());
		m_listeners = new Vector<ILogFileListener>();
		
		m_lines    = new Vector<LogLine>();
		m_viewport = new LogViewport();
	}

	public void finalize() throws Throwable {
		closeFile();
	}
	
	public void openFile(File file) {
		
		//do some initialization if new file is not the old file
		if (file != null && (file.equals(m_file) == false)) {
			
			UyooLogger.getLogger().info("Open new file \"" + file.getName() + "\"");

			m_file = file;
						
			//m_fileLastModified = m_file.lastModified();
			m_groupCount = 1;
			m_usedPattern = null;
			m_lines.clear();
			
			//stop old thread if necessary
			closeFile();
			
			//clear data
			clearViewport();	
			
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
		
			while (m_runWorkerThread) {
				if (br.ready()) {			 				
				
					//now add the rows
					String nextLine = null;
					for (int lineNr=1; ((nextLine = br.readLine()) != null) && m_runWorkerThread; lineNr++) {
						LogLine next = new LogLine(nextLine, lineNr);
						m_lines.add(next);
					}			
					
					if (m_runWorkerThread) {
						updateViewport();
					}
					
				} else {
					Thread.sleep(1000);
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
	
	public void updateViewport() {
		int oldGroupCount = m_groupCount;
		
		//update pattern first time
		if (m_usedPattern == null) {
			m_usedPattern = m_configuredPattern;
			
		//use new pattern
		} else if (m_usedPattern.equals(m_configuredPattern) == false) {
			m_usedPattern = m_configuredPattern;
			
			clearViewport();
		}
		//TODO: may be no pattern possible
		
		Pattern p = Pattern.compile(m_usedPattern);
		Matcher m = null;
		
		//calculate new group count - using first line
		//TODO: on pattern change viewport and group count has to be update
		if (m_readedLines == 0) {
			LogLine firstLine = m_lines.get(0);
			m = p.matcher(firstLine.getText());
			if (m.matches()) {
				//try to use an other line if not work
				m_groupCount = m.groupCount();
				m_groupCount++; //first column is line counter
			} else {
				UyooLogger.getLogger().error("Initial pattern missmatch");
			}
		}
				
		for (; m_readedLines < m_lines.size(); m_readedLines++) {
			LogLine nextLine = m_lines.get(m_readedLines);
			Vector<String> groupedData = new Vector<String>(m_groupCount);
			
			groupedData.add("" + (m_readedLines+1));
			m = p.matcher(nextLine.getText());
			if (m.matches()) {
				//row matches pattern, each group is a column
				for (int i=1; i <= m.groupCount(); i++) {
					groupedData.add(m.group(i));
				}
			} else {
				//row does not matches pattern, add whole line
				groupedData.add(nextLine.getText());
				//other columns are empty
				for (int i=1; i < m_groupCount; i++) {
					groupedData.add("");
				}
			}
			
			if (m_currentFilter == null) {
				//add line to viewport
				nextLine.setGroupedData(groupedData);
				m_viewport.add(nextLine);
			} else if (m.matches() == false)  {
				//nop
			} else {
				//if filter matches too -> add to viewport
				String contenCell = groupedData.get(m_currentFilter.getColumn());
				boolean contains = false;
				if (m_isCaseSensitive) {
					contains = contenCell.contains( m_currentFilter.getText() );
				} else {
					contains = contenCell.toUpperCase().contains( m_currentFilter.getText().toUpperCase() );
				}
				if (contains) {
					nextLine.setGroupedData(groupedData);
					m_viewport.add(nextLine);
				}
			}
		}
		
		if (oldGroupCount != getGroupCount()) {	
			fireStructureChanged();
		} else {
			fireDataChanged();
		}
	}

	private void clearViewport() {
		m_readedLines = 0;
		m_viewport.clear();
	}
	
	public int getGroupCount() {
		return m_groupCount;
	}
	
	public int getLineCount() {
		if (m_viewport != null) {
			return m_viewport.size();
		} else {
			return 0;
		}
	}
	
	public String getData(int line, int group) {
		return m_viewport.get(line).getGroupText(group);
	}

	public File getFile() {
		return m_file;
	}
	
	private void fireDataChanged() {	
		//UyooLogger.getLogger().debug("fire Data changed");
		for (ILogFileListener next : m_listeners) {
			next.dataChanged();
		}
	}
	
	private void fireFileChanged() {		
		UyooLogger.getLogger().debug("fireFileChanged");
		
		for (ILogFileListener next : m_listeners) {
			next.fileChanged(m_file);
		}
	}

	private void fireStructureChanged() {
		UyooLogger.getLogger().debug("fireStructureChanged");
		
		for (ILogFileListener next : m_listeners) {
			next.structureChanged();
		}
	}
	
	public void addListener(ILogFileListener listener) {
		m_listeners.add(listener);
	}
	
	public void removeListener(ILogFileListener listener) {
		m_listeners.remove(listener);
	}

	public boolean isSearchCaseSensitive() {
		return m_isCaseSensitive;
	}

	public void setSearchCaseSensitive(boolean sensetive) {
		m_isCaseSensitive = sensetive;
	}
	
	public void setSelectedPattern(Object pattern) {
		UyooLogger.getLogger().debug("Set pattern to " + pattern);
		m_configuredPattern = pattern.toString();		
	}

	public void setSelectedFilter(LogFileFilter tf) {
		UyooLogger.getLogger().debug("Set filter to " + tf);
		m_currentFilter = tf;	
	}
	
	public String getSelectedPattern() {
		return m_configuredPattern;	
	}

	public LogFileFilter getSelectedFilter() {
		return m_currentFilter;	
	}
}
