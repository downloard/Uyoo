package unittests.data;

import java.util.regex.Pattern;

import static org.junit.Assert.*;
import org.junit.Test;

import data.GroupedLogLine;
import data.LogLine;

public class GroupedLogLineTests {

	static private String PATTERN                = "^(.*?)-(.*?)-(.*?)-(.*?)$";
	static private String PATTERN_WITHOUT_TIME   = "^.*?-(.*?)-(.*?)-(.*?)$";
	
	static private String[] LINES = {"08.09.29-INFO-Main.cpp-Starting app",
									"08.09.29-DEBUG-Main.cpp-Load HMI",
									"Strange message",
									"08.09.29-DEBUG-Main.cpp-Load y.lib",
									"08.09.29-Debug-Main.cpp-Load z.lib",
									"08.09.29-ERROR-Main.cpp-Cannot find a.lib"};
	
	@Test
	public void checkValidPattern() {		
		LogLine ll = new LogLine(LINES[0], 1);
		GroupedLogLine gll = new GroupedLogLine(ll);
		gll.groupData(Pattern.compile(PATTERN), 5);
		
		assertEquals(5, gll.getGroupCount());
		
		//check 1. line - correct line
		assertEquals("1",            gll.getGroupText(0)); //line counter
		assertEquals("08.09.29",     gll.getGroupText(1)); //date
		assertEquals("INFO",         gll.getGroupText(2)); //Debug Level
		assertEquals("Main.cpp",     gll.getGroupText(3)); //class name
		assertEquals("Starting app", gll.getGroupText(4)); //msg
	}
	
	@Test
	public void excludeOneGroup() {		
		LogLine ll = new LogLine(LINES[0], 1);
		GroupedLogLine gll = new GroupedLogLine(ll);
		gll.groupData(Pattern.compile(PATTERN_WITHOUT_TIME), 4);
		
		assertEquals(4, gll.getGroupCount());
		
		//check 1. line - correct line
		assertEquals("1",            gll.getGroupText(0)); //line counter
		assertEquals("INFO",         gll.getGroupText(1)); //Debug Level
		assertEquals("Main.cpp",     gll.getGroupText(2)); //class name
		assertEquals("Starting app", gll.getGroupText(3)); //msg
	}
	
	@Test
	public void checkInvalidPattern() {		
		LogLine ll = new LogLine(LINES[2], 3);
		GroupedLogLine gll = new GroupedLogLine(ll);
		gll.groupData(Pattern.compile(PATTERN), 5);
		
		assertEquals(5, gll.getGroupCount());
		
		//check 3. line - did not match pattern
		assertEquals("3",               gll.getGroupText(0)); //line counter
		assertEquals("Strange message", gll.getGroupText(1)); //data
		assertEquals("",                gll.getGroupText(2)); //
		assertEquals("",                gll.getGroupText(3)); //
		assertEquals("",                gll.getGroupText(4)); //
	}
	
	@Test
	public void patternIsNull() {		
		LogLine ll = new LogLine(LINES[2], 3);
		GroupedLogLine gll = new GroupedLogLine(ll);
		gll.groupData(null, 2);
		
		assertEquals(2, gll.getGroupCount());
		
		//check 3. line - did not match pattern
		assertEquals("3",               gll.getGroupText(0)); //line counter
		assertEquals("Strange message", gll.getGroupText(1)); //data
		
		//access to illegal index
		assertEquals("", gll.getGroupText(2));
		assertEquals("", gll.getGroupText(-1));
	}
}
