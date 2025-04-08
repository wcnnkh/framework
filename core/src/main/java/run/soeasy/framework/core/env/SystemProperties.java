package run.soeasy.framework.core.env;

import run.soeasy.framework.core.transform.stereotype.Properties;
import run.soeasy.framework.core.transform.stereotype.Property;
import run.soeasy.framework.util.collection.Elements;

public final class SystemProperties implements Properties {
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
