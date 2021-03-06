package scw.netflix.eureka;

import java.util.Map;

import scw.beans.BeanDefinition;
import scw.beans.BeanDefinitionLoader;
import scw.beans.BeanDefinitionLoaderChain;
import scw.beans.BeanFactory;
import scw.beans.BeansException;
import scw.beans.support.DefaultBeanDefinition;
import scw.boot.Application;
import scw.boot.support.ApplicationUtils;
import scw.commons.util.IdUtils;
import scw.commons.util.InetUtils;
import scw.commons.util.InetUtilsProperties;
import scw.context.annotation.Provider;
import scw.core.utils.StringUtils;
import scw.netflix.eureka.metadata.DefaultManagementMetadataProvider;
import scw.netflix.eureka.metadata.ManagementMetadata;
import scw.netflix.eureka.metadata.ManagementMetadataProvider;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;

@Provider
public class EurekaClientBeanDefinitionLoader implements BeanDefinitionLoader {

	@Override
	public BeanDefinition load(BeanFactory beanFactory, Class<?> sourceClass, BeanDefinitionLoaderChain loaderChain) {
		if (sourceClass == EurekaClientConfig.class
				|| sourceClass == CloudEurekaInstanceConfig.class) {
			return new EurekaClientConfigBuilder(beanFactory, sourceClass);
		}

		if (sourceClass == EurekaInstanceConfig.class
				|| sourceClass == CloudEurekaInstanceConfig.class) {
			return new EurekaInstanceConfigBuilder(beanFactory, sourceClass);
		}

		if (sourceClass == ApplicationInfoManager.class) {
			return new ApplicationInfoManagerBuilder(beanFactory, sourceClass);
		}

		if (sourceClass == InetUtils.class) {
			return new InetUtilsBuilder(beanFactory, sourceClass);
		}
		
		if (sourceClass == EurekaClient.class) {
			return new EurekaClientBuilder(beanFactory, sourceClass);
		}

		return loaderChain.load(beanFactory, sourceClass);
	}

	private static class ApplicationInfoManagerBuilder extends DefaultBeanDefinition {

		public ApplicationInfoManagerBuilder(BeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		@Override
		public boolean isInstance() {
			return beanFactory.isInstance(EurekaInstanceConfig.class);
		}

		@Override
		public Object create() throws BeansException {
			EurekaInstanceConfig eurekaInstanceConfig = beanFactory.getInstance(EurekaInstanceConfig.class);
			InstanceInfo instanceInfo = beanFactory.getInstance(InstanceInfoFactory.class).create(eurekaInstanceConfig);
			return new ApplicationInfoManager(eurekaInstanceConfig, instanceInfo);
		}
	}

	private static class InetUtilsBuilder extends DefaultBeanDefinition {
		public InetUtilsBuilder(BeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		@Override
		public boolean isInstance() {
			return true;
		}

		@Override
		public Object create() throws BeansException {
			InetUtilsProperties inetUtilsProperties = beanFactory.getInstance(InetUtilsProperties.class);
			return new InetUtils(inetUtilsProperties);
		}
	}

	private static class EurekaInstanceConfigBuilder extends DefaultBeanDefinition {

		public EurekaInstanceConfigBuilder(BeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		@Override
		public boolean isInstance() {
			return beanFactory.isInstance(InetUtils.class) && beanFactory.isInstance(Application.class);
		}

		@Override	
		public Object create() throws BeansException {
			InetUtils inetUtils = beanFactory.getInstance(InetUtils.class);
			ManagementMetadataProvider managementMetadataProvider = beanFactory.isInstance(
					ManagementMetadataProvider.class) ? beanFactory.getInstance(ManagementMetadataProvider.class)
							: new DefaultManagementMetadataProvider();
			String hostname = beanFactory.getEnvironment().getString("eureka.instance.hostname");
			boolean preferIpAddress = beanFactory.getEnvironment().getBooleanValue("eureka.instance.prefer-ip-address");
			String ipAddress = beanFactory.getEnvironment().getString("eureka.instance.ip-address");
			boolean isSecurePortEnabled = beanFactory.getEnvironment().getBooleanValue("eureka.instance.secure-port-enabled");

			String serverContextPath = beanFactory.getEnvironment().getValue("server.servlet.context-path", String.class, "/");
			Application application = beanFactory.getInstance(Application.class);
			int serverPort = ApplicationUtils.getApplicationPort(application);

			Integer managementPort = beanFactory.getEnvironment().getValue("management.server.port", Integer.class, null);
			String managementContextPath = beanFactory.getEnvironment().getString("management.server.servlet.context-path");
			Integer jmxPort = beanFactory.getEnvironment().getValue("com.sun.management.jmxremote.port", Integer.class, null);
			EurekaInstanceConfigBean instance = new EurekaInstanceConfigBean(inetUtils);

			instance.setNonSecurePort(serverPort);
			instance.setInstanceId(IdUtils.getDefaultInstanceId(beanFactory.getEnvironment()));
			instance.setPreferIpAddress(preferIpAddress);
			instance.setSecurePortEnabled(isSecurePortEnabled);
			if (StringUtils.hasText(ipAddress)) {
				instance.setIpAddress(ipAddress);
			}

			if (isSecurePortEnabled) {
				instance.setSecurePort(serverPort);
			}

			if (StringUtils.hasText(hostname)) {
				instance.setHostname(hostname);
			}
			String statusPageUrlPath = beanFactory.getEnvironment().getString("eureka.instance.status-page-url-path");
			String healthCheckUrlPath = beanFactory.getEnvironment().getString("eureka.instance.health-check-url-path");

			if (StringUtils.hasText(statusPageUrlPath)) {
				instance.setStatusPageUrlPath(statusPageUrlPath);
			}
			if (StringUtils.hasText(healthCheckUrlPath)) {
				instance.setHealthCheckUrlPath(healthCheckUrlPath);
			}

			ManagementMetadata metadata = managementMetadataProvider.get(instance, serverPort, serverContextPath,
					managementContextPath, managementPort);

			if (metadata != null) {
				instance.setStatusPageUrl(metadata.getStatusPageUrl());
				instance.setHealthCheckUrl(metadata.getHealthCheckUrl());
				if (instance.isSecurePortEnabled()) {
					instance.setSecureHealthCheckUrl(metadata.getSecureHealthCheckUrl());
				}
				Map<String, String> metadataMap = instance.getMetadataMap();
				metadataMap.computeIfAbsent("management.port", k -> String.valueOf(metadata.getManagementPort()));
			} else {
				// without the metadata the status and health check URLs will
				// not be set
				// and the status page and health check url paths will not
				// include the
				// context path so set them here
				if (StringUtils.hasText(managementContextPath)) {
					instance.setHealthCheckUrlPath(managementContextPath + instance.getHealthCheckUrlPath());
					instance.setStatusPageUrlPath(managementContextPath + instance.getStatusPageUrlPath());
				}
			}

			setupJmxPort(instance, jmxPort);
			return instance;
		}
	}
	
	private static void setupJmxPort(EurekaInstanceConfigBean instance, Integer jmxPort) {
		Map<String, String> metadataMap = instance.getMetadataMap();
		if (metadataMap.get("jmx.port") == null && jmxPort != null) {
			metadataMap.put("jmx.port", String.valueOf(jmxPort));
		}
	}
	
	private static class EurekaClientBuilder extends DefaultBeanDefinition {

		public EurekaClientBuilder(BeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		@Override
		public boolean isInstance() {
			return beanFactory.isInstance(ApplicationInfoManager.class)
					&& beanFactory.isInstance(EurekaClientConfig.class) && beanFactory.isInstance(Application.class);
		}

		@Override
		public Object create() throws BeansException {
			ApplicationInfoManager applicationInfoManager = beanFactory.getInstance(ApplicationInfoManager.class);
			EurekaClientConfig eurekaClientConfig = beanFactory.getInstance(EurekaClientConfig.class);
			Application application = beanFactory.getInstance(Application.class);
			return new CloudEurekaClient(applicationInfoManager, eurekaClientConfig, application);
		}
	}

	private static class EurekaClientConfigBuilder extends DefaultBeanDefinition {

		public EurekaClientConfigBuilder(BeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		@Override
		public boolean isInstance() {
			return true;
		}

		@Override
		public Object create() throws BeansException {
			return new EurekaClientConfigBean();
		}
	}
}
