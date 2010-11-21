package data;

public class LogFileFilter {
	int m_column;
	String m_text;
	
	public LogFileFilter(String text) throws IllegalArgumentException {
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
	
	@Override
	public String toString() {
		return "" + m_column + ":" + m_text;
	}
}
