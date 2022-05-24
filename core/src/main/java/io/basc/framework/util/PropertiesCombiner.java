package io.basc.framework.util;

import java.util.List;
import java.util.Properties;

import io.basc.framework.util.stream.Processor;

public class PropertiesCombiner implements Processor<List<Properties>, Properties, RuntimeException> {
	public static final PropertiesCombiner DEFAULT = new PropertiesCombiner();

	@Override
	public Properties process(List<Properties> list) throws RuntimeException {
		Properties properties = new Properties();
		if (list != null) {
			for (Properties props : list) {
				properties.putAll(props);
			}
		}
		return properties;
	}

}
