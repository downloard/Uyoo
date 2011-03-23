package swing.table;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.table.AbstractTableModel;

import core.UyooLogger;
import data.ILogFileListener;
import data.LogFile;
import data.LogFileFilter;
import data.LogLine;
import data.LogViewport;


@SuppressWarnings("serial")
public class LogTableModel extends AbstractTableModel implements ILogFileListener {
	
	private static int DEFAULT_GROUP_COUNT = 2;
	
	private LogFile          m_logFile;
	
	private String           m_configuredPattern;
	private Matcher			 m_patternMatcher;
	private int 			 m_currentGroupCount;
	
	private boolean          m_isSearchCaseSensitive;
	private LogFileFilter    m_filter;
	
	private LogViewport      m_visibleRows;
	private int     	     m_readedLines;
	
	
	
	public LogTableModel() {
		m_visibleRows = new LogViewport();
		m_readedLines = 0;
		m_currentGroupCount = DEFAULT_GROUP_COUNT;
		m_patternMatcher = null;
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
	
	private void updateData() {	
		if (m_logFile == null) {
			return;
		}
		
		//iterate over all lines
		int lines = m_logFile.getLineCount();
		for (; m_readedLines < lines; m_readedLines++) {
			LogLine l = m_logFile.getData(m_readedLines);
			boolean contains = false;
			
			//check 
			if (m_filter == null) {
				contains = true;
			} else {
				if (m_isSearchCaseSensitive) {
					contains = l.getText().contains( m_filter.getText() );
				} else {
					contains = l.getText().toUpperCase().contains( m_filter.getText().toUpperCase() );
				}
			}
			
			if (contains == true) {
				m_visibleRows.add(l);
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
		LogLine line = m_visibleRows.get(rowIndex);
	
		switch (columnIndex)
		{
			case 0:
				return "" + line.getLineNumber();
			case 1:
				return line.getText();
			default:
				return "<Error>";
		}
	}
	
	@Override
	public String getColumnName(int column) {
		return "" + column;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	public void structureChanged() {
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
			Pattern p        = Pattern.compile(m_configuredPattern);
			m_patternMatcher = null;
			
			//test max. first 10 lines
			//TODO: make it configurable
			int maxLinesToTest = 5;
			if (maxLinesToTest > m_logFile.getLineCount()) {
				maxLinesToTest = m_logFile.getLineCount();
			}
			
			for (int i=0; i < maxLinesToTest; i++) {
				LogLine firstLine = m_logFile.getData(i);
				m_patternMatcher = p.matcher(firstLine.getText());
				if (m_patternMatcher.matches()) {
					m_currentGroupCount = m_patternMatcher.groupCount();
					m_currentGroupCount++; //first column is line counter
					bFound = true;
					break;
				}
			}
		}
		
		if (bFound == false) {
			UyooLogger.getLogger().error("Initial pattern missmatch");
			m_patternMatcher = null;
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
