package scw.logger;

import scw.core.reflect.ReflectionUtils;

/**
 * 懒加载的logger
 * 
 * @author shuchaowen
 *
 */
public final class LazyLogger extends AbstractLazyLogger {
	private volatile Logger logger;

	public LazyLogger(String name, String placeholder) {
		super(name, placeholder);
	}

	protected Logger getLogger() {
		if (logger == null) {
			synchronized (this) {
				if (logger == null) {
					try {
						logger = (Logger) ReflectionUtils.invokeStaticMethod("scw.logger.LoggerFactory", "getLogger",
								new Class<?>[] { String.class, String.class }, getName(), getPlaceholder());
					} catch (Throwable e) {
						throw new RuntimeException(getName(), e);
					}
				}
			}
		}
		return logger;
	}
}
