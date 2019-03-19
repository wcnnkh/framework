package scw.logger;

public interface ILoggerFactory {

	Logger getLogger(String name);

	void destroy();
}
