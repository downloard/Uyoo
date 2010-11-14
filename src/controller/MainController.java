package controller;

import generated.Settings.Files.F;

import java.io.File;
import java.util.List;

import javax.swing.JOptionPane;

import setup.UyooSettings;
import swing.MainFrame;

public class MainController {

	private File m_selectedFile;
	
	private MainFrame m_gui;
	
	public MainController(MainFrame gui) {
		m_selectedFile = null;
		m_gui = gui;
	}

	public void setSelectedFile(String filename) {
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
}
