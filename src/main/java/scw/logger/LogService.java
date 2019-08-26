package scw.logger;

public interface LogService {
	Logger getLogger();

	boolean isLogEnabled();

	void log(String format, Object... args);
}
