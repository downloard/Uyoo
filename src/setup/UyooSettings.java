package setup;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;

import core.UyooLogger;
import data.LogFileFilter;

import generated.ObjectFactory;
import generated.Settings;
import generated.Settings.Files.F;
import generated.Settings.Pattern.P;


public class UyooSettings {
	
	private static String       SETTINGS_FILE = "resources/settings.xml";
	private static UyooSettings m_instance;
	
	private Settings m_settings;
	
	public static UyooSettings getInstance() {
		if (m_instance == null) {
			synchronized (UyooSettings.class) {
				if (m_instance == null) {
					m_instance = new UyooSettings();
				}
			}
		}
		return m_instance;
	}
	
	protected UyooSettings() {
		loadConfigFile();
	}
	
	public String getFileName() {
		return SETTINGS_FILE;
	}
	
	public String getApplicationName() {
		return "Uyoo";
	}
	
	public String getVersionNumber() {
		Package p = getClass().getPackage();
		if (p != null) {
			return p.getImplementationVersion();
		} else {
			return "<Version>";
		}
	}
	
	public Settings getPersistentSettings() {
		return m_settings;
	}
	
	public void saveConfigFile() {
		try {
			JAXBContext ctx = JAXBContext.newInstance(Settings.class);
			javax.xml.bind.Marshaller m = ctx.createMarshaller();
			m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(m_settings, new File(getFileName()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void loadConfigFile() {
		File file = new File(getFileName());
		if(!file.exists())
		{
			createDefaultData();
		} else {
			try
			{
				JAXBContext context = JAXBContext.newInstance(Settings.class);
				javax.xml.bind.Unmarshaller um = context.createUnmarshaller();
				
				JAXBElement<Settings> configElement = um.unmarshal(new StreamSource(file), Settings.class);
				m_settings = configElement.getValue();
			}
			catch(JAXBException ex)
			{
				ex.printStackTrace();
			}
		}
	}

	private void createDefaultData() {
		ObjectFactory factory = new ObjectFactory();
		
		m_settings = factory.createSettings();
		
		//add files
		m_settings.setFiles( factory.createSettingsFiles() );
		
		//add filters
		m_settings.setFilter( factory.createSettingsFilter() );
		
		//add pattern
		m_settings.setPattern( factory.createSettingsPattern() );
	}

	public void saveFilter(LogFileFilter filter) {
		//check if pattern already exist
		List<generated.Settings.Filter.F> lstFilter = getPersistentSettings().getFilter().getF();
		//check if already in persistent data
		boolean needToAdd = true;
		for(generated.Settings.Filter.F next : lstFilter) {
			if (next.getValue().equals(filter.toString())) {
				needToAdd = false;
				break;
			}
		}
		
		if (needToAdd == true) {
			UyooLogger.getLogger().info("Save filter " + filter);
			
			//add to settings
			generated.Settings.Filter.F f = new ObjectFactory().createSettingsFilterF();
			f.setValue(filter.toString());
			lstFilter.add(f);

			//save persistent
			saveConfigFile();
		}		
	}

	public void savePattern(String selectedPattern) {
		//check if pattern already exist
		List<P> pattern = getPersistentSettings().getPattern().getP();
		//check if already in persistent data
		boolean needToAdd = true;
		for(P next : pattern) {
			if (next.getValue().equals( selectedPattern )) {
				needToAdd = false;
				break;
			}
		}
		
		if (needToAdd == true) {
			UyooLogger.getLogger().info("Save pattern " + selectedPattern);
			
			//add to settings
			P p = new ObjectFactory().createSettingsPatternP();
			p.setValue(selectedPattern);
			pattern.add(p);

			//save persistent
			saveConfigFile();
		}		
	}

	public void saveFile(File file) {
		//add file to settings
		List<F> files = getPersistentSettings().getFiles().getF();
		//check if already in persistent data
		boolean needToAdd = true;
		for(F next : files) {
			File nextFile = new File(next.getValue());
			if (nextFile.equals(file)) {
				needToAdd = false;
				break;
			}
		}
		
		if (needToAdd == true) {
			//add to settings
			F f = new ObjectFactory().createSettingsFilesF();
			f.setValue(file.getAbsolutePath());
			files.add(f);

			//save persistent
			saveConfigFile();
		}		
	}
}
