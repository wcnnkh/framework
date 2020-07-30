package scw.logger;

import scw.core.GlobalPropertyFactory;
import scw.value.property.ExtendGetPropertyFactory;

public class LoggerPropertyFactory extends ExtendGetPropertyFactory {
	private static LoggerPropertyFactory instance = new LoggerPropertyFactory();

	public static LoggerPropertyFactory getInstance() {
		return instance;
	}

	private LoggerPropertyFactory() {
		super(true, true);
		addFirstBasePropertyFactory(GlobalPropertyFactory.getInstance());
	}

	@Override
	protected Object getExtendValue(String key) {
		String value = null;
		if (key.equalsIgnoreCase("default.logger.level")) {
			value = LoggerLevelUtils.DEFAULT_LEVEL.getName();
		} else if (key.equalsIgnoreCase("logger.rootPath")) {
			value = GlobalPropertyFactory.getInstance().getWorkPath();
		}
		return value;
	}
}
