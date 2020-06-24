package scw.logger;

import scw.beans.Destroy;

public interface ILoggerFactory extends Destroy{

	Logger getLogger(String name);
	
	Logger getLogger(String name, String placeholder);
	
	void destroy();
}
