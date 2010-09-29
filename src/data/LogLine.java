package data;

import java.util.Vector;

public class LogLine {
	private int            m_lineNumber;
	private String 		   m_orgLineString;
	private Vector<String> m_groupedData;
	
	
	public LogLine(String line, int lineNumber) {
		m_lineNumber    = lineNumber;
		m_orgLineString = line; 
	}

	public String getText() {
		return m_orgLineString;
	}
	
	public Vector<String> getGroupedData() {
		return m_groupedData;
	}


	public void setGroupedData(Vector<String> data) {
		m_groupedData = data;
	}

	public int getLineNumber() {
		return m_lineNumber;
	}
	
	public String getGroupText(int groupIndex) {
		return m_groupedData.get(groupIndex);
	}
}
