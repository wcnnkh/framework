package scw.netflix.eureka.server;

import javax.servlet.ServletContext;

import scw.beans.annotation.Autowired;
import scw.boot.Application;
import scw.boot.servlet.ServletContextInitialization;
import scw.context.Destroy;
import scw.context.annotation.Provider;

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
