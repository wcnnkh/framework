package scw.servlet;

import javax.servlet.ServletResponse;

import scw.core.logger.DebugLogger;
import scw.core.logger.Logger;

public interface Response extends ServletResponse, DebugLogger{
	void write(Object obj) throws Exception;
	
	Logger getLogger();
}
