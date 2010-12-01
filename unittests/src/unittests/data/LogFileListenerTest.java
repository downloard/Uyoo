package unittests.data;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import unittests.mocks.LogFileListenerMock;


public class LogFileListenerTest {

	@Test
	public void defaultBehavior() {
		//mock for callbacks
		LogFileListenerMock callback = new LogFileListenerMock();
		
		//intial nothing is changed
		assertFalse( callback.wasDataChangedCalled() );
		assertFalse( callback.wasStructureChangedCalled() );
	}
	
}
