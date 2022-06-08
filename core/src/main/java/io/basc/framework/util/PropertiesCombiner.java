package io.basc.framework.util;

import java.util.List;
import java.util.Properties;
import java.util.function.Function;

public class PropertiesCombiner implements Function<List<Properties>, Properties> {
	public static final PropertiesCombiner DEFAULT = new PropertiesCombiner();

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
