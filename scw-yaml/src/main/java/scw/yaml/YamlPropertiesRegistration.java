package scw.yaml;

import java.util.Properties;

import scw.beans.PropertiesRegistration;
import scw.core.instance.annotation.Configuration;
import scw.event.Observable;
import scw.io.ResourceUtils;

@Configuration
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
			return new ObservableYamlProperties(ResourceUtils
					.getResourceOperations().getResources(CONFIGURATION));
		}
		return null;
	}
}
