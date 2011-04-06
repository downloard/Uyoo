package unittests;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import unittests.core.FileHelperTests;
import unittests.core.FileWatcherTest;
import unittests.data.GroupedLogLineTests;
import unittests.data.LogFileListenerTest;
import unittests.data.LogFileTests;
import unittests.data.LogFilterFileTest;
import unittests.setup.UyooSettingsTests;

@RunWith(Suite.class)
@SuiteClasses({
			   LogFileListenerTest.class,
			   GroupedLogLineTests.class,
	           LogFileTests.class,
	           LogFilterFileTest.class,
	           FileWatcherTest.class,
	           FileHelperTests.class,
	           UyooSettingsTests.class
	          })
public class UyooTestSuite {

	public static Test suite() {
		return new JUnit4TestAdapter(UyooTestSuite.class);
	}
	
}
