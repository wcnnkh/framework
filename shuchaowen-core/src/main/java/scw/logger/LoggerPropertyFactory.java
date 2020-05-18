package scw.logger;

import java.util.Enumeration;

import scw.core.Constants;
import scw.core.GlobalPropertyFactory;
import scw.value.property.StringValuePropertyFactory;

public class LoggerPropertyFactory extends StringValuePropertyFactory {

	public LoggerPropertyFactory() {
		addBasePropertyFactory(Constants.PROPERTY_FACTORY);
		addBasePropertyFactory(GlobalPropertyFactory.getInstance());
	}

	@Override
	protected Enumeration<String> internalEnumerationKeys() {
		return null;
	}

	@Override
	protected String getStringValue(String key) {
		String value = null;
		if (key.equalsIgnoreCase("default.logger.level")) {
			value = LoggerLevelUtils.DEFAULT_LEVEL.name();
		} else if (key.equalsIgnoreCase("logger.rootPath")) {
			value = GlobalPropertyFactory.getInstance().getWorkPath();
		}
		return value;
	}

}
