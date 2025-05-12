package run.soeasy.framework.core.collection.merge;

import java.util.Properties;

import run.soeasy.framework.core.collection.Elements;

public class PropertiesMerger implements Merger<Properties> {
	static final PropertiesMerger INSTANCE = new PropertiesMerger();

	@Override
	public Properties select(Elements<Properties> elements) {
		Properties properties = new Properties();
		for (Properties props : elements) {
			properties.putAll(props);
		}
		return properties;
	}
}