package io.basc.framework.netflix.eureka.server;

import io.basc.framework.beans.annotation.Autowired;
import io.basc.framework.boot.Application;
import io.basc.framework.boot.servlet.ServletContextInitialization;
import io.basc.framework.context.Destroy;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

import javax.servlet.ServletContext;

/**
 * 自动启动eureka服务端
 * 
 * @author shuchaowen
 *
 */
@Provider
public class EurekaServerInitializer implements ServletContextInitialization, Destroy {
	private static Logger logger = LoggerFactory.getLogger(EurekaServerInitializer.class);
	
	@Autowired
	private EurekaServerBootstrap eurekaServerBootstrap;
	private ServletContext servletContext;

	@Override
	public void init(Application application, ServletContext servletContext) {
		this.servletContext = servletContext;
		Thread thread = new Thread(() -> {
			try {
				eurekaServerBootstrap.contextInitialized(servletContext);
			} catch (Throwable e) {
				logger.error(e, "Cannot bootstrap eureka server :");
			}
		});
		thread.setName(EurekaServerInitializer.class.getSimpleName());
		thread.start();
	}

	@Override
	public void destroy() throws Throwable {
		if (servletContext == null) {
			return;
		}
		eurekaServerBootstrap.contextDestroyed(servletContext);
	}
}
