package io.basc.framework.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public interface ServletService {
	void service(ServletRequest request, ServletResponse response)
			throws ServletException, IOException;
}
