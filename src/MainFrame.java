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
import swing.SetupComboBoxModel;
import table.LogTable;
import table.LogTableFilter;
import table.LogTableModel;


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
	
	
	public MainFrame() {
		super(UyooSettings.getInstance().getApplicationName() 
		      + " - "
		      + UyooSettings.getInstance().getVersionNumber());
		
		setSize(800, 600);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		m_controller = new MainController(this);
		
		initComponents();
	}
	
	private void initComponents() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		Settings settings = UyooSettings.getInstance().getPersistentSettings();

		{
			JPanel pnlNorth = new JPanel();
			pnlNorth.setLayout(new GridLayout(3, 1));
			
			//File
			m_cbFile = new JComboBox(new SetupComboBoxModel(settings.getFiles().getF()));
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
			JPanel pnlFile = new JPanel(new FlowLayout(FlowLayout.LEFT));
			pnlFile.add(new JLabel("File:"));
			pnlFile.add(m_cbFile);
			pnlFile.add(m_btnOpen);
			pnlFile.add(m_btnReload);
			pnlFile.add(m_cbAutoReload);
			
			//Pattern
			JPanel pnlPattern = new JPanel(new FlowLayout(FlowLayout.LEFT));
			m_cbPattern = new JComboBox(new SetupComboBoxModel(settings.getPattern().getP()));
			m_cbPattern.setEditable(true);
			m_cbPattern.setPreferredSize(new Dimension(400, m_cbPattern.getPreferredSize().height));
			pnlPattern.add(new JLabel("Pattern:"));
			pnlPattern.add(m_cbPattern);
			
			//Filter
			JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT));
			m_cbFilter = new JComboBox(new SetupComboBoxModel(settings.getFilter().getF()));
			m_cbFilter.setPreferredSize(new Dimension(400, m_cbFilter.getPreferredSize().height));
			m_cbFilter.setEditable(true);
			pnlFilter.add(new JLabel("Filter:"));
			pnlFilter.add(m_cbFilter);
			
			pnlNorth.add(pnlFile);			
			pnlNorth.add(pnlPattern);
			pnlNorth.add(pnlFilter);
			
			mainPanel.add(pnlNorth, BorderLayout.NORTH);
			
			if (m_cbFile.getItemCount() > 0) {
				m_cbFile.setSelectedIndex(0);
				setSelectedFile();
			}
			if (m_cbPattern.getItemCount() > 0) {
				m_cbPattern.setSelectedIndex(0);
			}
			if (m_cbFilter.getItemCount() > 0) {
				m_cbFilter.setSelectedIndex(0);
			}
			updateSettings(null);
		}

	    m_logTableModel = new LogTableModel(); 
		m_tblLogs = new LogTable(m_logTableModel);
		JScrollPane sp = new JScrollPane(m_tblLogs, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		mainPanel.add(sp, BorderLayout.CENTER);
		
		getContentPane().add(mainPanel);
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
		LogTableFilter tf = null;
		String filter = m_cbFilter.getSelectedItem().toString();
		if (filter.equals("") == false) {
			tf = new LogTableFilter(filter);
		}
		
		boolean autoReload = m_cbAutoReload.isSelected();
		
		m_tblLogs.setFile(file, m_cbPattern.getSelectedItem().toString(), tf, autoReload);		
	}

	
}
