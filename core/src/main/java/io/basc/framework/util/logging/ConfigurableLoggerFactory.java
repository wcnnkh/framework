package io.basc.framework.util.logging;

import io.basc.framework.util.exchange.Receipt;
import io.basc.framework.util.spi.Configurable;
import io.basc.framework.util.spi.ServiceLoaderDiscovery;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class ConfigurableLoggerFactory extends LoggerRegistry implements Configurable {
	@NonNull
	private volatile LoggerFactory loggerFactory = new JdkLoggerFactory();

	@Override
	public Logger getLogger(String name) {
		Logger logger = super.getLogger(name);
		if (logger == null) {
			synchronized (this) {
				logger = super.getLogger(name);
				if (logger == null) {
					logger = loggerFactory.getLogger(name);
					if (logger != null) {
						logger = setLogger(name, logger);
					}
				}
			}
		}
		return logger;
	}

	@Override
	public void reload() {
		synchronized (this) {
			for (FacadeLogger facadeLogger : getLoggers()) {
				Logger logger = loggerFactory.getLogger(facadeLogger.getName());
				if (logger != null) {
					facadeLogger.setSource(logger);
				}
			}
			super.reload();
		}
	}

	public void setLoggerFactory(@NonNull LoggerFactory loggerFactory) {
		synchronized (this) {
			if (this.loggerFactory == loggerFactory) {
				return;
			}
			this.loggerFactory = loggerFactory;
			reload();
		}
	}

	@Override
	public Receipt doConfigure(@NonNull ServiceLoaderDiscovery discovery) {
		try {
			LoggerFactory loggerFactory = discovery.getServiceLoader(LoggerFactory.class).first();
			if (loggerFactory == null) {
				setLoggerFactory(loggerFactory);
			}
			return Receipt.SUCCESS;
		} catch (Throwable e) {
			return Receipt.FAILURE;
		}
	}
}
