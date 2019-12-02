package scw.core.resource;

public abstract class AbstractResourceLookup implements ResourceLookup {
	private final boolean loggerEnable;

	/**
	 * @param loggerEnable
	 *            是否启用日志，
	 *            此参数存在的原因是因为ResourceUtils和LoggerUtils相互依赖导致ClassLoader异常
	 */
	public AbstractResourceLookup(boolean loggerEnable) {
		this.loggerEnable = loggerEnable;
	}

	public final boolean isLoggerEnable() {
		return loggerEnable;
	}
}
