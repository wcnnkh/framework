package scw.logger;


public interface ILoggerFactory{

	default Logger getLogger(String name) {
		return getLogger(name, null);
	}
	
	Logger getLogger(String name, String placeholder);
	
	void destroy();
}
