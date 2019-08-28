package scw.logger;

public abstract class AbstractILoggerFactory implements ILoggerFactory {
	public Logger getLogger(String name) {
		return getLogger(name, null);
	}
}
