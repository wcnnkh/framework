package io.basc.framework.boot.servlet;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * servlet容器会使用spi机制初始化此类,对于嵌入式服务器应当手动初始化(如:tomcat embedded)
 * 
 * @author wcnnkh
 *
 */
public class ApplicationServletContainerInitializer extends ApplicationServletContextListener
		implements ServletContainerInitializer {
	public void onStartup(Set<Class<?>> classes, ServletContext servletContext) throws ServletException {
		ServletContextUtils.getServletApplicationStartup().load(servletContext);
		servletContext.addListener(this);
	}
}
