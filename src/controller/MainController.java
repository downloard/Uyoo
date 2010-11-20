package controller;

import generated.Settings.Files.F;

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

	
	public MainController(MainFrame gui) {
		m_selectedFile = null;
		m_gui = gui;
		
		m_logFile = new LogFile();
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

	public void selectFile() {
		File selectedFile = m_gui.selectFile();
		if (selectedFile != null) {
			m_selectedFile = selectedFile;
			
			addNewFile();
			
			m_gui.updateSettings(m_selectedFile.getAbsolutePath());
			
			//loadFile();
		}
	}
	
	public void loadFile() {
		if (m_selectedFile == null) {
			JOptionPane.showMessageDialog(m_gui, "No file selected");
			return;
		}		
		m_gui.loadFile(m_selectedFile);
	}

	/**
	 * Adds new file to persistent settings and GUI drop down
	 */
	private void addNewFile() {
		//add file to settings
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
		}
	}

	public void setFile(File file, String pattern, LogFileFilter filter, boolean autoReload) {
		stopAutoReload();	
		
		m_currentFilter  = filter;
		m_currentPattern = pattern;
		
		m_logFile.readFile(file);
		m_logFile.updateViewport(pattern, filter);
		
		if (autoReload) {
			startAutoreload();
		}
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
				UyooLogger.getLogger().debug("OnChange from autoreload arrived");
				m_logFile.reloadFile();
				m_logFile.updateViewport(m_currentPattern, m_currentFilter);
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

	public void setAutoreload(boolean selected) {
		if (selected) {
			startAutoreload();
		} else {
			stopAutoReload();
		}
	}
}
