package table;
import java.io.File;
import java.util.Date;
import java.util.Timer;

import javax.swing.table.AbstractTableModel;

import core.FileWatcher;
import data.LogFile;


@SuppressWarnings("serial")
public class LogTableModel extends AbstractTableModel {
	
	private static final int AUTORELOAD_PERIOD = 1000;
	
	private LogFile     m_logFile;
	private Timer       m_fileWatcher;
	
	private String         m_currentPattern;
	private LogTableFilter m_currentFilter;
	
	
	public LogTableModel() {
		m_logFile = new LogFile();
	}
	
	public void setFile(File file, String pattern, LogTableFilter filter, boolean autoReload)
	{	
		stopAutoReload();
		
		m_currentFilter = filter;
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

	public void startAutoreload() {
		stopAutoReload();
		m_fileWatcher = new Timer();
		FileWatcher worker = new FileWatcher(m_logFile.getFile()) {
			@Override
			protected void onChange(File file) {
				m_logFile.reloadFile();
				m_logFile.updateViewport(m_currentPattern, m_currentFilter);
				fireTableDataChanged();
			}
		};
		m_fileWatcher.schedule(worker, new Date(), AUTORELOAD_PERIOD);
	}	
	
	public void stopAutoReload() {
		if (m_fileWatcher != null) {
			m_fileWatcher.cancel();
			m_fileWatcher = null;
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
