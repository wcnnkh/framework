package io.basc.framework.netflix.eureka.server;

import javax.servlet.ServletContext;

import io.basc.framework.beans.factory.Destroy;
import io.basc.framework.beans.factory.annotation.Autowired;
import io.basc.framework.boot.Application;
import io.basc.framework.boot.servlet.ServletContextInitialization;
import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

/**
 * 自动启动eureka服务端
 * 
 * @author wcnnkh
 *
 */
@ConditionalOnParameters
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
	public void destroy() {
		if (servletContext == null) {
			return;
		}
		eurekaServerBootstrap.contextDestroyed(servletContext);
	}
}
