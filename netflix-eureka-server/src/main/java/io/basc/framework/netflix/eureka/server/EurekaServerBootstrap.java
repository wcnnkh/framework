package io.basc.framework.netflix.eureka.server;

import javax.servlet.ServletContext;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.DataCenterInfo;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.discovery.converters.JsonXStream;
import com.netflix.discovery.converters.XmlXStream;
import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.EurekaServerContext;
import com.netflix.eureka.EurekaServerContextHolder;
import com.netflix.eureka.V1AwareInstanceInfoConverter;
import com.netflix.eureka.aws.AwsBinder;
import com.netflix.eureka.aws.AwsBinderDelegate;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.util.EurekaMonitors;
import com.thoughtworks.xstream.XStream;

import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;

public class EurekaServerBootstrap {
	private static Logger log = LogManager.getLogger(EurekaServerBootstrap.class);

	protected EurekaServerConfig eurekaServerConfig;

	protected ApplicationInfoManager applicationInfoManager;

	protected EurekaClientConfig eurekaClientConfig;

	protected PeerAwareInstanceRegistry registry;

	protected volatile EurekaServerContext serverContext;

	protected volatile AwsBinder awsBinder;
	
	public EurekaServerBootstrap(ApplicationInfoManager applicationInfoManager, EurekaClientConfig eurekaClientConfig,
			EurekaServerConfig eurekaServerConfig, PeerAwareInstanceRegistry registry,
			EurekaServerContext serverContext) {
		this.applicationInfoManager = applicationInfoManager;
		this.eurekaClientConfig = eurekaClientConfig;
		this.eurekaServerConfig = eurekaServerConfig;
		this.registry = registry;
		this.serverContext = serverContext;
	}

	public void contextInitialized(ServletContext context) throws Exception {
		initEurekaEnvironment();
		initEurekaServerContext();
		context.setAttribute(EurekaServerContext.class.getName(), this.serverContext);
	}

	public void contextDestroyed(ServletContext context) {
		try {
			log.info("Shutting down Eureka Server..");
			context.removeAttribute(EurekaServerContext.class.getName());

			destroyEurekaServerContext();
			destroyEurekaEnvironment();

		} catch (Throwable e) {
			log.error(e, "Error shutting down eureka");
		}
		log.info("Eureka Service is now shutdown...");
	}

	protected void initEurekaEnvironment() throws Exception {
		log.info("Setting the eureka configuration..");

	}

	protected void initEurekaServerContext() throws Exception {
		// For backward compatibility
		JsonXStream.getInstance().registerConverter(new V1AwareInstanceInfoConverter(), XStream.PRIORITY_VERY_HIGH);
		XmlXStream.getInstance().registerConverter(new V1AwareInstanceInfoConverter(), XStream.PRIORITY_VERY_HIGH);

		if (isAws(this.applicationInfoManager.getInfo())) {
			this.awsBinder = new AwsBinderDelegate(this.eurekaServerConfig, this.eurekaClientConfig, this.registry,
					this.applicationInfoManager);
			this.awsBinder.start();
		}

		EurekaServerContextHolder.initialize(this.serverContext);
		serverContext.initialize();
		log.info("Initialized server context");

		// Copy registry from neighboring eureka node
		int registryCount = this.registry.syncUp();
		this.registry.openForTraffic(this.applicationInfoManager, registryCount);
		
		// Register all monitoring statistics.
		EurekaMonitors.registerAllStats();
	}

	/**
	 * Server context shutdown hook. Override for custom logic
	 * 
	 * @throws Exception - calling {@link AwsBinder#shutdown()} or
	 *                   {@link EurekaServerContext#shutdown()} may result in an
	 *                   exception
	 */
	protected void destroyEurekaServerContext() throws Exception {
		EurekaMonitors.shutdown();
		if (this.awsBinder != null) {
			this.awsBinder.shutdown();
		}
		if (this.serverContext != null) {
			this.serverContext.shutdown();
		}
	}

	/**
	 * Users can override to clean up the environment themselves.
	 * 
	 * @throws Exception - shutting down Eureka servers may result in an exception
	 */
	protected void destroyEurekaEnvironment() throws Exception {
	}

	protected boolean isAws(InstanceInfo selfInstanceInfo) {
		boolean result = DataCenterInfo.Name.Amazon == selfInstanceInfo.getDataCenterInfo().getName();
		log.info("isAws returned " + result);
		return result;
	}

}
