package swing;

import generated.Settings;
import generated.Settings.Files.F;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import core.ExceptionHandler;
import core.UyooLogger;

import setup.UyooSettings;
import swing.SetupComboBox.SetupComboBoxEditor;
import swing.SetupComboBox.SetupComboBoxModel;
import swing.SetupComboBox.SetupComboBoxModelFile;
import swing.SetupComboBox.SetupComboBoxModelFilter;
import swing.SetupComboBox.SetupComboBoxModelPattern;
import swing.components.StatusBarFrame;
import swing.table.LogTable;
import swing.table.LogTableModel;
import data.ILogFileListener;
import data.LogFile;
import data.LogFileFilter;


@SuppressWarnings("serial")
public class MainFrame extends StatusBarFrame implements ActionListener, ILogFileListener, DropTargetListener {
	
	private LogFile    m_logFile;
	
	private JComboBox  m_cbFile;
	private JButton    m_btnOpen; 
	private JButton    m_btnReload;
	private JButton    m_btnAddPattern;
	private JButton    m_btnAddFilter;
	
	private JComboBox  m_cbPattern;
	private JComboBox  m_cbFilter;
	
	private LogTable      m_tblLogs;
	private LogTableModel m_logTableModel;
	
//	private JCheckBox  m_cbAutoReload;
	private JCheckBox  m_cbSearchCaseSensitive;
	private JCheckBox  m_cbKeepFilteredLines;

	private JLabel m_lblFile;
	private JLabel m_lblPattern;
	private JLabel m_lblFilter;
	private JLabel m_lblEmpty;
	
	private boolean initDone = false;
	
	
	public MainFrame() {
		super();
		setTitle(null);
		
		m_logFile = new LogFile();
		m_logFile.addListener(this);
		
		m_logTableModel = new LogTableModel(); 
		
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//init GUI
		initComponents();
		arrangeComponents();
		
		//glue logic with gui
		m_logTableModel.setLogFile( m_logFile );
		
		initDone = true;
	}
	
	@Override
	public void setTitle(String title) {
		StringBuilder sb = new StringBuilder();
		sb.append(UyooSettings.getInstance().getApplicationName()); 
		sb.append(" ");
		sb.append(UyooSettings.getInstance().getVersionNumber());
		if (title != null) {
			sb.append(" - ");
			sb.append(title);
		}
		super.setTitle(sb.toString());
	}
	
	private void arrangeComponents() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		getContentPane().add(mainPanel);

		//North
		{
			JPanel pnlNorth = new JPanel();
			pnlNorth.setLayout(new GridLayout(4, 2));
			
			//File
			JPanel pnlFile = new JPanel(new FlowLayout(FlowLayout.LEFT));
			m_lblFile = new JLabel("File:");
			pnlFile.add(m_lblFile);
			pnlFile.add(m_cbFile);
			pnlFile.add(m_btnOpen);
			
			//Buttons
			JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
			m_lblEmpty = new JLabel("");
			pnlButtons.add(m_lblEmpty);
			pnlButtons.add(m_btnReload);
//			pnlButtons.add(m_cbAutoReload);
			
			//Pattern
			JPanel pnlPattern = new JPanel(new FlowLayout(FlowLayout.LEFT));
			m_lblPattern = new JLabel("Pattern:");
			pnlPattern.add(m_lblPattern);
			pnlPattern.add(m_cbPattern);
			pnlPattern.add(m_btnAddPattern);
			
			//Filter
			JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT));
			m_lblFilter = new JLabel("Filter:");
			pnlFilter.add(m_lblFilter);
			pnlFilter.add(m_cbFilter);
			pnlFilter.add(m_btnAddFilter);
			pnlFilter.add(m_cbSearchCaseSensitive);
			pnlFilter.add(m_cbKeepFilteredLines);
			
			pnlNorth.add(pnlFile);
			pnlNorth.add(pnlButtons);			
			pnlNorth.add(pnlPattern);
			pnlNorth.add(pnlFilter);
			
			//recalculate prefered size for lables
			{
				int maxWidth = m_lblFile.getPreferredSize().width;
				maxWidth = Math.max(maxWidth, m_lblFilter.getPreferredSize().width);
				maxWidth = Math.max(maxWidth, m_lblPattern.getPreferredSize().width);
				maxWidth = Math.max(maxWidth, m_lblEmpty.getPreferredSize().width);
				
				m_lblFile.setPreferredSize(new Dimension(maxWidth, m_lblFile.getPreferredSize().height));
				m_lblFilter.setPreferredSize(new Dimension(maxWidth, m_lblFilter.getPreferredSize().height));
				m_lblPattern.setPreferredSize(new Dimension(maxWidth, m_lblPattern.getPreferredSize().height));
				m_lblEmpty.setPreferredSize(new Dimension(maxWidth, m_lblEmpty.getPreferredSize().height));
			}
			
			mainPanel.add(pnlNorth, BorderLayout.NORTH);
		}

		//Center
		{
			m_tblLogs = new LogTable(m_logTableModel);
			JScrollPane sp = new JScrollPane(m_tblLogs, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			mainPanel.add(sp, BorderLayout.CENTER);
		}
	}

	private void initComponents() {
		Settings settings = UyooSettings.getInstance().getPersistentSettings();
		
		List<F> files = settings.getFiles().getF();
		m_cbFile = new JComboBox(new SetupComboBoxModelFile(files));
		m_cbFile.setPreferredSize(new Dimension(600, m_cbFile.getPreferredSize().height));
		m_cbFile.setEditable(false);
		m_cbFile.addActionListener(this);
		new DropTarget(m_cbFile, this);
		
		m_btnOpen = new JButton("...");
		m_btnOpen.addActionListener(this);
		
		m_btnReload = new JButton("Reload");
		m_btnReload.addActionListener(this);
		
		m_btnAddPattern = new JButton("Add");
		m_btnAddPattern.addActionListener(this);
		
		m_btnAddFilter = new JButton("Add");
		m_btnAddFilter.addActionListener(this);
		
		m_cbSearchCaseSensitive = new JCheckBox("Case sensitive");
		m_cbSearchCaseSensitive.setSelected(m_logTableModel.isSearchCaseSensitive());
		m_cbSearchCaseSensitive.addActionListener(this);
		
		m_cbKeepFilteredLines = new JCheckBox("Keep filtered lines");
		m_cbKeepFilteredLines.setSelected(m_logTableModel.isKeepFilteredLines());
		m_cbKeepFilteredLines.addActionListener(this);
		
		m_cbPattern = new JComboBox(new SetupComboBoxModelPattern(settings.getPattern().getP()));
		m_cbPattern.setEditor(new SetupComboBoxEditor());
		m_cbPattern.setEditable(true);
		m_cbPattern.addActionListener(this);
		m_cbPattern.setPreferredSize(new Dimension(400, m_cbPattern.getPreferredSize().height));
		
		m_cbFilter = new JComboBox(new SetupComboBoxModelFilter(settings.getFilter().getF()));
		m_cbFilter.setPreferredSize(new Dimension(400, m_cbFilter.getPreferredSize().height));
		m_cbFilter.setEditor(new SetupComboBoxEditor());
		m_cbFilter.setEditable(true);
		m_cbFilter.addActionListener(this);
		
		selectFirstItem(m_cbFile);
		selectFirstItem(m_cbPattern);
		selectFirstItem(m_cbFilter);
		
		//update BL 		
		setSelectedPattern();
		setSelectedFilter();
		updateSettings();
	}

	private void selectFirstItem(JComboBox cb) {
		if (cb.getItemCount() > 0) {
			cb.setSelectedIndex(0);
		}
	}
	
	public void updateSettings() {
		File file = m_logFile.getFile();
		if (file != null) {
			//set selected item of combo box
			m_cbFile.setSelectedItem(file.getAbsolutePath());
		}
		
		((SetupComboBoxModel) (m_cbFile.getModel())).dataChanged();
		((SetupComboBoxModel) (m_cbFilter.getModel())).dataChanged();
		((SetupComboBoxModel) (m_cbPattern.getModel())).dataChanged();
	}
	
	private File selectFile() {	
		JFileChooser fc = new JFileChooser();
		if (m_logFile.getFile() != null) {
			fc.setSelectedFile(m_logFile.getFile().getAbsoluteFile());
		}
		int state = fc.showOpenDialog(this);
		if ( state == JFileChooser.APPROVE_OPTION ) {
			return fc.getSelectedFile();
	    } else {
	    	return null;
	    }
	}
	
	private void openSelectedFile() {
		assert(m_cbFile.getItemCount() > 0);

		File f = null;
		Object selectedFile = m_cbFile.getSelectedItem();
		if (selectedFile != null) {
			f = new File(selectedFile.toString());
		} 
		// if f is null openFile creates error message
		openFile(f);
	}
	
	private void openFile(File file) {
		if (file != null && file.exists()) {			
			m_logFile.openFile( file );
			
			//saveSelectedFile
			UyooSettings.getInstance().saveFile(m_logFile.getFile());
			updateSettings();			
		} else {
			JOptionPane.showMessageDialog(this, 
                    "No valid file selected",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
			
			//TODO: clear table
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (initDone == false) {
			return;
		}
		
		// Open
		if (e.getSource() == m_btnOpen) {
			File selectedFile = selectFile();
			if (selectedFile != null) {								
				openFile(selectedFile);
			}
			
		// File
		} else if ((e.getSource() == m_cbFile) || (e.getSource() == m_btnReload)) {
			openSelectedFile();

		// Case sensitive
		} else if (e.getSource() == m_cbSearchCaseSensitive) {
			m_logTableModel.setSearchCaseSensitive(m_cbSearchCaseSensitive.isSelected());
		
		// keep filtered lines
		} else if (e.getSource() == m_cbKeepFilteredLines) {
			m_logTableModel.setKeepFilteredLines(m_cbKeepFilteredLines.isSelected());				
			
		// Pattern
		//TODO: is it dirty to check "comboBoxChangeEvent" via string? 
		//      http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4808758
		} else if (e.getSource() == m_cbPattern
				   && e.getActionCommand().equals("comboBoxChanged")) {
			setSelectedPattern();
			
		// Filter
		//TODO: is it dirty to check "comboBoxChangeEvent" via string? 
		//      http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4808758
		} else if (e.getSource() == m_cbFilter 
				   && e.getActionCommand().equals("comboBoxChanged")) {
			setSelectedFilter();
		
		// Add Pattern
		} else if (e.getSource() == m_btnAddPattern) {
			UyooSettings.getInstance().savePattern( m_logTableModel.getSelectedPattern() );
			updateSettings();
		
		// Add Filter
		} else if (e.getSource() == m_btnAddFilter) {
			LogFileFilter filter =  m_logTableModel.getSelectedFilter();
			if (filter != null) {
				UyooSettings.getInstance().saveFilter(filter);
				updateSettings();
			}
		}
	}

	private void setSelectedFilter() {
		LogFileFilter tf = null;
		try {
			String filter = m_cbFilter.getEditor().getItem().toString();
			if (filter.equals("") == false) {
				tf = new LogFileFilter(filter);
			}
		} catch (IllegalArgumentException ex) {
			JOptionPane.showMessageDialog(this, 
										  "Illegal Filter", 
					                      "Error",
					                      JOptionPane.ERROR_MESSAGE);
		}
		m_logTableModel.setSelectedFilter(tf);
	}

	private void setSelectedPattern() {
		m_logTableModel.setSelectedPattern( m_cbPattern.getEditor().getItem().toString() );
	}
	
	private void updateFileInformation() {
		File file = m_logFile.getFile();
		
		//JFrame title
		{
			setTitle(file.getAbsolutePath());
		}

		//File size informations
		{
			long fileSize = file.length();
			StringBuilder strSize = new StringBuilder("Size: ");
			
			if (fileSize > 1000*1000) {
				//xx.xxMB
				strSize.append(fileSize/1000/1000);
				strSize.append(".");
				//only first 2 digits
				strSize.append((fileSize/1000/10)%(100)); 
				strSize.append("MB");
			} else if (fileSize > 1000) {
				//xx.xxKB
				strSize.append(fileSize/1000);
				strSize.append(".");
				//only first 2 digits
				strSize.append((fileSize/10)%(100));
				strSize.append("KB");
			} else {
				//xxBytes
				strSize.append(fileSize + "Bytes");
			}
			setLeftText(strSize.toString());
		}
		
		//File time stamp informations
		{
			long date = file.lastModified();
			DateFormat formatter = new SimpleDateFormat(); 
			setMidlleText("Last Modified: " + formatter.format(date) );
			
		}
	}
	
	@Override
	public void dataAdded() {
		updateFileInformation();
	}
	
	@Override
	public void fileChanged(File newFile) {
		updateFileInformation();
	}
	
	//************** DROP TARGET LISTENER ***********************
	
	@Override
	public void dragEnter(DropTargetDragEvent dtde) {		
	}
	
	@Override
	public void dragExit(DropTargetEvent dte) {
	}
	
	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		Transferable tr = dtde.getTransferable();
		if (tr != null && tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			File f = getDropFile(tr);
			if (f != null) {
				dtde.acceptDrag(DnDConstants.ACTION_COPY);
			} else {
				dtde.acceptDrag(DnDConstants.ACTION_NONE);
			}
		} else {
			dtde.acceptDrag(DnDConstants.ACTION_NONE);
		}
		
	}
	
	@Override
	public void drop(DropTargetDropEvent dtde) {
		Transferable tr = dtde.getTransferable();

		if (tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			
			dtde.acceptDrop(DnDConstants.ACTION_COPY);
			
			File f = getDropFile(tr);
			
			if (f != null) {
				openFile(f);
			} else {
				dtde.rejectDrop();
			}
				
		} else {
			dtde.rejectDrop();
		}
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	private File getDropFile(Transferable tr) {
		File f = null;
		try {
			//Java doc "DataFlavor.javaFileListFlavor":
			// Each element of the list is required/guaranteed to be of type java.io.File
			@SuppressWarnings("unchecked")
			List<File> data = (List<File>) (tr.getTransferData(DataFlavor.javaFileListFlavor));
			
			//only accept one file
			if (data.size() == 1) {
				//use first entry
				f = data.get(0);
				
				if (f.isDirectory()) {
					f = null;
				}
				
			}
		} catch (UnsupportedFlavorException e) {
			UyooLogger.getLogger().error("UnsupportedFlavorException", e);
		} catch (IOException e) {
			UyooLogger.getLogger().error("IOException", e);
		} catch (ClassCastException e) {
			UyooLogger.getLogger().error("ClassCastException", e);
		} 
			
		return f;
	}
}
