package unittests.data;

import static org.junit.Assert.*;

import org.junit.Test;

import data.LogFileFilter;


public class LogFilterFileTest {

	@Test
	public void checkConvertion() {
		LogFileFilter lff = new LogFileFilter("1:asdf");
		
		assertEquals(1, lff.getColumn());
		assertEquals("asdf", lff.getText());
	}
	
}
