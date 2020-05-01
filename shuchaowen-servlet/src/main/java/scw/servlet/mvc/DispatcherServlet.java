package scw.servlet.mvc;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import scw.application.Application;
import scw.application.CommonApplication;
import scw.core.GlobalPropertyFactory;
import scw.servlet.ServletUtils;

public class DispatcherServlet extends GenericServlet {
	private static final long serialVersionUID = 1L;
	private Application application;
	private ServletService servletService;
	private boolean reference = false;

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.reference = true;
		this.application = application;
	}

	public ServletService getServletService() {
		return servletService;
	}

	public void setServletService(ServletService servletService) {
		this.servletService = servletService;
	}

	public void setDefaultServletService(boolean force) {
		if (getServletService() == null || force) {
			if (getApplication() != null) {
				setServletService(getApplication().getBeanFactory()
						.getInstance(ServletService.class));
			}
		}
	}

	@Override
	public void service(ServletRequest req, ServletResponse resp)
			throws ServletException, IOException {
		if (req instanceof HttpServletRequest) {
			if (ServletUtils.isWebSocketRequest((HttpServletRequest) req)) {
				return;
			}
		}
		getServletService().service(req, resp);
	}

	@Override
	public final void init(ServletConfig servletConfig) throws ServletException {
		ServletConfigPropertyFactory propertyFactory = new ServletConfigPropertyFactory(
				servletConfig);
		if (getApplication() == null) {
			GlobalPropertyFactory.getInstance().setWorkPath(
					servletConfig.getServletContext().getRealPath("/"));
			this.application = new CommonApplication(
					propertyFactory.getConfigXml());
		}

		getApplication().getPropertyFactory().addBasePropertyFactory(
				propertyFactory);

		if (!reference) {
			getApplication().init();
		}

		setDefaultServletService(false);
	}

	@Override
	public void destroy() {
		if (!reference && getApplication() != null) {
			getApplication().destroy();
		}
		super.destroy();
	}
}
