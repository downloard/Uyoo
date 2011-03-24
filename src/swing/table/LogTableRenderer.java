package swing.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class LogTableRenderer  extends DefaultTableCellRenderer {

	private LogTableModel m_tableModel;
	private Color         m_bgColorHighlighted;
	private Color         m_bgColorDefault;

	public LogTableRenderer(LogTableModel model) {
		m_tableModel = model;
		m_bgColorHighlighted = new Color(255,255,165);
		m_bgColorDefault     = new Color(255,255,255);
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		TableLogLine line = m_tableModel.getVisibleRows().get(row);
		if (line.isGroupFilterHit(column)) {
			c.setBackground(m_bgColorHighlighted);
		} else {
			c.setBackground(m_bgColorDefault);
		}
		
		return c;
	}
}
