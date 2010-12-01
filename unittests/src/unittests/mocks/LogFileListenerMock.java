package unittests.mocks;

import data.ILogFileListener;

public class LogFileListenerMock implements ILogFileListener {

	private boolean m_dataChangedCalled;
	private boolean m_structureChangedCalled;
	
	public LogFileListenerMock() {
		reset();
	}

	@Override
	public void structureChanged() {
		m_structureChangedCalled = true;
	}

	@Override
	public void dataChanged() {
		m_dataChangedCalled = true;
	}

	public boolean wasStructureChangedCalled() {
		return m_structureChangedCalled;
	}
	
	public boolean wasDataChangedCalled() {
		return m_dataChangedCalled;
	}
	
	public void reset() {
		m_dataChangedCalled = false;
		m_structureChangedCalled = false;
	}
}
