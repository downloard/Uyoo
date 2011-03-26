package data;

import java.util.HashSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupedLogLine extends LogLine {
	
	private Vector<String>   m_groupedData;
	private HashSet<Integer> m_filterHits;
	
	public GroupedLogLine(LogLine ll) {
		super(ll.getText(), ll.getLineNumber());
		m_filterHits = new HashSet<Integer>();
	}
	
	public String getGroupText(int groupIndex) {
		if (groupIndex < 0  || groupIndex >= m_groupedData.size()) {
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
	
	public void groupData(Pattern pattern, int groupCountPatternShouldContain) {		
		// create group data by pattern
		m_groupedData = new Vector<String>();			
		
		//first column is line number
		//TODO: as group in encapsulated in GroupedLogLine
		//      line number can be extracted from group data vector
		m_groupedData.add("" + getLineNumber());
		
		// pattern defined
		if (pattern != null) {
			Matcher matcher = pattern.matcher( getText() );
			if (matcher.matches()) {
				//row matches pattern, each group is a column
				for (int i=1; i <= matcher.groupCount(); i++) {
					m_groupedData.add(matcher.group(i));
				}
			} else {
				//row does not matches pattern, add whole line
				m_groupedData.add(getText());
				//other columns are empty 
				while (m_groupedData.size() < groupCountPatternShouldContain) {
					m_groupedData.add("");
				}
			}
			
		// no pattern
		} else {
			// whole line into 2. column
			m_groupedData.add( getText() );
		}
	}
}
