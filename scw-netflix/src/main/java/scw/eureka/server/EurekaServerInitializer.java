package scw.eureka.server;

import javax.servlet.ServletContext;

import com.netflix.eureka.EurekaServerConfig;

import scw.application.Application;
import scw.beans.Destroy;
import scw.beans.annotation.Autowired;
import scw.core.instance.annotation.Configuration;
import scw.eureka.server.event.EurekaRegistryAvailableEvent;
import scw.eureka.server.event.EurekaServerStartedEvent;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.servlet.ServletContextInitialization;

/**
 * 自动启动eureka服务端
 * 
 * @author shuchaowen
 *
 */
@Configuration(order = Integer.MIN_VALUE)
public class EurekaServerInitializer implements ServletContextInitialization, Destroy {
	private static Logger logger = LoggerFactory.getLogger(EurekaServerInitializer.class);

	@Autowired(required = false)
	private EurekaServerBootstrap eurekaServerBootstrap;
	@Autowired(required = false)
	private EurekaServerConfig eurekaServerConfig;
	private ServletContext servletContext;

	@Override
	public void init(Application application, ServletContext servletContext) {
		this.servletContext = servletContext;
		if (eurekaServerBootstrap == null) {
			return;
		}

		new Thread(() -> {
			try {
				// TODO: is this class even needed now?
				eurekaServerBootstrap.contextInitialized(servletContext);
				logger.info("Started Eureka Server");

				application.publishEvent(new EurekaRegistryAvailableEvent(eurekaServerConfig));
				application.publishEvent(new EurekaServerStartedEvent(eurekaServerConfig));
			} catch (Exception ex) {
				// Help!
				logger.error(ex, "Could not initialize Eureka servlet context");
			}
		}).start();
	}

	@Override
	public void destroy() throws Throwable {
		if (eurekaServerBootstrap == null) {
			return;
		}

		eurekaServerBootstrap.contextDestroyed(this.servletContext);
	}
}
