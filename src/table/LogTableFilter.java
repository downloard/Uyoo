package table;

public class LogTableFilter {

	int m_column;
	String m_text;
	
	public LogTableFilter(String text) {
		String[] arr = text.split(":");
		m_column = Integer.parseInt(arr[0]);
		m_text = arr[1];
	}

	public int getColumn() {
		return m_column;
	}

	public String getText() {
		return m_text;
	}	
}
