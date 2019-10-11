package scw.mvc.servlet;

import javax.servlet.ServletConfig;

import scw.application.Application;
import scw.application.CommonApplication;
import scw.beans.BeanFactory;
import scw.core.PropertyFactory;

public class ServletApplication implements Application {
	private final CommonApplication commonApplication;

	public ServletApplication(ServletConfig servletConfig) throws Throwable {
		ServletConfigPropertyFactory propertyFactory = new ServletConfigPropertyFactory(servletConfig);
		this.commonApplication = new CommonApplication(propertyFactory.getConfigXml());
		this.commonApplication.addPropertyFactory(propertyFactory);
	}

	public BeanFactory getBeanFactory() {
		return commonApplication.getBeanFactory();
	}

	public PropertyFactory getPropertyFactory() {
		return commonApplication.getPropertyFactory();
	}

	public void init() {
		commonApplication.init();
	}

	public void destroy() {
		commonApplication.destroy();
	}

	public CommonApplication getCommonApplication() {
		return commonApplication;
	}

}
