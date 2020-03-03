package scw.servlet.mvc;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import scw.application.CommonApplication;
import scw.core.utils.SystemPropertyUtils;
import scw.servlet.ServletUtils;

public class DispatcherServlet extends GenericServlet {
	private static final long serialVersionUID = 1L;
	private CommonApplication commonApplication;
	private ServletService servletService;
	private boolean reference = false;

	public CommonApplication getCommonApplication() {
		return commonApplication;
	}

	public void setCommonApplication(CommonApplication commonApplication) {
		this.reference = true;
		this.commonApplication = commonApplication;
	}

	public ServletService getServletService() {
		return servletService;
	}

	public void setServletService(ServletService servletService) {
		this.servletService = servletService;
	}
	
	public void setDefaultServletService(boolean force){
		if(getServletService() == null || force){
			if(getCommonApplication() != null){
				setServletService(getCommonApplication().getInstance(ServletService.class));
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
		if (getCommonApplication() == null) {
			SystemPropertyUtils.setWorkPath(servletConfig.getServletContext()
					.getRealPath("/"));
			this.commonApplication = new CommonApplication(
					propertyFactory.getConfigXml());
		}

		getCommonApplication().addPropertyFactory(propertyFactory);

		if (!reference) {
			getCommonApplication().init();
		}
		
		setDefaultServletService(false);
	}

	@Override
	public void destroy() {
		if (!reference && getCommonApplication() != null) {
			getCommonApplication().destroy();
		}
		super.destroy();
	}
}
