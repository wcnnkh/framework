package scw.logger;


public class JdkLoggerFactory implements ILoggerFactory{
	
	@Override
	public Logger getLogger(String name, String placeholder) {
		java.util.logging.Logger logger = java.util.logging.Logger.getLogger(name);
		return new JdkLogger(logger, name, placeholder);
	}

	@Override
	public void destroy() {
	}

}
