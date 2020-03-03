package scw.servlet.mvc;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;
import scw.core.utils.StringUtils;
import scw.servlet.ServletUtils;

public final class ConfigurationServletService implements ServletService{
	private final ServletService service;
	
	public ConfigurationServletService(BeanFactory beanFactory, PropertyFactory propertyFactory){
		this.service = getServletService(beanFactory, propertyFactory, ServletUtils.isAsyncSupport() && StringUtils.parseBoolean(propertyFactory.getProperty("servlet.async")));
	}
	
	public void service(ServletRequest servletRequest,
			ServletResponse servletResponse) {
		service.service(servletRequest, servletResponse);
	}

	public static ServletService getServletService(BeanFactory beanFactory, PropertyFactory propertyFactory,
			boolean async) {
		if (async) {
			return beanFactory.getInstance("scw.servlet.mvc.AsyncServletService");
		} else {
			return beanFactory.getInstance("scw.servlet.mvc.DefaultServletService");
		}
	}
}
