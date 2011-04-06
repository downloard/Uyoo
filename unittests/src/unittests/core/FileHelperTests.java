package unittests.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import core.FileHelper;


public class FileHelperTests {
	
	@Test
	public void test0B() {
		long size = 0;
		String expected = "0b";
		assertEquals(expected, FileHelper.getFormattedFileSize(size));
	}
	
	@Test
	public void test42B() {
		long size = 42;
		String expected = "42b";
		assertEquals(expected, FileHelper.getFormattedFileSize(size));
	}
	
	@Test
	public void test1023B() {
		long size = 1023;
		String expected = "1023b";
		assertEquals(expected, FileHelper.getFormattedFileSize(size));
	}
	
	@Test
	public void test1024B() {
		long size = 1024;
		String expected = "1KB";
		assertEquals(expected, FileHelper.getFormattedFileSize(size));
	}
	
	@Test
	public void test1116B() {
		long size = 1116;
		String expected = "1.08KB";
		assertEquals(expected, FileHelper.getFormattedFileSize(size));
	}
	
	@Test
	public void test1200B() {
		long size = 1200;
		String expected = "1.17KB";
		assertEquals(expected, FileHelper.getFormattedFileSize(size));
	}
	
	@Test
	public void test1MB() {
		long size = 1024*1024;
		String expected = "1MB";
		assertEquals(expected, FileHelper.getFormattedFileSize(size));
	}
	
	@Test
	public void test11MB() {
		long size = 1153435;
		String expected = "1.10MB";
		assertEquals(expected, FileHelper.getFormattedFileSize(size));
	}
	
	@Test
	public void test20MB() {
		long size = 21495808;
		String expected = "20.50MB";
		assertEquals(expected, FileHelper.getFormattedFileSize(size));
	}
	
	
}