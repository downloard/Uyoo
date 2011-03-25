package swing.table;

import java.util.HashSet;
import java.util.Vector;

import data.LogLine;

public class TableLogLine extends LogLine {

	private Vector<String>   m_groupedData;
	private HashSet<Integer> m_filterHits;
	
	public TableLogLine(LogLine ll) {
		super(ll.getText(), ll.getLineNumber());
		m_filterHits = new HashSet<Integer>();
	}
	
	public void setGroupDate(Vector<String> data) {
		m_groupedData = data;
	}
	
	public String getGroupText(int groupIndex) {
		if (groupIndex > m_groupedData.size()) {
			return "";
		} else {
			return m_groupedData.get(groupIndex);
		}
	}
	
	public void setGroupIsFilterHit(int index) {
		m_filterHits.add(index);
	}

	public boolean isGroupFilterHit(int index) {
		return m_filterHits.contains(index);
	}

	public int getGroupCount() {
		return m_groupedData.size();
	}
}
