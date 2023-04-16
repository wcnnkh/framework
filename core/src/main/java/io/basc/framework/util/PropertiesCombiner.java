package io.basc.framework.util;

import java.util.Properties;

public class PropertiesCombiner implements Selector<Properties> {
	public static final PropertiesCombiner INSTANCE = new PropertiesCombiner();

	@Override
	public Properties apply(Elements<Properties> elements) {
		Properties properties = new Properties();
		for (Properties props : elements) {
			properties.putAll(props);
		}
		return properties;
	}
}
