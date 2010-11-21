package swing.SetupComboBox;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxEditor;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class SetupComboBoxEditor extends JTextField implements ComboBoxEditor {

	public SetupComboBoxEditor() {
		setBorder(BorderFactory.createEmptyBorder());
	}	
	
	@Override
	public Component getEditorComponent() {
		return this;
	}

	@Override
	public void setItem(Object anObject) {
		this.setText((String)anObject);
	}

	@Override
	public Object getItem() {
		return getText();
	}
}
