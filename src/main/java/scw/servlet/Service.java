package scw.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Service {
	void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Throwable;

	void sendError(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Throwable throwable);

	void destroy();
}
