package swing.table;

import java.util.Vector;

import data.LogLine;

public class TableLogLine extends LogLine {

	private Vector<String> m_groupedData;
	
	public TableLogLine(LogLine ll) {
		super(ll.getText(), ll.getLineNumber());
	}
	
	public void setGroupDate(Vector<String> data) {
		m_groupedData = data;
	}
	
	public Vector<String> getGroupedData() {
		return m_groupedData;
	}
	
	public String getGroupText(int groupIndex) {
		if (groupIndex > m_groupedData.size()) {
			return "";
		} else {
			return m_groupedData.get(groupIndex);
		}
	}
}
