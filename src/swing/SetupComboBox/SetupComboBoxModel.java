package swing.SetupComboBox;

import java.util.List;

import javax.swing.DefaultComboBoxModel;

@SuppressWarnings("serial")
public abstract class SetupComboBoxModel extends DefaultComboBoxModel {
	
	private int INVALID_INDEX = -1;
	
	private List<?> m_data;
	private int m_selectedIndex;
		
	public SetupComboBoxModel(List<?> data) {
		m_data = data;
		m_selectedIndex = INVALID_INDEX;
	}
	
	public void dataChanged() {
		fireContentsChanged(m_data, 0, m_data.size());
	}
	
	@Override
	public int getSize() {
		return m_data.size();
	}

	@Override
	public void setSelectedItem(Object anItem) {
		String strItem = (String) anItem;
		for (int i=0; i < getSize(); i++) {
			String next = (String) getElementAt(i);
			if (next.equals(strItem) == true) {
				m_selectedIndex = i;
				return;
			}
		}
		m_selectedIndex = INVALID_INDEX;
	}

	@Override
	public Object getSelectedItem() {
		if (m_selectedIndex != INVALID_INDEX) {
			return getElementAt(m_selectedIndex);
		} else {
			return "";
		}
	}
	
	@Override
	public void addElement(Object anObject) {
		// TODO Auto-generated method stub
		super.addElement(anObject);
	}
	
	@Override
	public void insertElementAt(Object anObject, int index) {
		// TODO Auto-generated method stub
		super.insertElementAt(anObject, index);
	}
	
	@Override
	public abstract Object getElementAt(int index);
}