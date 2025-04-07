package run.soeasy.framework.core.env;

import run.soeasy.framework.core.transform.stereotype.Properties;
import run.soeasy.framework.core.transform.stereotype.Property;
import run.soeasy.framework.util.collections.Elements;

public class SystemProperties implements Properties {
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
