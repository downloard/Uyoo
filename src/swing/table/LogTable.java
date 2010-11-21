package swing.table;

import java.awt.Font;

import javax.swing.JTable;


@SuppressWarnings("serial")
public class LogTable extends JTable {
	
	private LogTableModel m_logTableModel;
	
	public LogTable(LogTableModel model) {
		//init table model
		m_logTableModel = model;
		setModel(m_logTableModel);
		
		//set behavior
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setFont(new Font("Courier New", Font.PLAIN, 11));
	}
}
