package scw.logger;

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
					this.logger = LoggerFactory.getILoggerFactory().getLogger(getName(), getPlaceholder());
				}
			}
		}
		return logger;
	}
}
