package scw.core.logger;

public interface Logger extends DebugLogger, WarnLogger, ErrorLogger, TraceLogger {

	boolean isInfoEnabled();
	
	void info(String format, Object... args);
	
	void info(Throwable e, String msg, Object ...args);

	void trace(Throwable e, String msg, Object ...args);
	
	void warn(Throwable e, String msg, Object ...args);
	
	void error(String format, Object... args);
	
	void debug(Throwable e, String msg, Object ...args)	;
}
