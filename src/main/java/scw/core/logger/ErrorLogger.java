package scw.core.logger;

public interface ErrorLogger {
	boolean isErrorEnabled();

	void error(String msg);

	void error(String format, Object... args);

	void error(String msg, Throwable t);
}
