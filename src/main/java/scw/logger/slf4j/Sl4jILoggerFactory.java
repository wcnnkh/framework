package scw.logger.slf4j;

import scw.logger.ILoggerFactory;

public class Sl4jILoggerFactory implements ILoggerFactory {
	
	public Sl4jILoggerFactory(){
	}

	public scw.logger.Logger getLogger(String name) {
		return new Slf4jLoggerAdapter(name);
	}

	public void destroy() {
	}
}
