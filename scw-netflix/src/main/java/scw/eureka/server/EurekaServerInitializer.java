package scw.eureka.server;

import javax.servlet.ServletContext;

import com.netflix.eureka.EurekaServerConfig;

import scw.application.Application;
import scw.application.ApplicationEvent;
import scw.beans.Destroy;
import scw.beans.annotation.Autowired;
import scw.core.instance.annotation.Configuration;
import scw.eureka.server.event.EurekaRegistryAvailableEvent;
import scw.eureka.server.event.EurekaServerStartedEvent;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.servlet.ServletContextBootstrap;

/**
 * 自动启动eureka服务端
 * 
 * @author shuchaowen
 *
 */
@Configuration(order = Integer.MIN_VALUE)
public class EurekaServerInitializer implements ServletContextBootstrap, Destroy {
	private static Logger logger = LoggerFactory.getLogger(EurekaServerInitializer.class);

	@Autowired
	private Application application;
	@Autowired(required = false)
	private EurekaServerBootstrap eurekaServerBootstrap;
	@Autowired(required = false)
	private EurekaServerConfig eurekaServerConfig;
	private ServletContext servletContext;

	@Override
	public void init(ServletContext servletContext) {
		this.servletContext = servletContext;
		try {
			// TODO: is this class even needed now?
			eurekaServerBootstrap.contextInitialized(servletContext);
			logger.info("Started Eureka Server");

			publish(new EurekaRegistryAvailableEvent(eurekaServerConfig));
			publish(new EurekaServerStartedEvent(eurekaServerConfig));
		} catch (Exception ex) {
			// Help!
			logger.error(ex, "Could not initialize Eureka servlet context");
		}
	}

	private void publish(ApplicationEvent event) {
		if (application == null) {
			return;
		}
		application.publishEvent(event);
	}

	@Override
	public void destroy() throws Throwable {
		eurekaServerBootstrap.contextDestroyed(this.servletContext);
	}
}
