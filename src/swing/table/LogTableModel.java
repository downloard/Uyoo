package swing.table;
import java.io.File;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import core.UyooLogger;
import data.ILogFileListener;
import data.LogFile;
import data.LogFileFilter;
import data.LogLine;


@SuppressWarnings("serial")
public class LogTableModel extends AbstractTableModel implements ILogFileListener {
	
	private LogFile          m_logFile;
	
	private String           m_configuredPattern;
	private boolean          m_isSearchCaseSensitive;
	private LogFileFilter    m_filter;
	
	private Vector<LogLine>  m_visibleRows;
	
	
	
	public LogTableModel() {
		m_visibleRows = new Vector<LogLine>();
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
		//clear table data
		m_visibleRows.clear();
		
		//iterate over all lines
		int lines = m_logFile.getLineCount();
		for (int i=0; i < lines; i++) {
			LogLine l = m_logFile.getData(i);
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

	@Override
	public void structureChanged() {
		updateData();
		fireTableStructureChanged();
	}

	@Override
	public void dataChanged() {
		updateData();
		fireTableDataChanged();
	}
	
	@Override
	public void fileChanged(File newFile) {
		updateData();
		fireTableStructureChanged();
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	public boolean isSearchCaseSensitive() {
		return m_isSearchCaseSensitive;
	}

	public void setSearchCaseSensitive(boolean sensetive) {
		UyooLogger.getLogger().debug("Search is case sensitive: " + sensetive);
		m_isSearchCaseSensitive = sensetive;
	}
	
	public void setSelectedPattern(String pattern) {
		UyooLogger.getLogger().debug("Set pattern to " + pattern);
		m_configuredPattern = pattern;		
	}
	
	public String getSelectedPattern() {
		return m_configuredPattern;	
	}
	
	public void setSelectedFilter(LogFileFilter tf) {
		m_filter = tf;
	}

	public LogFileFilter getSelectedFilter() {
		//TODO: umbau
		throw new NotImplementedException();	
	}
}
