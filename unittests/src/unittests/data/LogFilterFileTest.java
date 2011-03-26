package unittests.data;

import static org.junit.Assert.*;

import java.util.regex.Pattern;

import org.junit.Test;

import data.GroupedLogLine;
import data.LogFileFilter;
import data.LogLine;


public class LogFilterFileTest {
	
	static private String PATTERN                = "^(.*?)-(.*?)-(.*?)-(.*?)$";
	
	static private String[] LINES = {"08.09.29-INFO-Main.cpp-Starting app",
									"08.09.29-DEBUG-Main.cpp-Load HMI",
									"Strange message",
									"08.09.29-DEBUG-Main.cpp-Load y.lib",
									"08.09.29-Debug-Main.cpp-Load z.lib",
									"08.09.29-ERROR-Main.cpp-Cannot find a.lib"};

	@Test
	public void lineFilter() {
		LogFileFilter lff = new LogFileFilter("1:asdf");
		
		assertEquals(1, lff.getColumn());
		assertEquals("asdf", lff.getText());
	}
	
	@Test
	public void allLineFilter() {
		LogFileFilter lff = new LogFileFilter("\"asdf\"");
		
		assertEquals(LogFileFilter.ALL_COLUMNS, lff.getColumn());
		assertEquals("asdf", lff.getText());
	}
	
	@Test
	public void checkEquals() {
		LogFileFilter lff = new LogFileFilter("1:asdf");
		LogFileFilter lff2 = new LogFileFilter("1:asdf");
		
		assertEquals(lff, lff2);
		
		lff = new LogFileFilter("1:asdf");
		lff2 = new LogFileFilter("2:asdf");
		
		assertNotSame(lff, lff2);
		
		lff = new LogFileFilter("2:abcd");
		lff2 = new LogFileFilter("2:asdf");
		
		assertNotSame(lff, lff2);
	}
	
	@Test
	public void match_WithoutPattern_caseInsensitive() {
		GroupedLogLine ll = new GroupedLogLine(new LogLine(LINES[0], 0));
		ll.groupData(null, 2);
		
		LogFileFilter lff = new LogFileFilter("1:info");
		assertTrue( lff.matchesFilter(ll, false) );	
	}
	
	@Test
	public void noMatch_WithoutPattern_caseInsensitive() {
		GroupedLogLine ll = new GroupedLogLine(new LogLine(LINES[0], 0));
		ll.groupData(null, 2);
		
		LogFileFilter lff = new LogFileFilter("1:asdf");
		assertFalse( lff.matchesFilter(ll, false) );	
	}
	
	@Test
	public void noMatch_WithoutPattern_caseSensitive() {
		GroupedLogLine ll = new GroupedLogLine(new LogLine(LINES[0], 0));
		ll.groupData(null, 2);
		
		LogFileFilter lff = new LogFileFilter("1:info");
		assertFalse( lff.matchesFilter(ll, true) );	
	}
	
	@Test
	public void match_WithoutPattern_caseInsensitive_allColumns() {
		GroupedLogLine ll = new GroupedLogLine(new LogLine(LINES[0], 0));
		ll.groupData(null, 2);
		
		LogFileFilter lff = new LogFileFilter("\"info\"");
		assertTrue( lff.matchesFilter(ll, false) );	
	}
	
	@Test
	public void match_withPattern_caseInsensitive() {
		GroupedLogLine ll = new GroupedLogLine(new LogLine(LINES[0], 0));
		ll.groupData(Pattern.compile(PATTERN), 5);
		
		//(1     ) (2 ) (3     ) (4         )
		//08.09.29-INFO-Main.cpp-Starting app
		
		LogFileFilter lff = new LogFileFilter("2:info");
		assertTrue( lff.matchesFilter(ll, false) );	
	}
	
	@Test
	public void match_withPattern_caseSensitive() {
		GroupedLogLine ll = new GroupedLogLine(new LogLine(LINES[0], 0));
		ll.groupData(Pattern.compile(PATTERN), 5);
		
		//(1     ) (2 ) (3     ) (4         )
		//08.09.29-INFO-Main.cpp-Starting app
		
		LogFileFilter lff = new LogFileFilter("2:info");
		assertFalse( lff.matchesFilter(ll, true) );	
	}
	
	@Test
	public void withPattern() {
		GroupedLogLine ll = new GroupedLogLine(new LogLine(LINES[0], 0));
		ll.groupData(Pattern.compile(PATTERN), 5);
		
		//(1     ) (2 ) (3     ) (4         )
		//08.09.29-INFO-Main.cpp-Starting app
		
		LogFileFilter lff = new LogFileFilter("2:start");
		assertFalse( lff.matchesFilter(ll, false) );
		
		//but matches in whole line
		lff = new LogFileFilter("\"start\"");
		assertTrue( lff.matchesFilter(ll, false) );
		
		//but not if case sensitive
		assertFalse( lff.matchesFilter(ll, true) );
	}

}
