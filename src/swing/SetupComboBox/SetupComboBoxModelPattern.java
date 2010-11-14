package swing.SetupComboBox;

import generated.Settings.Pattern.P;

import java.util.List;

import swing.SetupComboBox.SetupComboBoxModel;;


@SuppressWarnings("serial")
public class SetupComboBoxModelPattern extends SetupComboBoxModel {

	private List<P> m_lstData;
	
	public SetupComboBoxModelPattern(List<P> listFiles) {
		super(listFiles);
		m_lstData = listFiles;
	}
	
	@Override
	public Object getElementAt(int index) {
		P f = m_lstData.get(index);
		return f.getValue();
	}
}
