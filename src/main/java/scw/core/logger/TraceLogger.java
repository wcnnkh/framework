package scw.core.logger;

public interface TraceLogger {
	boolean isTraceEnabled();

	void trace(String msg);

	void trace(String format, Object... args);

	void trace(String msg, Throwable t);
}
