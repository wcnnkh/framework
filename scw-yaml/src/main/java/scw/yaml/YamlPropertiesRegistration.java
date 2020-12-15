package scw.yaml;

import java.util.Properties;

import scw.beans.PropertiesRegistration;
import scw.core.instance.annotation.SPI;
import scw.event.Observable;
import scw.io.ResourceUtils;

@SPI
public class YamlPropertiesRegistration implements PropertiesRegistration {
	private static final String CONFIGURATION = "application.yaml";

	public String getPrefix() {
		return null;
	}

	public boolean isFormat() {
		return true;
	}

	public Observable<Properties> getProperties() {
		if (ResourceUtils.getResourceOperations().isExist(CONFIGURATION)) {
			Observable<Properties> properies = new YamlProperties(CONFIGURATION);
			properies.register();
			return properies;
		}
		return null;
	}

	public boolean isAutoRefresh() {
		return true;
	}
}
