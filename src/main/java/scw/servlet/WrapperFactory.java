package scw.servlet;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public interface WrapperFactory {
	Request wrapperRequest(ServletRequest request, ServletResponse response) throws Exception;

	Response wrapperResponse(ServletRequest request, ServletResponse response) throws Exception;
}
