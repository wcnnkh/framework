package scw.logger;


public interface ILoggerFactory{

	Logger getLogger(String name);
	
	Logger getLogger(String name, String placeholder);
	
	void destroy();
}
