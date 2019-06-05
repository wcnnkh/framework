package scw.core.logger;

public interface DebugLogger {
	boolean isDebugEnabled();

	void debug(String format, Object... args);
}
