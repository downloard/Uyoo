package unittests.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import unittests.data.LogFileTests;
import core.FileHelper;
import core.FileWatcher;


public class FileWatcherTest {

	private static File TEST_LOG_FILE = new File("copy.log");
	
	private static int m_callBackCounter;
	
	
	@Before
	public void copyTestFile() {
		//make a copy of log file		
		File source = new File(LogFileTests.TEST_FILE);
		
		assertFalse( TEST_LOG_FILE.exists() );
		
		try {
			FileHelper.copyFile(TEST_LOG_FILE, source);
		} catch (IOException e) {
			assertTrue( false );
		}
	}
	
	@After
	public void afterTest() {
		//delete file
		TEST_LOG_FILE.delete();
	}
	
	@Test
	public void watchingLineAdded() {
		FileWatcher watcher = initFileWatcher();
		
		//wait if started
		waitForFileWatcherCyclus();
		
		//append data
		appendLine(1, false);
		
		//wait until watcher has updated
		waitForFileWatcherCyclus();

		//callback must has been called 
		assertEquals(1, m_callBackCounter);
		
		watcher.stop();
	}
	
	@Test
	public void updateWithOutSave() {
		FileWatcher watcher = initFileWatcher();
		
		//wait if started
		waitForFileWatcherCyclus();
		
		//append data
		appendLine(2, true);
		
		//adding 2 lines and sleep between addings
		//-> Watcher should be triggered 2 time
		waitForFileWatcherCyclus();

		//callback must has been called 3 times
		// 1. add line
		// 2. add line
		// 3. close file -> timestamp changes
		assertEquals(3, m_callBackCounter);
		
		watcher.stop();
	}

	private FileWatcher initFileWatcher() {
		m_callBackCounter = 0;
		
		FileWatcher watcher = new FileWatcher() {
			@Override
			protected void onChange(File file) {
				m_callBackCounter++;
			}
		};
		
		//set and start
		watcher.setFile(TEST_LOG_FILE);
		watcher.start();
		return watcher;
	}

	private void waitForFileWatcherCyclus() {
		try {
			Thread.sleep(2*FileWatcher.AUTORELOAD_PERIODE);
		} catch (Exception e) {
			assertTrue( false );
		}
	}

	private void appendLine(int linesToAdd, boolean sleepAfterLineAdded) {
		try { 
			BufferedWriter out = new BufferedWriter(new FileWriter(TEST_LOG_FILE, true)); 
			
			for (int i=0; i < linesToAdd; i++) {
				out.write("08.09.29-INFO-Main.cpp-UT add a line\n");
				out.flush();
				
				if (sleepAfterLineAdded) {
					waitForFileWatcherCyclus();
				}
			}
			
			out.close(); 
		} catch (IOException e) {
			assertTrue(false);
		} 
	}
}