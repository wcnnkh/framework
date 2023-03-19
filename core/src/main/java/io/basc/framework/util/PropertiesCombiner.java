package io.basc.framework.util;

import java.util.List;
import java.util.Properties;

public class PropertiesCombiner implements Selector<Properties> {
	public static final PropertiesCombiner INSTANCE = new PropertiesCombiner();

	@Override
	public Properties apply(List<Properties> list) throws RuntimeException {
		Properties properties = new Properties();
		if (list != null) {
			for (Properties props : list) {
				properties.putAll(props);
			}
		}
		return properties;
	}
}
