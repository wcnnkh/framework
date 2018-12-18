package scw.servlet;

import javax.servlet.http.HttpServletRequest;

public interface ServiceCallable<T extends HttpServletRequest>{
	void callable(HttpServerApplication httpServerApplication, T request);
}
