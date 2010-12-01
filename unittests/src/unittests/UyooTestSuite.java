package unittests;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import unittests.data.LogFileListenerTest;
import unittests.data.LogFileTests;
import unittests.data.LogFilterFileTest;

@RunWith(Suite.class)
@SuiteClasses({LogFileListenerTest.class,
	           LogFileTests.class,
	           LogFilterFileTest.class})
public class UyooTestSuite {

	public static Test suite() {
		return new JUnit4TestAdapter(UyooTestSuite.class);
	}
	
}
