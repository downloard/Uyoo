package unittests.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import unittests.mocks.LogFileListenerMock;
import data.LogFile;


public class LogFileTests {
	
	static public  String TEST_FILE              = "unittests/resources/Unittest_LogFileTests.log";
	

	@Test
	public void loadFile() throws Throwable {
		LogFile lf = new LogFile();
		
		//mock for callbacks
		LogFileListenerMock callback = new LogFileListenerMock();
		lf.addListener(callback);
		assertFalse( callback.wasDataChangedCalled() );
		assertFalse( callback.wasStructureChangedCalled() );		
		
		assertEquals(0, lf.getLineCount());
		
		lf.openFile(new File(TEST_FILE));
		
		//check structure changed event was fired
		assertFalse( callback.wasDataChangedCalled() );
		assertFalse( callback.wasStructureChangedCalled() );
		assertTrue( callback.wasFileChangedCalled() );
		
		//check line count
		Thread.sleep(1000);
		assertEquals(6, lf.getLineCount());
		
		lf.finalize();
	}
	
	@Test
	public void noFile() {
		LogFile lf = new LogFile();
		
		//check default pattern
		assertEquals(0, lf.getLineCount());
	}
}
