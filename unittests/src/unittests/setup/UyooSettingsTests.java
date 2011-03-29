package unittests.setup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Test;

import setup.UyooSettings;
import data.LogFileFilter;


public class UyooSettingsTests {

	private static String UT_SETTINGS_FILE = "unittests/resources/UT_settings.xml";
	
	
	@Test
	public void saveFileNames() {
		String filename1 = "c:\\asdf.file";
		String filename2 = "c:\\asdf2.file";
		
		// save
		{
			UTUyooSettings.FILE_NAME = UT_SETTINGS_FILE;
			UTUyooSettings set = new UTUyooSettings();
			
			set.saveFile(new File(filename1));
		}
		
		// load
		{
			UTUyooSettings.FILE_NAME = UT_SETTINGS_FILE;
			UTUyooSettings set = new UTUyooSettings();
			
			assertEquals(1, set.getPersistentSettings().getFiles().getF().size() );
			assertEquals(filename1, set.getPersistentSettings().getFiles().getF().get(0).getValue() );
						
			//add a 2. filename
			set.saveFile(new File(filename2));
			assertEquals(2, set.getPersistentSettings().getFiles().getF().size() );
		}
		
		// load again
		{
			UTUyooSettings.FILE_NAME = UT_SETTINGS_FILE;
			UTUyooSettings set = new UTUyooSettings();
			
			assertEquals(2, set.getPersistentSettings().getFiles().getF().size() );
			assertEquals(filename1, set.getPersistentSettings().getFiles().getF().get(0).getValue() );
			assertEquals(filename2, set.getPersistentSettings().getFiles().getF().get(1).getValue() );
		}
		
		// not needed to add again
		{
			UTUyooSettings.FILE_NAME = UT_SETTINGS_FILE;
			UTUyooSettings set = new UTUyooSettings();
			
			assertEquals(2, set.getPersistentSettings().getFiles().getF().size() );
			set.saveFile(new File(filename2));
			assertEquals(2, set.getPersistentSettings().getFiles().getF().size() );
			
		}
	}
	
	@Test
	public void savePatterns() {
		String pattern1 = "^1(.*)&";
		String pattern2 = "^2(.*)&";
		
		// save
		{
			UTUyooSettings.FILE_NAME = UT_SETTINGS_FILE;
			UTUyooSettings set = new UTUyooSettings();
			
			set.savePattern(pattern1);
		}
		
		// load
		{
			UTUyooSettings.FILE_NAME = UT_SETTINGS_FILE;
			UTUyooSettings set = new UTUyooSettings();
			
			assertEquals(1, set.getPersistentSettings().getPattern().getP().size() );
			assertEquals(pattern1, set.getPersistentSettings().getPattern().getP().get(0).getValue() );
			
			//add a 2. pattern
			set.savePattern(pattern2);
			assertEquals(2, set.getPersistentSettings().getPattern().getP().size() );
		}
		
		// load again
		{
			UTUyooSettings.FILE_NAME = UT_SETTINGS_FILE;
			UTUyooSettings set = new UTUyooSettings();
			
			assertEquals(2, set.getPersistentSettings().getPattern().getP().size() );
			assertEquals(pattern1, set.getPersistentSettings().getPattern().getP().get(0).getValue() );
			assertEquals(pattern2, set.getPersistentSettings().getPattern().getP().get(1).getValue() );
		}
		
		// not needed to add again
		{
			UTUyooSettings.FILE_NAME = UT_SETTINGS_FILE;
			UTUyooSettings set = new UTUyooSettings();
			
			assertEquals(2, set.getPersistentSettings().getPattern().getP().size() );
			set.savePattern(pattern2);
			assertEquals(2, set.getPersistentSettings().getPattern().getP().size() );
			
		}
	}
	
	@Test
	public void saveFilters() {
		LogFileFilter filter1 = new LogFileFilter("1:asdf");
		LogFileFilter filter2 = new LogFileFilter("2:asdf");
		
		// save
		{
			UTUyooSettings.FILE_NAME = UT_SETTINGS_FILE;
			UTUyooSettings set = new UTUyooSettings();
			
			set.saveFilter(filter1);
		}
		
		// load
		{
			UTUyooSettings.FILE_NAME = UT_SETTINGS_FILE;
			UTUyooSettings set = new UTUyooSettings();
			
			assertEquals(1, set.getPersistentSettings().getFilter().getF().size() );
			assertEquals(filter1, new LogFileFilter( set.getPersistentSettings().getFilter().getF().get(0).getValue() ) );
			
			//add a 2. pattern
			set.saveFilter(filter2);
			assertEquals(2, set.getPersistentSettings().getFilter().getF().size() );
		}
		
		// load again
		{
			UTUyooSettings.FILE_NAME = UT_SETTINGS_FILE;
			UTUyooSettings set = new UTUyooSettings();
			
			assertEquals(2, set.getPersistentSettings().getFilter().getF().size() );
			assertEquals(filter1, new LogFileFilter( set.getPersistentSettings().getFilter().getF().get(0).getValue() ) );
			assertEquals(filter2, new LogFileFilter( set.getPersistentSettings().getFilter().getF().get(1).getValue() ) );
		}
		
		// not needed to add again
		{
			UTUyooSettings.FILE_NAME = UT_SETTINGS_FILE;
			UTUyooSettings set = new UTUyooSettings();
			
			assertEquals(2, set.getPersistentSettings().getFilter().getF().size() );
			set.saveFilter(filter1);
			assertEquals(2, set.getPersistentSettings().getFilter().getF().size() );
			
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
