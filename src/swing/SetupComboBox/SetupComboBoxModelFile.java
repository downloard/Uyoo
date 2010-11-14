package swing.SetupComboBox;

import java.util.List;

import swing.SetupComboBox.SetupComboBoxModel;

import generated.Settings.Files.F;

@SuppressWarnings("serial")
public class SetupComboBoxModelFile extends SetupComboBoxModel {

	private List<F> m_lstData;
	
	public SetupComboBoxModelFile(List<F> listFiles) {
		super(listFiles);
		m_lstData = listFiles;
	}
	
	@Override
	public Object getElementAt(int index) {
		F f = m_lstData.get(index);
		return f.getValue();
	}
}
