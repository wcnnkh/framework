package scw.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.application.Application;
import scw.application.CommonApplication;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.servlet.http.HttpServletService;

public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerUtils.getLogger(DispatcherServlet.class);
	private Application application;
	private HttpServletService httpServletService;
	private boolean reference = false;

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.reference = true;
		this.application = application;
	}

	public HttpServletService getServletService() {
		return httpServletService;
	}

	public void setHttpServletService(HttpServletService httpServletService) {
		this.httpServletService = httpServletService;
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		getServletService().service(req, resp);
	}
	
	@Override
	public final void init(ServletConfig servletConfig) throws ServletException {
		logger.info("Servlet context realPath / in {}", servletConfig.getServletContext().getRealPath("/"));
		ServletConfigPropertyFactory propertyFactory = new ServletConfigPropertyFactory(
				servletConfig);
		if (getApplication() == null) {
			reference = false;
			this.application = new CommonApplication(
					propertyFactory.getConfigXml());
		}

		getApplication().getPropertyFactory().addLastBasePropertyFactory(
				propertyFactory);

		if (!reference) {
			getApplication().init();
		}
		
		if(httpServletService == null && getApplication() != null){
			this.httpServletService = getApplication().getBeanFactory().getInstance(HttpServletService.class);
		}
	}

	@Override
	public void destroy() {
		if (!reference && getApplication() != null) {
			getApplication().destroy();
		}
		super.destroy();
	}
}
