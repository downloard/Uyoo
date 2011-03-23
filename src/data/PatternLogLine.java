package data;

import java.util.Vector;

public class PatternLogLine extends LogLine {

	private Vector<String> m_groupedData;
	
	public PatternLogLine(LogLine ll) {
		super(ll.getText(), ll.getLineNumber());
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
