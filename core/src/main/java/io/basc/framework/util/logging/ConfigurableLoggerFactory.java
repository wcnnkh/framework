package io.basc.framework.util.logging;

import io.basc.framework.util.spi.ConfigurableServices;

public class ConfigurableLoggerFactory extends ConfigurableServices<LoggerFactory> implements LoggerFactory {

	public ConfigurableLoggerFactory() {
		setServiceClass(LoggerFactory.class);
	}

	@Override
	public Logger getLogger(String name) {
		for(LoggerFactory factory : getElements()) {
			Logger logger = factory.getLogger(name);
			if(logger != null) {
				return logger;
			}
		}
		return null;
	}

}
