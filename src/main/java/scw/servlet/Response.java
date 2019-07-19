package scw.servlet;

import javax.servlet.ServletResponse;

import scw.logger.DebugLogger;
import scw.logger.Logger;

public interface Response extends ServletResponse, DebugLogger{
	void write(Object obj) throws Exception;
	
	Logger getLogger();
}
