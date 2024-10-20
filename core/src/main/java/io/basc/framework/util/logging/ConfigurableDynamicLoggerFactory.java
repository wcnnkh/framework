package io.basc.framework.util.logging;

import io.basc.framework.util.Receipt;
import io.basc.framework.util.Reloadable;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.spi.Configurable;
import io.basc.framework.util.spi.ServiceLoaderDiscovery;
import lombok.NonNull;

public class ConfigurableDynamicLoggerFactory extends DynamicLoggerFactory implements Configurable, Reloadable {
	
	public ConfigurableDynamicLoggerFactory(@NonNull LoggerFactory loggerFactory) {
		super(loggerFactory);
	}

	private volatile ServiceLoader<? extends LoggerFactory> loggerFactoryServiceLoader;
	private volatile ServiceLoader<? extends LevelFactory> levelFactoryServiceLoader;

	@Override
	public void reload() {
		synchronized (this) {
			if (loggerFactoryServiceLoader != null) {
				loggerFactoryServiceLoader.reload();
			}

			if (levelFactoryServiceLoader != null) {
				levelFactoryServiceLoader.reload();
			}
		}
	}

	@Override
	public Receipt doConfigure(ServiceLoaderDiscovery discovery) {
		synchronized (this) {
			if (loggerFactoryServiceLoader == null) {
				loggerFactoryServiceLoader = discovery.getServiceLoader(LoggerFactory.class);
			}

			if (levelFactoryServiceLoader == null) {
				levelFactoryServiceLoader = discovery.getServiceLoader(LevelFactory.class);
			}
			return Receipt.success();
		}
		return Receipt.fail();
	}
}
