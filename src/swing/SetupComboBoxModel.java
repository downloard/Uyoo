package swing;

import java.lang.reflect.Method;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

@SuppressWarnings("serial")
public class SetupComboBoxModel extends DefaultComboBoxModel {
	
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
	public Object getElementAt(int index) { 
		Object o = m_data.get(index);
		try {
			Method m = o.getClass().getMethod("getValue");
			return m.invoke(o);
		} catch (Exception e) {
			e.printStackTrace();
			assert(false);
			return "<Exception: wrong object>";
		}
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
		if (m_selectedIndex != INVALID_INDEX)
			return getElementAt(m_selectedIndex);
		else
			return "";
	}
}