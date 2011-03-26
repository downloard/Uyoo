package swing.table;
import java.io.File;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.table.AbstractTableModel;

import core.UyooLogger;
import data.GroupedLogLine;
import data.ILogFileListener;
import data.LogFile;
import data.LogFileFilter;
import data.LogLine;


@SuppressWarnings("serial")
public class LogTableModel extends AbstractTableModel implements ILogFileListener {
	
	private static int DEFAULT_GROUP_COUNT = 2;
	
	private LogFile          m_logFile;
	
	private String           m_configuredPattern;
	private Pattern			 m_pattern;
	private int 			 m_currentGroupCount;
	
	private boolean          m_keepFilteredLines;
	private boolean          m_isSearchCaseSensitive;
	private LogFileFilter    m_filter;
	
	private Vector<GroupedLogLine> m_visibleRows;
	private int     	         m_readedLines;
	
	
	
	public LogTableModel() {
		m_visibleRows = new Vector<GroupedLogLine>();
		m_readedLines = 0;
		m_currentGroupCount = DEFAULT_GROUP_COUNT;
		m_pattern = null;
		
		m_isSearchCaseSensitive = false;
		m_keepFilteredLines     = false;
	}
	
	public LogTableModel(LogFile logFile) {
		setLogFile(logFile);
	}
	
	public void setLogFile(LogFile logFile) {
		//remove listener from old log file
		if (m_logFile != null) {
			m_logFile.removeListener(this);
		}
		
		//new log file
		m_logFile = logFile;
		logFile.addListener(this);
	}
	
	public Vector<GroupedLogLine> getVisibleRows() {
		return m_visibleRows;
	}
	
	private void updateData() {	
		if (m_logFile == null) {
			return;
		}
		
		//iterate over all lines
		int lines = m_logFile.getLineCount();
		for (; m_readedLines < lines; m_readedLines++) {
			
			GroupedLogLine nextLogLine = new GroupedLogLine( m_logFile.getData(m_readedLines) );
			nextLogLine.groupData(m_pattern, m_currentGroupCount);
			
			//check filter
			boolean contains = false;
			if (m_filter == null) {
				contains = true;
			} else if (m_filter.getColumn() >= m_currentGroupCount) {
				// filter column to high
				contains = true;
			} else {
				contains = m_filter.matchesFilter(nextLogLine, m_isSearchCaseSensitive);
			}
			
			// finaly check if line should be visible
			if (contains == true || m_keepFilteredLines) {
				m_visibleRows.add(nextLogLine);
			}
		}
	}
	
	private void clearData() {
		m_visibleRows.clear();
		m_readedLines = 0;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
			
	@Override
	public int getColumnCount() {
		if (m_logFile == null) {
			return 0;
		} else {
			return m_currentGroupCount;	
		}
	}

	@Override
	public int getRowCount() {
		if (m_logFile == null) {
			return 0;
		} else {
			return m_visibleRows.size();
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		GroupedLogLine line = m_visibleRows.get(rowIndex);
		return line.getGroupText(columnIndex);
	}
	
	@Override
	public String getColumnName(int column) {
		return "" + column;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	public void structureChanged() {
		UyooLogger.getLogger().debug("Structure changed");
		clearData();
		updateData();
		fireTableStructureChanged();
	}
	
	public void dataChanged() {
		clearData();
		updateData();
		fireTableDataChanged();
	}

	@Override
	public void dataAdded() {
		int alreadyReaded = m_visibleRows.size();
		updateData();
		fireTableRowsInserted(alreadyReaded, m_visibleRows.size()-1);
	}
	
	@Override
	public void fileChanged(File newFile) {
		calculateGroupCountByPattern();
		structureChanged();
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	public boolean isSearchCaseSensitive() {
		return m_isSearchCaseSensitive;
	}

	public void setSearchCaseSensitive(boolean sensetive) {
		UyooLogger.getLogger().debug("Search is case sensitive: " + sensetive);
		if (m_isSearchCaseSensitive != sensetive) {
			m_isSearchCaseSensitive = sensetive;
			
			dataChanged();
		}
	}
	
	public boolean isKeepFilteredLines() {
		return m_keepFilteredLines;
	}

	public void setKeepFilteredLines(boolean keep) {
		UyooLogger.getLogger().debug("Keep filtered lines: " + keep);
		if (m_keepFilteredLines != keep) {
			m_keepFilteredLines = keep;
			
			dataChanged();
		}
	}
	
	public void setSelectedPattern(String pattern) {
		if ((pattern == null) || (pattern.equals(m_configuredPattern) == false)) {
			UyooLogger.getLogger().debug("Set pattern to " + pattern);
			
			int oldGroupCount = m_currentGroupCount;
			m_configuredPattern = pattern;
			
			//calculate new group count
			calculateGroupCountByPattern();
			
			if (oldGroupCount == m_currentGroupCount) {
				dataChanged();
			} else {
				structureChanged();
			}
		}
	}

	private void calculateGroupCountByPattern() {
		boolean bFound = false;
		
		if (m_logFile != null) {
			m_pattern = Pattern.compile(m_configuredPattern);
			Matcher matcher = null;
			
			//test max. first 10 lines
			//TODO: make it configurable
			int maxLinesToTest = 10;
			if (maxLinesToTest > m_logFile.getLineCount()) {
				maxLinesToTest = m_logFile.getLineCount();
			}
			
			for (int i=0; i < maxLinesToTest; i++) {
				LogLine firstLine = m_logFile.getData(i);
				matcher = m_pattern.matcher(firstLine.getText());
				if (matcher.matches()) {
					m_currentGroupCount = matcher.groupCount();
					m_currentGroupCount++; //first column is line counter
					bFound = true;
					break;
				}
			}
		}
		
		if (bFound == false) {
			UyooLogger.getLogger().error("Initial pattern missmatch");
			m_pattern = null;
			m_currentGroupCount = DEFAULT_GROUP_COUNT;
		}
	}
	
	public String getSelectedPattern() {
		return m_configuredPattern;	
	}
	
	public void setSelectedFilter(LogFileFilter tf) {		
		if ((tf == null) || (tf.equals(m_filter) == false)) {
			UyooLogger.getLogger().debug("Set filter to " + tf);
			
			m_filter = tf;
			
			dataChanged();
		}
	}

	public LogFileFilter getSelectedFilter() {
		return m_filter;	
	}
}
