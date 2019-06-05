package scw.core.logger;

public interface WarnLogger {
	boolean isWarnEnabled();

	void warn(String format, Object... args);
}
