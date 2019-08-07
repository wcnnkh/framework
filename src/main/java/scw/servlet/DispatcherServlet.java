package scw.servlet;

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
	public void service(ServletRequest req, ServletResponse resp)
			throws ServletException, IOException {
		servletService.service(req, resp);
	}

	@Override
	public final void init(ServletConfig servletConfig) throws ServletException {
		try {
			application = new ServletApplication(servletConfig);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		application.init();
		this.servletService = ServletUtils.getServletService(application
				.getBeanFactory(), application.getPropertyFactory(),
				application.getCommonApplication().getConfigPath(), application
						.getCommonApplication().getBeanFactory()
						.getFilterNames());
	}

	@Override
	public void destroy() {
		application.destroy();
		super.destroy();
	}
}
