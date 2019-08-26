package scw.core;

import scw.core.utils.SystemPropertyUtils;

public class SystemPropertyFactory implements PropertyFactory {
	public static final SystemPropertyFactory INSTANCE = new SystemPropertyFactory();

	public String getProperty(String key) {
		return SystemPropertyUtils.getProperty(key);
	}

}
