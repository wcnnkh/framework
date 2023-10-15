package io.basc.framework.util.select;

import java.util.Properties;

import io.basc.framework.util.element.Elements;

public class PropertiesCombiner implements Selector<Properties> {
	public static final PropertiesCombiner INSTANCE = new PropertiesCombiner();

	@Override
	public Properties apply(Elements<? extends Properties> elements) {
		Properties properties = new Properties();
		for (Properties props : elements) {
			properties.putAll(props);
		}
		return properties;
	}
}
