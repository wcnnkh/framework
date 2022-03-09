package io.basc.framework.logger;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.factory.support.DefaultServiceLoaderFactory;

public class DynamicLoggerFactory extends JdkLoggerFactory {
	private volatile Map<String, DynamicLogger> loggerMap = new HashMap<String, DynamicLogger>();
	private final ServiceLoaderFactory serviceLoaderFactory;

	public DynamicLoggerFactory() {
		this(DefaultServiceLoaderFactory.INSTANCE);
	}

	public DynamicLoggerFactory(ServiceLoaderFactory serviceLoaderFactory) {
		this.serviceLoaderFactory = serviceLoaderFactory;
	}

	public final ServiceLoaderFactory getServiceLoaderFactory() {
		return serviceLoaderFactory;
	}

	private volatile ILoggerFactory loggerFactory;

	private ILoggerFactory loadLoggerFactory(String loggerName) {
		if (loggerFactory == null) {
			try {
				loggerFactory = serviceLoaderFactory.getServiceLoader(ILoggerFactory.class).first();
				if (loggerFactory == null) {
					return null;
				}

				Logger logger = getLogger(DynamicLoggerFactory.class.getName());
				logger.info("Use logger factory [" + loggerFactory + "]");
				for (Entry<String, DynamicLogger> entry : loggerMap.entrySet()) {
					Logger oldLogger = entry.getValue().getSource();
					try {
						entry.getValue().setSource(loggerFactory.getLogger(entry.getKey()));
						if (oldLogger instanceof Closeable) {
							((Closeable) oldLogger).close();
						}
					} catch (Throwable e) {
						logger.error(e, "name={}, logger={}", entry.getKey(), oldLogger);
					}
				}
			} catch (Throwable e) {
				getRootLogger().log(CustomLevel.DEBUG, e, () -> "Failed to load " + loggerName);
			}
		}
		return loggerFactory;
	}

	public ILoggerFactory getILoggerFactory() {
		return loggerFactory;
	}

	@Override
	public Logger getLogger(String name) {
		DynamicLogger logger = loggerMap.get(name);
		if (logger == null) {
			synchronized (loggerMap) {
				logger = loggerMap.get(name);
				if (logger == null) {
					ILoggerFactory loggerFactory = loadLoggerFactory(name);
					Logger source;
					if (loggerFactory == null) {
						source = super.getLogger(name);
					} else {
						source = loggerFactory.getLogger(name);
					}
					logger = new DynamicLogger(source);
					loggerMap.put(name, logger);
				}
			}
		}
		return logger;
	}

}
