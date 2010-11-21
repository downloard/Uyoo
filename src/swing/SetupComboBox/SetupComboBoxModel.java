package swing.SetupComboBox;

import java.util.List;

import javax.swing.DefaultComboBoxModel;

@SuppressWarnings("serial")
public abstract class SetupComboBoxModel extends DefaultComboBoxModel {
	
	private List<?> m_data;
	
		
	public SetupComboBoxModel(List<?> data) {
		m_data = data;
	}
	
	public void dataChanged() {
		fireContentsChanged(m_data, 0, m_data.size());
	}
	
	@Override
	public int getSize() {
		return m_data.size();
	}
	
	@Override
	public void addElement(Object anObject) {
		super.addElement(anObject);
	}
	
	@Override
	public void insertElementAt(Object anObject, int index) {
		super.insertElementAt(anObject, index);
	}
	
	@Override
	public abstract Object getElementAt(int index);
}