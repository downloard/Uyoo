package data;


public class LogFileFilter {
	
	public static final int ALL_COLUMNS = -1;
	
	private int     m_column;
	private String  m_text;
	
	public LogFileFilter(String text) throws IllegalArgumentException {
		if (text.startsWith("\"")) {
			if (text.endsWith("\"")) {
				m_text = text.substring(1, text.length()-1);
				m_column = ALL_COLUMNS;
			} else {
				throw new IllegalArgumentException();
			}
		} else {
			String[] arr = text.split(":");
			m_column = Integer.parseInt(arr[0]);
			m_text = arr[1];
		}
	}

	public int getColumn() {
		return m_column;
	}

	public String getText() {
		return m_text;
	}	
	
	public boolean matchesFilter(GroupedLogLine line, boolean caseSensitive) {
		boolean result = false;
		
		if (this.getColumn() == LogFileFilter.ALL_COLUMNS) {
			for (int iNextGroup=0; iNextGroup < line.getGroupCount(); iNextGroup++) {
				boolean matchResult = matchesTextInGroup(line, getText(), iNextGroup, caseSensitive);
				if (matchResult == true) {
					result = true;
					line.setGroupIsFilterHit( iNextGroup );
				}
			}
		} else {
			result = matchesTextInGroup(line, getText(), getColumn(), caseSensitive);			
			if (result == true) {
				line.setGroupIsFilterHit( getColumn() );
			}
		}
		
		return result;
	}
	
	private boolean  matchesTextInGroup(GroupedLogLine line, String text, int indexToCheck, boolean caseSensitive) {
		boolean result;
		String contenCell = line.getGroupText( indexToCheck);
		if (caseSensitive) {
			result = contenCell.contains( text );
		} else {
			result = contenCell.toLowerCase().contains( text.toLowerCase() );
		}
		
		if (result == true) {
			line.setGroupIsFilterHit( indexToCheck );
		}
		return result;
	}

	
	@Override
	public String toString() {
		return "" + m_column + ":" + m_text;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LogFileFilter == false) {
			return false;
		}
		
		LogFileFilter other = (LogFileFilter) obj;
		if (other.m_column != this.m_column) {
			return false;
		}
		
		if (other.m_text.equals(this.m_column) == false) {
			return false;
		}

		return true;
	}
}
