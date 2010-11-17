package table;
import java.io.File;

import javax.swing.table.AbstractTableModel;

import core.FileWatcher;
import data.LogFile;


@SuppressWarnings("serial")
public class LogTableModel extends AbstractTableModel {
	
	private LogFile        m_logFile;
	
	private String         m_currentPattern;
	private LogTableFilter m_currentFilter;

	private FileWatcher    m_watcher;
	
	
	public LogTableModel() {
		m_logFile = new LogFile();
	}
	
	public void setFile(File file, String pattern, LogTableFilter filter, boolean autoReload)
	{	
		stopAutoReload();
		
		m_currentFilter  = filter;
		m_currentPattern = pattern;
		
		int oldGroupSize = m_logFile.getGroupCount();
		
		m_logFile = new LogFile();
		m_logFile.readFile(file);
		m_logFile.updateViewport(pattern, filter);
		
		if (oldGroupSize != m_logFile.getGroupCount()) {	
			fireTableStructureChanged();
		} else {
			fireTableDataChanged();
		}
		
		if (autoReload) {
			startAutoreload();
		}
	}

	public synchronized void startAutoreload() {
		stopAutoReload();
		
		if (m_logFile.getFile() == null) {
			return;
		}
		
		m_watcher = new FileWatcher(m_logFile.getFile()) {
			@Override
			protected void onChange(File file) {
				m_logFile.reloadFile();
				m_logFile.updateViewport(m_currentPattern, m_currentFilter);
				fireTableDataChanged();
			}
		};
		m_watcher.start();
	}	
	
	public synchronized void stopAutoReload() {
		if (m_watcher != null) {
			m_watcher.stop();
		}
	}
	
	@Override
	public int getColumnCount() {
		return m_logFile.getGroupCount();	
	}

	@Override
	public int getRowCount() {
		return m_logFile.getLineCount();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return m_logFile.getData(rowIndex, columnIndex);
	}
}
