package core;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ExceptionHandler {

	public static void handleException(Exception e) {
		//log
		UyooLogger.getLogger().error(e.getMessage(), e);
		
		//show dialog
		showErrorDialog(e.getMessage(), e);
	}

	public static void showErrorDialog(String message, Exception e) {
		String[] s = { "OK", "Details" };
		JOptionPane jo = new JOptionPane();
		if (message != null && !message.equals("")) {
			jo.setMessage(message);
		} else {
			jo.setMessage("Error");
		}
		jo.setOptions(s);
		jo.setMessageType(JOptionPane.ERROR_MESSAGE);
		JDialog d = jo.createDialog(null, "Error occured");
		d.setVisible(true);

		if (jo.getValue() == s[1]) {
			showStackTraceDialog(e);
		}
	}

	private static void showStackTraceDialog(Exception e) {
		e.printStackTrace();

		JScrollPane sp = new JScrollPane(getStackTraceJTextArea(e));
		sp.setPreferredSize(new Dimension(640, 300));

		JOptionPane.showMessageDialog(null, sp, "Error - "
				+ e.getClass().getName(), JOptionPane.ERROR_MESSAGE);
	}

	private static JTextArea getStackTraceJTextArea(Exception e) {
		String message = e.getMessage();

		StringBuffer buff = new StringBuffer();
		buff.append(message);
		buff.append("\n");
		if (message != null) {
			for (int i = 0; i < message.length(); i++) {
				buff.append("-");
			}
			buff.append("\n");
		}

		StackTraceElement[] st = e.getStackTrace();
		for (int j = 0; j < st.length; j++) {
			buff.append(st[j]);
			buff.append("\n");
		}

		JTextArea ta = new JTextArea(buff.toString());
		ta.setEditable(false);

		return ta;
	}

}
