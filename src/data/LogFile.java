package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import core.FileWatcher;
import core.UyooLogger;

public class LogFile {
	
	private FileWatcher    m_watcher;

	private File m_file;
	
	private Vector<LogLine> m_lines;
	private Vector<LogLine> m_viewport;
	
	private Vector<ILogFileListener> m_listeners;

	private int      m_groupCount;

	private boolean  m_isCaseSensitive;

	private boolean m_autoReload;
	
	private String         m_currentPattern;
	private LogFileFilter  m_currentFilter;
	
	
	public LogFile() {
		UyooLogger.getLogger().debug("ctor "  + this.getClass().getName());
		m_listeners = new Vector<ILogFileListener>();
	}

	public void readFile(File file) {
		//init data
		if (false == file.equals(m_file)) {
			m_groupCount = 0;
		}
		m_lines = new Vector<LogLine>();
		
		//now load
		m_file = file;
		reloadFile();
	}
	
	public void reloadFile() {
		m_lines.clear();
		
		try {			
			BufferedReader br = new BufferedReader(new FileReader(m_file));
			
			//now add the rows
			String nextLine = null;
			for (int lineNr=1; (nextLine = br.readLine()) != null; lineNr++) {
				LogLine next = new LogLine(nextLine, lineNr);
				m_lines.add(next);
			}				
			
			br.close();
		} catch (Exception e) {
			//TODO: handle exception
			e.printStackTrace();
			return;
		}
	}
	
	public void updateViewport() {
		int oldGroupCount = getGroupCount();
		
		//reset
		m_groupCount = 1;
		m_viewport = new Vector<LogLine>();
		
		//check pattern
		Pattern p = Pattern.compile(m_currentPattern);
		Matcher m = null;
		
		//first set group count as column count
		//TODO: annahme dass erst zeile dem pattern matched
		if (m_lines.size() > 0) {
			LogLine firstLine = m_lines.get(0);
			m = p.matcher(firstLine.getText());
			if (m.matches()) {
				m_groupCount = m.groupCount();
				m_groupCount++; //first column is line counter
			}
			
			//check filter column not to high
			//TODO: is this the right point?
			//      maybe new file is selected and patter/fiilter was also update
			if (m_currentFilter != null && m_currentFilter.getColumn() >= m_groupCount) {
				JOptionPane.showMessageDialog(null, "Illegal column in filter", "Error", JOptionPane.ERROR_MESSAGE);
				throw new RuntimeException("Filter column is to high"); //TODO: exception handling
			}
		}
		
		//now add matching lines
		for (int lineNr=0; lineNr < m_lines.size(); lineNr++) {
			LogLine nextLine = m_lines.get(lineNr);
			Vector<String> groupedData = new Vector<String>(m_groupCount);
			
			groupedData.add("" + (lineNr+1));
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
		
		//inform listeners
		{
			if (oldGroupCount != getGroupCount()) {	
				fireStructureChanged();
			} else {
				fireDataChanged();
			}
		}
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
		for (ILogFileListener next : m_listeners) {
			next.dataChanged();
		}
	}
	
	private void fireFileChanged() {		
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

	public void startAutoReload() {
		stopAutoReload();
		
		if (m_file == null) {
			return;
		}
		
		UyooLogger.getLogger().debug("starting auto reload");
		m_watcher = new FileWatcher(m_file) {
			@Override
			protected void onChange(File file) {
				reloadFile();
				updateViewport();
				
				fireFileChanged();
			}
		};
		m_watcher.start();
	}

	public void stopAutoReload() {
		if (m_watcher != null) {
			UyooLogger.getLogger().debug("stopping auto reload");
			m_watcher.stop();
		}		
	}

	public void setAutoreload(boolean autoreload) {
		UyooLogger.getLogger().debug("Set autoreload to " + autoreload);
		m_autoReload = autoreload;
		
		//TODO: maybe refactor - do not start every time if already running
		if (autoreload) {
			startAutoReload();
		} else {
			stopAutoReload();
		}		
	}
	
	public void openFile() {
		openFile(m_file);
	}

	public void openFile(File file) {
		UyooLogger.getLogger().info("Open file \"" + file + "\"");
		
		m_file = file;
		
		stopAutoReload();	
		
		readFile(file);
		updateViewport();
		
		fireFileChanged();
		
		if (m_autoReload) {
			startAutoReload();
		}
		
	}

	public void setFile(File filename) {
		m_file = filename;		
	}
	
	public void setSelectedPattern(Object pattern) {
		UyooLogger.getLogger().debug("Set pattern to " + pattern);
		m_currentPattern = pattern.toString();		
	}

	public void setSelectedFilter(LogFileFilter tf) {
		UyooLogger.getLogger().debug("Set filter to " + tf);
		m_currentFilter = tf;	
	}
	
	public String getSelectedPattern() {
		return m_currentPattern;	
	}

	public LogFileFilter getSelectedFilter() {
		return m_currentFilter;	
	}

	public boolean isAutoReload() {
		return m_autoReload;
	}
}
