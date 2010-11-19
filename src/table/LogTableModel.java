package table;
import javax.swing.table.AbstractTableModel;

import data.ILogFileListener;
import data.LogFile;


@SuppressWarnings("serial")
public class LogTableModel extends AbstractTableModel implements ILogFileListener {
	
	private LogFile        m_logFile;
	
	public LogTableModel() {
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
			return m_logFile.getLineCount();
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return m_logFile.getData(rowIndex, columnIndex);
	}

	@Override
	public void structureChanged() {
		fireTableStructureChanged();
	}

	@Override
	public void dataChanged() {
		fireTableDataChanged();
	}
}
