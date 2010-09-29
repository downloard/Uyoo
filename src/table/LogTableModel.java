package table;
import java.io.File;

import javax.swing.table.AbstractTableModel;

import data.LogFile;


@SuppressWarnings("serial")
public class LogTableModel extends AbstractTableModel {
	
	private LogFile m_logFile;
	
	public LogTableModel() {
		m_logFile = new LogFile();
	}
	
	public void setFile(File file, String pattern, LogTableFilter filter)
	{	
		int oldGroupSize = m_logFile.getGroupCount();
		
		m_logFile = new LogFile(file);
		m_logFile.updateViewport(pattern, filter);
		
		if (oldGroupSize != m_logFile.getGroupCount())		
			fireTableStructureChanged();
		else
			fireTableDataChanged();
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
