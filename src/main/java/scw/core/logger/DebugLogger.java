package scw.core.logger;

public interface DebugLogger {
	boolean isDebugEnabled();

	void debug(String msg);

	void debug(String format, Object... args);

	void debug(String msg, Throwable t);
}
