package run.soeasy.framework.core.spi;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.mapping.PropertyAccessor;
import run.soeasy.framework.core.convert.mapping.PropertyTemplate;

public final class SystemProperties implements PropertyTemplate {
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
	public PropertyAccessor get(String key) {
		return new SystemProperty(key);
	}

	@Override
	public Elements<PropertyAccessor> getElements() {
		return keys().map((key) -> get(key));
	}

	@Override
	public Elements<String> keys() {
		Elements<String> systemKeys = Elements.of(System.getProperties().stringPropertyNames());
		Elements<String> envKeys = Elements.of(System.getenv().keySet());
		return systemKeys.concat(envKeys).distinct();
	}
}
