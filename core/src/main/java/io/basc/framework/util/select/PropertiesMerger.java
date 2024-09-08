package io.basc.framework.util.select;

import java.util.Properties;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Merger;

public class PropertiesMerger implements Merger<Properties> {
	public static final PropertiesMerger INSTANCE = new PropertiesMerger();

	@Override
	public Properties merge(Elements<? extends Properties> elements) {
		Properties properties = new Properties();
		for (Properties props : elements) {
			properties.putAll(props);
		}
		return properties;
	}
}
