package unittests.mocks;

import java.io.File;

import data.ILogFileListener;

public class LogFileListenerMock implements ILogFileListener {

	private boolean m_dataChangedCalled;
	private boolean m_structureChangedCalled;
	private boolean m_wasFileChangedCalled;
	
	public LogFileListenerMock() {
		reset();
	}

	@Override
	public void dataAdded() {
		m_dataChangedCalled = true;
	}
	
	@Override
	public void fileChanged(File newFile) {
		m_wasFileChangedCalled = true;
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
		m_wasFileChangedCalled = false;
	}

	public boolean wasFileChangedCalled() {
		return m_wasFileChangedCalled;
	}
}
