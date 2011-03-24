package swing.table;

import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


@SuppressWarnings("serial")
public class LogTable extends JTable {
	
	private LogTableModel m_logTableModel;
	private LogTableRenderer m_renderer;
	
	public LogTable(LogTableModel model) {
		//init table model
		m_logTableModel = model;
		setModel(m_logTableModel);
		
		m_renderer = new LogTableRenderer(model);
		
		//set behavior
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setFont(new Font("Courier New", Font.PLAIN, 11));
	}
	
	@Override
	public TableCellRenderer getDefaultRenderer(Class<?> columnClass) {
		return m_renderer;
	}
}
