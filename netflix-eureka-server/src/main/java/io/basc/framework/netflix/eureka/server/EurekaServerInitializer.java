package io.basc.framework.netflix.eureka.server;

import io.basc.framework.beans.annotation.Autowired;
import io.basc.framework.boot.Application;
import io.basc.framework.boot.servlet.ServletContextInitialization;
import io.basc.framework.context.Destroy;
import io.basc.framework.context.annotation.Provider;

import javax.servlet.ServletContext;

/**
 * 自动启动eureka服务端
 * 
 * @author shuchaowen
 *
 */
@Provider
public class EurekaServerInitializer implements ServletContextInitialization, Destroy {
	@Autowired
	private EurekaServerBootstrap eurekaServerBootstrap;
	private ServletContext servletContext;

	@Override
	public void init(Application application, ServletContext servletContext) {
		eurekaServerBootstrap.contextInitialized(servletContext);
		this.servletContext = servletContext;
	}

	@Override
	public void destroy() throws Throwable {
		if (servletContext == null) {
			return;
		}

		eurekaServerBootstrap.contextDestroyed(servletContext);
	}
}
