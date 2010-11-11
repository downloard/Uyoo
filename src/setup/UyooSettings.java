package setup;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;

import generated.Settings;


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
	
	private UyooSettings() {
		loadConfigFile();
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
			m.marshal(m_settings, new File(SETTINGS_FILE));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void loadConfigFile() {
		File file = new File(SETTINGS_FILE);
		if(!file.exists())
		{
			return;
		}

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
