package io.basc.framework.util;

import java.util.List;
import java.util.Properties;

public class PropertiesCombiner implements Combiner<Properties> {
	public static final PropertiesCombiner DEFAULT = new PropertiesCombiner();

	@Override
	public Properties combine(List<Properties> list) {
		Properties properties = new Properties();
		if (list != null) {
			for (Properties props : list) {
				properties.putAll(props);
			}
		}
		return properties;
	}

}
