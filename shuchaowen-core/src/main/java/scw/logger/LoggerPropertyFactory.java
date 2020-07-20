package scw.logger;

import scw.core.Constants;
import scw.core.GlobalPropertyFactory;
import scw.value.property.ExtendGetPropertyFactory;

public class LoggerPropertyFactory extends ExtendGetPropertyFactory {

	public LoggerPropertyFactory() {
		super(true, true);
		addFirstBasePropertyFactory(Constants.PROPERTY_FACTORY);
		addFirstBasePropertyFactory(GlobalPropertyFactory.getInstance());
	}

	@Override
	protected Object getExtendValue(String key) {
		String value = null;
		if (key.equalsIgnoreCase("default.logger.level")) {
			value = LoggerLevelUtils.DEFAULT_LEVEL.name();
		} else if (key.equalsIgnoreCase("logger.rootPath")) {
			value = GlobalPropertyFactory.getInstance().getWorkPath();
		}
		return value;
	}

}
