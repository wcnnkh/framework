package scw.servlet;

import javax.servlet.ServletRequest;

import scw.core.StringVerification;
import scw.core.ValueFactory;
import scw.logger.DebugLogger;
import scw.logger.Logger;

public interface Request extends ServletRequest, DebugLogger, StringVerification, ValueFactory<String> {
	long getCreateTime();

	<T> T getBean(Class<T> type);
	
	Logger getLogger();
}
