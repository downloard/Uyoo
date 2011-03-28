package unittests.setup;

import java.io.File;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

import data.LogFileFilter;

import setup.UyooSettings;


public class UyooSettingsTests {

	private static String UT_SETTINGS_FILE = "unittests/resources/UT_settings.xml";
	
	
	@Test
	public void saveFileName() {
		String filename = "c:\\asdf.file";
		
		//save
		{
			UTUyooSettings.FILE_NAME = UT_SETTINGS_FILE;
			UTUyooSettings set = new UTUyooSettings();
			
			set.saveFile(new File(filename));
		}
		
		//load
		{
			UTUyooSettings.FILE_NAME = UT_SETTINGS_FILE;
			UTUyooSettings set = new UTUyooSettings();
			
			assertEquals(1, set.getPersistentSettings().getFiles().getF().size() );
			assertEquals(filename, set.getPersistentSettings().getFiles().getF().get(0).getValue() );
		}
		
	}
	
	@AfterClass
	public static void deleteUTSettingsFile() {
		File f = new File(UT_SETTINGS_FILE);
		assertTrue( f.delete() );
	}
}


class UTUyooSettings extends UyooSettings {
	
	public static String FILE_NAME;

	public UTUyooSettings() {
	}
	
	@Override
	public String getFileName() {
		return FILE_NAME;
	}
}
