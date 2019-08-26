package scw.servlet;

import javax.servlet.ServletResponse;

import scw.logger.LogService;

public interface Response extends ServletResponse, LogService{
	void write(Object obj) throws Exception;
}
