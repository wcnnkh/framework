package io.basc.framework.netflix.eureka;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.Registration;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.HealthCheckHandler;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;

import io.basc.framework.aop.ProxyInstanceTarget;
import io.basc.framework.boot.Application;
import io.basc.framework.cloud.DefaultServiceInstance;
import io.basc.framework.cloud.ServiceInstance;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;

/**
 * Eureka-specific implementation of service instance {@link Registration}.
 *
 */
public class EurekaRegistration implements ServiceInstance {
	private static final Logger log = LoggerFactory.getLogger(EurekaRegistration.class);

	private final EurekaClient eurekaClient;

	private final AtomicReference<CloudEurekaClient> cloudEurekaClient = new AtomicReference<>();

	private final CloudEurekaInstanceConfig instanceConfig;

	private final ApplicationInfoManager applicationInfoManager;

	private HealthCheckHandler healthCheckHandler;

	private EurekaRegistration(CloudEurekaInstanceConfig instanceConfig, EurekaClient eurekaClient,
			ApplicationInfoManager applicationInfoManager, @Nullable HealthCheckHandler healthCheckHandler) {
		this.eurekaClient = eurekaClient;
		this.instanceConfig = instanceConfig;
		this.applicationInfoManager = applicationInfoManager;
		this.healthCheckHandler = healthCheckHandler;
	}

	public static Builder builder(CloudEurekaInstanceConfig instanceConfig) {
		return new Builder(instanceConfig);
	}

	@Override
	public String getId() {
		return this.instanceConfig.getInstanceId();
	}

	@Override
	public String getName() {
		return this.instanceConfig.getAppname();
	}

	@Override
	public String getHost() {
		return this.instanceConfig.getHostName(false);
	}

	@Override
	public int getPort() {
		if (this.instanceConfig.getSecurePortEnabled()) {
			return this.instanceConfig.getSecurePort();
		}
		return this.instanceConfig.getNonSecurePort();
	}

	@Override
	public boolean isSecure() {
		return this.instanceConfig.getSecurePortEnabled();
	}

	@Override
	public URI getUri() {
		return DefaultServiceInstance.getUri(this);
	}

	@Override
	public Map<String, String> getMetadata() {
		return this.instanceConfig.getMetadataMap();
	}

	public CloudEurekaClient getEurekaClient() {
		if (this.cloudEurekaClient.get() == null) {
			try {
				this.cloudEurekaClient.compareAndSet(null, getTargetObject(eurekaClient, CloudEurekaClient.class));
			}
			catch (Exception e) {
				log.error("error getting CloudEurekaClient", e);
			}
		}
		return this.cloudEurekaClient.get();
	}
	
	@SuppressWarnings({ "unchecked" })
	protected <T> T getTargetObject(Object proxy, Class<T> targetClass) throws Exception {
		if(proxy instanceof ProxyInstanceTarget){
			return (T) ((ProxyInstanceTarget) proxy).getTargetProxyInstance();
		} else {
			return (T) proxy; // expected to be cglib proxy then, which is simply a
			// specialized class
		}
	}

	public CloudEurekaInstanceConfig getInstanceConfig() {
		return instanceConfig;
	}

	public ApplicationInfoManager getApplicationInfoManager() {
		return applicationInfoManager;
	}

	public HealthCheckHandler getHealthCheckHandler() {
		return healthCheckHandler;
	}

	public void setHealthCheckHandler(HealthCheckHandler healthCheckHandler) {
		this.healthCheckHandler = healthCheckHandler;
	}

	public void setNonSecurePort(int port) {
		this.instanceConfig.setNonSecurePort(port);
	}

	public int getNonSecurePort() {
		return this.instanceConfig.getNonSecurePort();
	}

	public void setSecurePort(int port) {
		this.instanceConfig.setSecurePort(port);
	}

	public int getSecurePort() {
		return this.instanceConfig.getSecurePort();
	}

	/**
	 * A builder for {@link EurekaRegistration} objects.
	 */
	public static class Builder {

		private final CloudEurekaInstanceConfig instanceConfig;

		private ApplicationInfoManager applicationInfoManager;

		private EurekaClient eurekaClient;

		private HealthCheckHandler healthCheckHandler;

		private EurekaClientConfig clientConfig;

		private Application application;

		Builder(CloudEurekaInstanceConfig instanceConfig) {
			this.instanceConfig = instanceConfig;
		}

		public Builder with(ApplicationInfoManager applicationInfoManager) {
			this.applicationInfoManager = applicationInfoManager;
			return this;
		}

		public Builder with(EurekaClient eurekaClient) {
			this.eurekaClient = eurekaClient;
			return this;
		}

		public Builder with(HealthCheckHandler healthCheckHandler) {
			this.healthCheckHandler = healthCheckHandler;
			return this;
		}

		public Builder with(EurekaClientConfig clientConfig, Application application) {
			this.clientConfig = clientConfig;
			this.application = application;
			return this;
		}

		public EurekaRegistration build() {
			Assert.notNull(instanceConfig, "instanceConfig may not be null");

			if (this.applicationInfoManager == null) {
				InstanceInfo instanceInfo = new InstanceInfoFactory().create(this.instanceConfig);
				this.applicationInfoManager = new ApplicationInfoManager(this.instanceConfig, instanceInfo);
			}
			if (this.eurekaClient == null) {
				Assert.notNull(this.clientConfig, "if eurekaClient is null, EurekaClientConfig may not be null");
				Assert.notNull(this.application, "if eurekaClient is null, ApplicationEventPublisher may not be null");

				this.eurekaClient = new CloudEurekaClient(this.applicationInfoManager, this.clientConfig,
						this.application);
			}
			return new EurekaRegistration(instanceConfig, eurekaClient, applicationInfoManager, healthCheckHandler);
		}

	}

}
