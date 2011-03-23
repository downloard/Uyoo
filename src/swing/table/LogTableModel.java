package swing.table;
import java.io.File;

import javax.swing.table.AbstractTableModel;

import core.UyooLogger;
import data.ILogFileListener;
import data.LogFile;
import data.LogFileFilter;
import data.LogLine;
import data.LogViewport;


@SuppressWarnings("serial")
public class LogTableModel extends AbstractTableModel implements ILogFileListener {
	
	private LogFile          m_logFile;
	
	private String           m_configuredPattern;
	private boolean          m_isSearchCaseSensitive;
	private LogFileFilter    m_filter;
	
	private LogViewport      m_visibleRows;
	private int     	     m_readedLines;
	
	
	
	public LogTableModel() {
		m_visibleRows = new LogViewport();
		m_readedLines = 0;
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
			return m_logFile.getGroupCount();	
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
		clearData();
		updateData();
		fireTableStructureChanged();
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
		UyooLogger.getLogger().debug("Set pattern to " + pattern);
		m_configuredPattern = pattern;		
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
