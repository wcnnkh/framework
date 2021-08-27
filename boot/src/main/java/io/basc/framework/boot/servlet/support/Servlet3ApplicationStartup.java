package io.basc.framework.boot.servlet.support;

import io.basc.framework.boot.Application;
import io.basc.framework.boot.servlet.FilterRegistration;
import io.basc.framework.core.utils.CollectionUtils;
import io.basc.framework.util.XUtils;

import java.util.Collection;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.Servlet;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class Servlet3ApplicationStartup extends DefaultServletApplicationStartup{
	@Override
	protected void afterStarted(ServletContext servletContext, Application application)
			throws ServletException {
		int i = 0;
		for (FilterRegistration registration : application.getBeanFactory().getServiceLoader(FilterRegistration.class)) {
			Filter filter = registration.getFilter();
			if (filter == null) {
				continue;
			}
			
			String name = XUtils.getName(filter, FilterRegistration.class.getSimpleName() + (i++));
			Dynamic dynamic = servletContext.addFilter(name, filter);
			Collection<String> urlPatterns = registration.getUrlPatterns();
			dynamic.addMappingForUrlPatterns(null, true, CollectionUtils.isEmpty(urlPatterns)? new String[]{FilterRegistration.ALL}:urlPatterns.toArray(new String[0]));
			dynamic.setAsyncSupported(true);
		}
		
		for(io.basc.framework.boot.servlet.ServletRegistration registration : application.getBeanFactory().getServiceLoader(io.basc.framework.boot.servlet.ServletRegistration.class)){
			Servlet servlet = registration.getServlet();
			if(servlet == null){
				continue;
			}
			
			String name = XUtils.getName(servlet, ServletRegistration.class.getSimpleName() + (i++));
			ServletRegistration.Dynamic dynamic = servletContext.addServlet(name, servlet);
			Collection<String> urlPatterns = registration.getUrlPatterns();
			dynamic.addMapping(CollectionUtils.isEmpty(urlPatterns)? new String[]{io.basc.framework.boot.servlet.ServletRegistration.ALL}:urlPatterns.toArray(new String[0]));
			dynamic.setAsyncSupported(true);
			dynamic.setLoadOnStartup(1);
		}
		
		for(ServletContextListener listener : application.getBeanFactory().getServiceLoader(ServletContextListener.class)){
			servletContext.addListener(listener);
		}
		
		for (ServletContainerInitializer initializer : application.getBeanFactory().getServiceLoader(ServletContainerInitializer.class)) {
			initializer.onStartup(application.getContextClasses().toSet(), servletContext);
		}
		super.afterStarted(servletContext, application);
	}
}
