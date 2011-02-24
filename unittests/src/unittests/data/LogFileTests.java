package unittests.data;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import unittests.mocks.LogFileListenerMock;

import data.LogFile;
import data.LogFileFilter;


public class LogFileTests {
	
	static public  String TEST_FILE              = "unittests/resources/Unittest_LogFileTests.log";
	static private String PATTERN                = "^(.*?)-(.*?)-(.*?)-(.*?)$";
	static private String PATTERN_WITHOUT_TIME   = "^.*?-(.*?)-(.*?)-(.*?)$";
	

	@Test
	public void loadFile() {
		LogFile lf = new LogFile();
		
		//mock for callbacks
		LogFileListenerMock callback = new LogFileListenerMock();
		lf.addListener(callback);
		assertFalse( callback.wasDataChangedCalled() );
		assertFalse( callback.wasStructureChangedCalled() );
		
		lf.setSelectedPattern(PATTERN);
		
		lf.openFile(new File(TEST_FILE));		
		
		assertEquals(6, lf.getLineCount());
		assertEquals(5, lf.getGroupCount()); //includes line number 
		
		//check structure changed event was fired
		assertFalse( callback.wasDataChangedCalled() );
		assertTrue( callback.wasStructureChangedCalled() );
		
		//check line count
		assertEquals(6, lf.getLineCount());
		
		//check 1. line - correct line
		assertEquals("1",            lf.getData(0, 0)); //line counter
		assertEquals("08.09.29",     lf.getData(0, 1)); //date
		assertEquals("INFO",         lf.getData(0, 2)); //Debug Level
		assertEquals("Main.cpp",     lf.getData(0, 3)); //class name
		assertEquals("Starting app", lf.getData(0, 4)); //msg
		
		//check 3. line - did not match pattern
		assertEquals("3",               lf.getData(2, 0)); //line counter
		assertEquals("Strange message", lf.getData(2, 1)); //data
		assertEquals("",                lf.getData(2, 2)); //
		assertEquals("",                lf.getData(2, 3)); //
		assertEquals("",                lf.getData(2, 4)); //
	}
	
	@Test
	public void noFile() {
		LogFile lf = new LogFile();
		
		//check default pattern
		assertEquals(0, lf.getLineCount());
		assertEquals(0, lf.getGroupCount()); //includes line number
	}
	
	@Test
	public void checkPattern() {
		LogFile lf = new LogFile();
		
		lf.setSelectedPattern(PATTERN);
		
		//check default pattern
		assertEquals(6, lf.getLineCount());
		assertEquals(5, lf.getGroupCount()); //includes line number 

		//add mock for callbacks
		LogFileListenerMock callback = new LogFileListenerMock();
		lf.addListener(callback);
		
		//change pattern without time
		lf.setSelectedPattern(PATTERN_WITHOUT_TIME);
		
		lf.openFile(new File(TEST_FILE));
		
		//check new line/group count
		assertEquals(6, lf.getLineCount());
		assertEquals(4, lf.getGroupCount()); //includes line number 
		
		//check callbacks
		assertFalse( callback.wasDataChangedCalled() );
		assertTrue( callback.wasStructureChangedCalled() );
	}
	
	@Test 
	public void checkFilterCaseInsensitive() {
		LogFile lf = new LogFile();
		
		assertFalse(lf.isSearchCaseSensitive());
		
		LogFileFilter filter = new LogFileFilter("2:DEBUG");
		
		lf.setSelectedPattern(PATTERN);
		lf.setSelectedFilter(filter);
		
		lf.openFile(new File(TEST_FILE));
		
		//check line count
		assertEquals(3, lf.getLineCount());
		
		//check first line
		assertEquals("2",        lf.getData(0, 0)); //line counter
		assertEquals("DEBUG",    lf.getData(0, 2)); //data
		assertEquals("Load HMI", lf.getData(0, 4)); //msg
	}
	
	@Test 
	public void checkFilterCaseSensitive() {
		LogFile lf = new LogFile();
		lf.setSearchCaseSensitive(true);
		
		assertTrue(lf.isSearchCaseSensitive());
		
		LogFileFilter filter = new LogFileFilter("2:Debug");
		
		lf.setSelectedPattern(PATTERN);
		lf.setSelectedFilter(filter);
		
		lf.openFile(new File(TEST_FILE));
		
		//check line count
		assertEquals(1, lf.getLineCount());
		
		//check first line
		assertEquals("5",          lf.getData(0, 0)); //line counter
		assertEquals("Debug",      lf.getData(0, 2)); //data
		assertEquals("Load z.lib", lf.getData(0, 4)); //msg
	}
}
