package scw.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.application.Application;
import scw.application.CommonApplication;
import scw.core.GlobalPropertyFactory;
import scw.servlet.http.HttpServletService;

public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
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
		ServletConfigPropertyFactory propertyFactory = new ServletConfigPropertyFactory(
				servletConfig);
		if (getApplication() == null) {
			GlobalPropertyFactory.getInstance().setWorkPath(
					servletConfig.getServletContext().getRealPath("/"));
			reference = false;
			this.application = new CommonApplication(
					propertyFactory.getConfigXml());
		}

		getApplication().getPropertyFactory().addBasePropertyFactory(
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
