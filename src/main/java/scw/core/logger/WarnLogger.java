package scw.core.logger;

public interface WarnLogger {
	boolean isWarnEnabled();

	void warn(String msg);

	void warn(String format, Object... args);

	void warn(String msg, Throwable t);
}
