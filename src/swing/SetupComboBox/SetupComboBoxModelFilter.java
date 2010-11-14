package swing.SetupComboBox;

import generated.Settings.Filter.F;

import java.util.List;

import swing.SetupComboBox.SetupComboBoxModel;;

@SuppressWarnings("serial")
public class SetupComboBoxModelFilter extends SetupComboBoxModel {

	private List<F> m_lstData;
	
	public SetupComboBoxModelFilter(List<F> listFiles) {
		super(listFiles);
		m_lstData = listFiles;
	}
	
	//TODO: could return LogTableFilter
	@Override
	public Object getElementAt(int index) {
		F f = m_lstData.get(index);
		return f.getValue();
	}
}
