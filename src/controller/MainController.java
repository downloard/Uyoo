package controller;

import generated.Settings.Files.F;
import generated.Settings.Pattern.P;

import java.io.File;
import java.util.List;

import javax.swing.JOptionPane;

import setup.UyooSettings;
import swing.MainFrame;
import core.FileWatcher;
import core.UyooLogger;
import data.LogFile;
import data.LogFileFilter;

public class MainController {

	private LogFile        m_logFile;
	private File           m_selectedFile;

	private String         m_currentPattern;
	private LogFileFilter  m_currentFilter;

	private FileWatcher    m_watcher;
	
	private MainFrame      m_gui;
	
	private boolean        m_autoReload;

	
	public MainController(MainFrame gui) {
		m_gui = gui;
		
		m_selectedFile = null;
		m_autoReload   = false;
		m_logFile      = new LogFile();
	}

	public LogFile getLogFile() {
		return m_logFile;
	}
	
	public void setSelectedFile(String filename) {
		UyooLogger.getLogger().debug("Set selected file: " + filename);
		m_selectedFile = new File(filename);		
	}
	
	public void setSelectedFile(File file) {
		assert(file != null);
		m_selectedFile = file;
	}
	
	public File getSelectedFile() {
		return m_selectedFile;
	}

	public void selectAndAddFile() {
		File selectedFile = m_gui.selectFile();
		if (selectedFile != null) {
			m_selectedFile = selectedFile;
			
			saveSelectedFile();
			
			openFile();
		}
	}

	/**
	 * Adds new file to persistent settings and GUI drop down
	 */
	private void saveSelectedFile() {
		//add file to settings
		//TODO: move this code to settings
		List<F> files = UyooSettings.getInstance().getPersistentSettings().getFiles().getF();
		//check if already in persistent data
		boolean needToAdd = true;
		for(F next : files) {
			File nextFile = new File(next.getValue());
			if (nextFile.equals(m_selectedFile)) {
				needToAdd = false;
				break;
			}
		}
		
		if (needToAdd == true) {
			//add to settings
			F f = new F();
			f.setValue(m_selectedFile.getAbsolutePath());
			files.add(f);

			//save persistent
			UyooSettings.getInstance().saveConfigFile();
			
			//inform gui
			m_gui.updateSettings();
		}
	}
	
	public void openFile() {
		if (m_selectedFile == null) {
			JOptionPane.showMessageDialog(m_gui, "No file selected");
			return;
		}	
		
		UyooLogger.getLogger().info("Open file \"" + m_selectedFile + "\"");
		
		stopAutoReload();	
		
		m_logFile.readFile(m_selectedFile);
		m_logFile.updateViewport(m_currentPattern, m_currentFilter);
		m_gui.updateFileInformation(m_selectedFile);
		
		if (m_autoReload) {
			startAutoreload();
		}
	}
	
	public boolean isAutoReload() {
		return m_autoReload;
	}

	public synchronized void startAutoreload() {
		stopAutoReload();
		
		if (m_logFile.getFile() == null) {
			return;
		}
		
		UyooLogger.getLogger().debug("starting auto reload");
		m_watcher = new FileWatcher(m_logFile.getFile()) {
			@Override
			protected void onChange(File file) {
				UyooLogger.getLogger().debug("File '" + file.getName() + "' changed");
				m_logFile.reloadFile();
				m_logFile.updateViewport(m_currentPattern, m_currentFilter);
				m_gui.updateFileInformation(m_selectedFile);
			}
		};
		m_watcher.start();
	}	
	
	public synchronized void stopAutoReload() {
		if (m_watcher != null) {
			UyooLogger.getLogger().debug("stopping auto reload");
			m_watcher.stop();
		}
	}

	public void setAutoreload(boolean autoreload) {
		UyooLogger.getLogger().debug("Set autoreload to " + autoreload);
		m_autoReload = autoreload;
		
		//TODO: maybe refactor - do not start every time if already running
		if (autoreload) {
			startAutoreload();
		} else {
			stopAutoReload();
		}
	}

	public void setSelectedPattern(Object pattern) {
		UyooLogger.getLogger().debug("Set pattern to " + pattern);
		m_currentPattern = pattern.toString();		
	}

	public void setSelectedFilter(LogFileFilter tf) {
		UyooLogger.getLogger().debug("Set filter to " + tf);
		m_currentFilter = tf;	
	}

	public void saveCurrentPattern() {
		//check if pattern already exist
		//TODO: move this code to settings
		List<P> pattern = UyooSettings.getInstance().getPersistentSettings().getPattern().getP();
		//check if already in persistent data
		boolean needToAdd = true;
		for(P next : pattern) {
			if (next.getValue().equals(m_currentPattern)) {
				needToAdd = false;
				break;
			}
		}
		
		if (needToAdd == true) {
			UyooLogger.getLogger().info("Save pattern " + m_currentPattern);
			
			//add to settings
			P p = new P();
			p.setValue(m_currentPattern);
			pattern.add(p);

			//save persistent
			UyooSettings.getInstance().saveConfigFile();
			
			//inform gui
			m_gui.updateSettings();
		}
	}

	public void saveCurrentFilter() {
		//check if pattern already exist
		//TODO: move this code to settings
		List<generated.Settings.Filter.F> filter = UyooSettings.getInstance().getPersistentSettings().getFilter().getF();
		//check if already in persistent data
		boolean needToAdd = true;
		for(generated.Settings.Filter.F next : filter) {
			if (next.getValue().equals(m_currentFilter.toString())) {
				needToAdd = false;
				break;
			}
		}
		
		if (needToAdd == true) {
			UyooLogger.getLogger().info("Save filter " + m_currentFilter);
			
			//add to settings
			generated.Settings.Filter.F f = new generated.Settings.Filter.F();
			f.setValue(m_currentFilter.toString());
			filter.add(f);

			//save persistent
			UyooSettings.getInstance().saveConfigFile();
			
			//inform gui
			m_gui.updateSettings();
		}		
	}
}
