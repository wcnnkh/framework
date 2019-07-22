package scw.core.logger;

public interface TraceLogger {
	boolean isTraceEnabled();

	void trace(String format, Object... args);
}
