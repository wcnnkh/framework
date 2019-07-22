package scw.core.logger;

public interface ErrorLogger {
	boolean isErrorEnabled();

	void error(Throwable e, String msg, Object ...args);
}
