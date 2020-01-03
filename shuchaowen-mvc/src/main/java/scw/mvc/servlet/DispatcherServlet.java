package scw.mvc.servlet;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class DispatcherServlet extends GenericServlet {
	private static final long serialVersionUID = 1L;
	private ServletApplication application;
	private ServletService servletService;

	@Override
	public void service(ServletRequest req, ServletResponse resp) throws ServletException, IOException {
		servletService.service(req, resp);
	}

	@Override
	public final void init(ServletConfig servletConfig) throws ServletException {
		application = new ServletApplication(servletConfig);
		application.init();
		this.servletService = ServletUtils.getServletService(application.getBeanFactory(),
				application.getPropertyFactory());
	}

	@Override
	public void destroy() {
		application.destroy();
		super.destroy();
	}
}
