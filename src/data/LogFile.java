package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import core.UyooLogger;

public class LogFile {

	private File m_file;
	
	private Vector<LogLine> m_lines;
	private Vector<LogLine> m_viewport;
	
	private Vector<ILogFileListener> m_listeners;

	private int      m_groupCount;

	private boolean  m_isCaseSensitive;
	
	
	public LogFile() {
		UyooLogger.getLogger().debug("ctor "  + this.getClass().getName());
		m_listeners = new Vector<ILogFileListener>();
	}

	public void readFile(File file) {
		//init data
		if (false == file.equals(m_file)) {
			m_groupCount = 0;
		}
		m_lines = new Vector<LogLine>();
		
		//now load
		m_file = file;
		reloadFile();
	}
	
	public void reloadFile() {
		m_lines.clear();
		
		try {			
			BufferedReader br = new BufferedReader(new FileReader(m_file));
			
			//now add the rows
			String nextLine = null;
			for (int lineNr=1; (nextLine = br.readLine()) != null; lineNr++) {
				LogLine next = new LogLine(nextLine, lineNr);
				m_lines.add(next);
			}				
			
			br.close();
		} catch (Exception e) {
			//TODO: handle exception
			e.printStackTrace();
			return;
		}
	}
	
	public void updateViewport(String pattern, LogFileFilter filter) {
		int oldGroupCount = getGroupCount();
		
		//reset
		m_groupCount = 1;
		m_viewport = new Vector<LogLine>();
		
		//check pattern
		Pattern p = Pattern.compile(pattern);
		Matcher m = null;
		
		//first set group count as column count
		//TODO: annahme dass erst zeile dem pattern matched
		if (m_lines.size() > 0) {
			LogLine firstLine = m_lines.get(0);
			m = p.matcher(firstLine.getText());
			if (m.matches()) {
				m_groupCount = m.groupCount();
				m_groupCount++; //first column is line counter
			}
			
			//check filter column not to high
			if (filter != null && filter.getColumn() >= m_groupCount) {
				JOptionPane.showMessageDialog(null, "Illegal column in filter", "Error", JOptionPane.ERROR_MESSAGE);
				throw new RuntimeException("Filter column is to high"); //TODO: exception handling
			}
		}
		
		//now add matching lines
		for (int lineNr=0; lineNr < m_lines.size(); lineNr++) {
			LogLine nextLine = m_lines.get(lineNr);
			Vector<String> groupedData = new Vector<String>(m_groupCount);
			
			groupedData.add("" + (lineNr+1));
			m = p.matcher(nextLine.getText());
			if (m.matches()) {
				//row matches pattern, each group is a column
				for (int i=1; i <= m.groupCount(); i++) {
					groupedData.add(m.group(i));
				}
			} else {
				//row does not matches pattern, add whole line
				groupedData.add(nextLine.getText());
				//other columns are empty
				for (int i=1; i < m_groupCount; i++) {
					groupedData.add("");
				}
			}
			
			if (filter == null) {
				//add line to viewport
				nextLine.setGroupedData(groupedData);
				m_viewport.add(nextLine);
			} else if (m.matches() == false)  {
				//nop
			} else {
				//if filter matches too -> add to viewport
				String contenCell = groupedData.get(filter.getColumn());
				boolean contains = false;
				if (m_isCaseSensitive) {
					contains = contenCell.contains( filter.getText() );
				} else {
					contains = contenCell.toUpperCase().contains( filter.getText().toUpperCase() );
				}
				if (contains) {
					nextLine.setGroupedData(groupedData);
					m_viewport.add(nextLine);
				}
			}
		}			
		
		//inform listeners
		{
			if (oldGroupCount != getGroupCount()) {	
				fireStructureChanged();
			} else {
				fireDataChanged();
			}
		}
	}

	public int getGroupCount() {
		return m_groupCount;
	}
	
	public int getLineCount() {
		if (m_viewport != null) {
			return m_viewport.size();
		} else {
			return 0;
		}
	}
	
	public String getData(int line, int group) {
		return m_viewport.get(line).getGroupText(group);
	}

	public File getFile() {
		return m_file;
	}
	
	private void fireDataChanged() {		
		for (ILogFileListener next : m_listeners) {
			next.dataChanged();
		}
	}

	private void fireStructureChanged() {
		UyooLogger.getLogger().debug("fireStructureChanged");
		
		for (ILogFileListener next : m_listeners) {
			next.structureChanged();
		}
	}
	
	public void addListener(ILogFileListener listener) {
		m_listeners.add(listener);
	}
	
	public void removeListener(ILogFileListener listener) {
		m_listeners.remove(listener);
	}

	public boolean isSearchCaseSensitive() {
		return m_isCaseSensitive;
	}

	public void setSearchCaseSensitive(boolean sensetive) {
		m_isCaseSensitive = sensetive;
	}
}
