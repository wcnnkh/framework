package scw.logger;

public interface LogService {
	Logger getLogger();

	boolean isLogEnabled();

	void log(Object format, Object... args);
}
