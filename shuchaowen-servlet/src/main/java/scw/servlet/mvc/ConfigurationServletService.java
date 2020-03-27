package scw.servlet.mvc;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import scw.beans.BeanFactory;
import scw.servlet.ServletUtils;
import scw.util.value.property.PropertyFactory;

public final class ConfigurationServletService implements ServletService{
	private final ServletService service;
	
	public ConfigurationServletService(BeanFactory beanFactory, PropertyFactory propertyFactory){
		this.service = getServletService(beanFactory, propertyFactory, ServletUtils.isAsyncSupport() && propertyFactory.getBooleanValue("servlet.async"));
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
