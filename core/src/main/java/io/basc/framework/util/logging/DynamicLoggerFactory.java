package io.basc.framework.util.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.util.Assert;
import io.basc.framework.util.Receipt;
import io.basc.framework.util.Reloadable;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.spi.Configurable;
import io.basc.framework.util.spi.ServiceLoaderDiscovery;
import lombok.NonNull;

public class DynamicLoggerFactory implements ILoggerFactory, Reloadable, Configurable {
	@NonNull
	private volatile ILoggerFactory loggerFactory;
	private volatile Map<String, DynamicLogger> loggerMap = new HashMap<String, DynamicLogger>();
	private volatile LevelFactory levelFactory;

	public DynamicLoggerFactory(@NonNull ILoggerFactory loggerFactory) {
		Assert.requiredArgument(loggerFactory != null, "loggerFactory");
		this.loggerFactory = loggerFactory;
	}

	@Override
	public DynamicLogger getLogger(String name) {
		DynamicLogger logger = loggerMap.get(name);
		if (logger == null) {
			synchronized (this) {
				logger = loggerMap.get(name);
				if (logger == null) {
					Logger source = loggerFactory.getLogger(name);
					logger = createDynamicLogger(name, source);
					loggerMap.put(name, logger);
				}
			}
		}
		return logger;
	}

	protected DynamicLogger createDynamicLogger(String name, Logger source) {
		if (source instanceof DynamicLogger) {
			return (DynamicLogger) source;
		} else {
			return new DynamicLogger(source);
		}
	}

	public ILoggerFactory getLoggerFactory() {
		synchronized (this) {
			return loggerFactory;
		}
	}

	private volatile ServiceLoader<ILoggerFactory> serviceLoader;

	@Override
	public void reload() {
		synchronized (this) {
			if (this.serviceLoader != null) {
				this.serviceLoader.reload();
				ILoggerFactory loggerFactory = this.serviceLoader.first();
				if (loggerFactory != null) {
					this.loggerFactory = loggerFactory;
				}
			}

			for (Entry<String, DynamicLogger> entry : this.loggerMap.entrySet()) {
				reload(entry.getKey(), entry.getValue());
			}
		}
	}

	protected void reload(String name, DynamicLogger dynamicLogger) {
		if (loggerFactory != null) {
			Logger logger = loggerFactory.getLogger(name);
			if (logger != null) {
				dynamicLogger.setSource(logger);
			}
		}
	}

	public void setLoggerFactory(@NonNull ILoggerFactory loggerFactory) {
		Assert.requiredArgument(loggerFactory != null, "loggerFactory");
		synchronized (this) {
			if (this.loggerFactory == loggerFactory) {
				return;
			}

			this.loggerFactory = loggerFactory;
			reload();
		}
	}

	@Override
	public Receipt doConfigure(ServiceLoaderDiscovery discovery) {
		if (this.serviceLoader == null) {
			synchronized (this) {
				if (this.serviceLoader == null) {
					try {
						ServiceLoader<ILoggerFactory> serviceLoader = discovery
								.getServiceProvider(ILoggerFactory.class);
						ILoggerFactory loggerFactory = serviceLoader.first();
						if (loggerFactory != null) {
							setLoggerFactory(loggerFactory);
						}
					} catch (Throwable e) {
						return Receipt.fail(e);
					}
				}
			}
		}
		return Receipt.success();
	}
	
	
}