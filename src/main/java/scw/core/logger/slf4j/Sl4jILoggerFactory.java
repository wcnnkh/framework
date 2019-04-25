package scw.core.logger.slf4j;

import scw.core.logger.ILoggerFactory;

public class Sl4jILoggerFactory implements ILoggerFactory {
	
	public Sl4jILoggerFactory(){
	}

	public scw.core.logger.Logger getLogger(String name) {
		return new Slf4jLoggerAdapter(name);
	}

	public void destroy() {
	}
}
