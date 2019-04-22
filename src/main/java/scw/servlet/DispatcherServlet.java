package scw.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ServletApplication application;
	private ServletService servletService;

	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		servletService.service(req, res);
	}
	
	@Override
	public final void init(ServletConfig servletConfig) throws ServletException {
		try {
			application = new ServletApplication(servletConfig);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		application.init();
		this.servletService = ServletUtils.getServletService(application.getBeanFactory(),
				application.getPropertiesFactory(), application.getCommonApplication().getConfigPath(),
				application.getCommonApplication().getBeanFactory().getFilterNames());
	}

	@Override
	public void destroy() {
		application.destroy();
		super.destroy();
	}
}
