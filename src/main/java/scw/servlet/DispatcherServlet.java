package scw.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.application.CommonApplication;

public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private CommonApplication commonApplication;
	private Service service;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			service.service(req, resp);
		} catch (Throwable e) {
			service.sendError(req, resp, e);
		}
	}

	@Override
	public final void init(ServletConfig servletConfig) throws ServletException {
		commonApplication = ServletUtils.createCommonApplication(servletConfig);
		commonApplication.init();
		service = ServletUtils.createService(commonApplication);
		super.init(servletConfig);
	}

	@Override
	public void destroy() {
		commonApplication.destroy();
		service.destroy();
		super.destroy();
	}
}
