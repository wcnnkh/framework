package scw.core.logger;

public interface Logger extends DebugLogger, WarnLogger, ErrorLogger, TraceLogger {

	boolean isInfoEnabled();

	void info(String msg);

	void info(String format, Object... args);

	void info(String msg, Throwable t);

}
