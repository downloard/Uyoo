package data;

public class LogLine {
	private int            m_lineNumber;
	private String 		   m_text;
	
	
	public LogLine(String line, int lineNumber) {
		m_lineNumber    = lineNumber;
		m_text = line; 
	}

	public String getText() {
		return m_text;
	}

	public int getLineNumber() {
		return m_lineNumber;
	}
}
