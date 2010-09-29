import generated.Settings;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import table.LogTable;
import table.LogTableFilter;


@SuppressWarnings("serial")
public class MainFrame extends JFrame implements ActionListener {
	
	private File 	   m_selectedFile;
	
	private JTextField m_tfFile;
	private JButton    m_btnOpen; 
	private JButton    m_btnReload;
	
	private JComboBox  m_cbPattern;
	private JComboBox  m_cbFilter;
	
	private LogTable   m_tblLogs;
	
	
	public MainFrame() {
		super("Uyoo");
		
		m_selectedFile = null;
		
		setSize(800, 600);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		initComponents();
	}

	private void initComponents() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		Settings settings = UyooSettings.getInstance().getSettings();
		
		{
			JPanel pnlNorth = new JPanel();
			pnlNorth.setLayout(new GridLayout(3, 1));
			
			//File
			m_tfFile = new JTextField(50);
			m_tfFile.setEditable(false);
			m_btnOpen = new JButton("Open");
			m_btnOpen.addActionListener(this);
			m_btnReload = new JButton("Reload");
			m_btnReload.addActionListener(this);
			JPanel pnlFile = new JPanel(new FlowLayout(FlowLayout.LEFT));
			pnlFile.add(new JLabel("File:"));
			pnlFile.add(m_tfFile);
			pnlFile.add(m_btnOpen);
			pnlFile.add(m_btnReload);
			
			//Pattern
			JPanel pnlPattern = new JPanel(new FlowLayout(FlowLayout.LEFT));
			m_cbPattern = new JComboBox();
			m_cbPattern.setEditable(true);
			m_cbPattern.setPreferredSize(new Dimension(400, m_cbPattern.getPreferredSize().height));
			for (Settings.Pattern.P pattern : settings.getPattern().getP()) {
				m_cbPattern.addItem(pattern.getValue());
			}
			pnlPattern.add(new JLabel("Pattern:"));
			pnlPattern.add(m_cbPattern);
			
			//Filter
			JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT));
			m_cbFilter = new JComboBox();
			m_cbFilter.setPreferredSize(new Dimension(400, m_cbFilter.getPreferredSize().height));
			m_cbFilter.setEditable(true);
			for (Settings.Filter.F filter : settings.getFilter().getF()) {
				m_cbFilter.addItem(filter.getValue());
			}
			pnlFilter.add(new JLabel("Filter:"));
			pnlFilter.add(m_cbFilter);
			
			pnlNorth.add(pnlFile);			
			pnlNorth.add(pnlPattern);
			pnlNorth.add(pnlFilter);
			
			mainPanel.add(pnlNorth, BorderLayout.NORTH);
		}

		m_tblLogs = new LogTable();
		JScrollPane sp = new JScrollPane(m_tblLogs, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		mainPanel.add(sp, BorderLayout.CENTER);
		
		getContentPane().add(mainPanel);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == m_btnOpen) {
			selectFile();
			openFile();
		} else if (e.getSource() == m_btnReload) {
			openFile();
		}
	}
	
	private void selectFile() {
		JFileChooser fc = new JFileChooser("c:\\Projects\\3\\Boards\\_workspaces_speech\\_Win32_OGLES2_Debug\\output\\");
		int state = fc.showOpenDialog(this);
		if ( state == JFileChooser.APPROVE_OPTION )
	    {
			m_selectedFile = fc.getSelectedFile();
			m_tfFile.setText( m_selectedFile.getAbsolutePath() );
	    }
	}
	
	private void openFile() {
		if (m_selectedFile == null) {
			JOptionPane.showMessageDialog(this, "No file selected");
			return;
		}
		
		LogTableFilter tf = null;
		String filter = m_cbFilter.getSelectedItem().toString();
		if (filter.equals("") == false) {
			tf = new LogTableFilter(filter);
		}
		m_tblLogs.setFile(m_selectedFile, m_cbPattern.getSelectedItem().toString(), tf);
	}
}
