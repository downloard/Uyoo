package swing;

import generated.Settings;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import setup.UyooSettings;
import swing.SetupComboBox.SetupComboBoxEditor;
import swing.SetupComboBox.SetupComboBoxModel;
import swing.SetupComboBox.SetupComboBoxModelFile;
import swing.SetupComboBox.SetupComboBoxModelFilter;
import swing.SetupComboBox.SetupComboBoxModelPattern;
import table.LogTable;
import table.LogTableFilter;
import table.LogTableModel;
import controller.MainController;


@SuppressWarnings("serial")
public class MainFrame extends JFrame implements ActionListener {
	
	private MainController m_controller;
	
	private JComboBox  m_cbFile;
	private JButton    m_btnOpen; 
	private JButton    m_btnReload;
	
	private JComboBox  m_cbPattern;
	private JComboBox  m_cbFilter;
	
	private LogTable   m_tblLogs;
	private LogTableModel m_logTableModel;
	
	private JCheckBox  m_cbAutoReload;

	private JLabel m_lblFile;
	private JLabel m_lblPattern;
	private JLabel m_lblFilter;
	private JLabel m_lblEmpty;
	
	
	public MainFrame() {
		super(UyooSettings.getInstance().getApplicationName() 
		      + " "
		      + UyooSettings.getInstance().getVersionNumber());
		
		m_controller = new MainController(this);
		
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		initComponents();
		arrangeComponents();
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
			
			//Buttons
			JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
			m_lblEmpty = new JLabel("");
			pnlButtons.add(m_lblEmpty);
			pnlButtons.add(m_btnOpen);
			pnlButtons.add(m_btnReload);
			pnlButtons.add(m_cbAutoReload);
			
			//Pattern
			JPanel pnlPattern = new JPanel(new FlowLayout(FlowLayout.LEFT));
			m_lblPattern = new JLabel("Pattern:");
			pnlPattern.add(m_lblPattern);
			pnlPattern.add(m_cbPattern);
			
			//Filter
			JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT));
			m_lblFilter = new JLabel("Filter:");
			pnlFilter.add(m_lblFilter);
			pnlFilter.add(m_cbFilter);
			
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
		    m_logTableModel = new LogTableModel(); 
			m_tblLogs = new LogTable(m_logTableModel);
			JScrollPane sp = new JScrollPane(m_tblLogs, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			mainPanel.add(sp, BorderLayout.CENTER);
		}
	}

	private void initComponents() {
		Settings settings = UyooSettings.getInstance().getPersistentSettings();
		
		m_cbFile = new JComboBox(new SetupComboBoxModelFile(settings.getFiles().getF()));
		m_cbFile.setPreferredSize(new Dimension(600, m_cbFile.getPreferredSize().height));
		m_cbFile.setEditable(false);
		m_cbFile.addActionListener(this);
		
		m_btnOpen = new JButton("Select");
		m_btnOpen.addActionListener(this);
		m_btnReload = new JButton("(Re)Load");
		m_btnReload.addActionListener(this);
		
		m_cbAutoReload = new JCheckBox("Autoreload");
		m_cbAutoReload.setSelected(false);
		m_cbAutoReload.addActionListener(this);
		
		m_cbPattern = new JComboBox(new SetupComboBoxModelPattern(settings.getPattern().getP()));
		m_cbPattern.setEditor(new SetupComboBoxEditor());
		m_cbPattern.setEditable(true);
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
		setSelectedFile();			
		updateSettings(null);
	}

	private void selectFirstItem(JComboBox cb) {
		if (cb.getItemCount() > 0) {
			cb.setSelectedIndex(0);
			
		}
	}
	
	public void updateSettings(String file) {
		if (file != null) {
			//set selected item of combo box
			m_cbFile.setSelectedItem(file);
		}
		
		((SetupComboBoxModel) (m_cbFile.getModel())).dataChanged();
		((SetupComboBoxModel) (m_cbFilter.getModel())).dataChanged();
		((SetupComboBoxModel) (m_cbPattern.getModel())).dataChanged();
	}
	
	private void setSelectedFile() {
		assert(m_cbFile.getItemCount() > 0);
		m_controller.setSelectedFile(m_cbFile.getSelectedItem().toString());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == m_btnOpen) {
			m_controller.selectFile();
		} else if (e.getSource() == m_btnReload) {
			m_controller.loadFile();
		} else if (e.getSource() == m_cbAutoReload) {
			//TODO: move to controller
			if (m_cbAutoReload.isSelected()) {
				m_logTableModel.startAutoreload();
			} else {
				m_logTableModel.stopAutoReload();
			}
		} else if (e.getSource() == m_cbFile) {
			setSelectedFile();
		} else if (e.getSource() == m_cbFilter) {
			int i=0;
			i++;
		}
	}
	
	public File selectFile() {	
		JFileChooser fc = new JFileChooser();
		if (m_controller.getSelectedFile() != null) {
			fc.setSelectedFile(m_controller.getSelectedFile());
		}
		int state = fc.showOpenDialog(this);
		if ( state == JFileChooser.APPROVE_OPTION ) {
			return fc.getSelectedFile();
	    } else {
	    	return null;
	    }
	}
	
	public void loadFile(File file) {		
		//TODO: move to controller:
		
		//filter
		LogTableFilter tf = null;
		String filter = m_cbFilter.getEditor().getItem().toString();
		if (filter.equals("") == false) {
			tf = new LogTableFilter(filter);
		}
		
		//pattern
		String pattern = m_cbPattern.getEditor().getItem().toString();
		
		//autoreload
		boolean autoReload = m_cbAutoReload.isSelected();
		
		m_tblLogs.setFile(file, pattern, tf, autoReload);		
	}

	
}