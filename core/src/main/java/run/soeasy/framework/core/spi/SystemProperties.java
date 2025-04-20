package run.soeasy.framework.core.spi;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.transform.mapping.PropertySource;
import run.soeasy.framework.core.transform.mapping.Property;

public final class SystemProperties implements PropertySource {
	private static volatile SystemProperties instance;

	public static SystemProperties getInstance() {
		if (instance == null) {
			synchronized (SystemProperties.class) {
				if (instance == null) {
					instance = new SystemProperties();
				}
			}
		}
		return instance;
	}

	private SystemProperties() {
	}

	@Override
	public Property get(String key) {
		return new SystemProperty(key);
	}

	@Override
	public Elements<Property> getElements() {
		return keys().map((key) -> get(key));
	}

	@Override
	public Elements<String> keys() {
		Elements<String> systemKeys = Elements.of(System.getProperties().stringPropertyNames());
		Elements<String> envKeys = Elements.of(System.getenv().keySet());
		return systemKeys.concat(envKeys).distinct();
	}
}
