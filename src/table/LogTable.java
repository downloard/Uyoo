package table;

import java.io.File;

import javax.swing.JTable;


@SuppressWarnings("serial")
public class LogTable extends JTable {
	
	private LogTableModel m_logTableModel;
	
	public LogTable(LogTableModel model) {
		//init table model
		m_logTableModel = model;
		setModel(m_logTableModel);
		
		
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}

	public void setFile(File file, String pattern, LogTableFilter tf, boolean autoReload)
	{
		m_logTableModel.setFile(file, pattern, tf, autoReload);
	}
}
