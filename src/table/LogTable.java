package table;

import java.io.File;

import javax.swing.JTable;


@SuppressWarnings("serial")
public class LogTable extends JTable {
	
	LogTableModel m_logTableModel;
	
	public LogTable() {
		//init table model
		m_logTableModel = new LogTableModel();
		setModel(m_logTableModel);
		
		
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}

	public void setFile(File file, String pattern, LogTableFilter tf)
	{
		m_logTableModel.setFile(file, pattern, tf);
	}
}
