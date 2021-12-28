package io.basc.framework.netflix.eureka.beans;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.netflix.appinfo.EurekaInstanceConfig;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.boot.Application;
import io.basc.framework.boot.support.ApplicationUtils;
import io.basc.framework.cloud.commons.util.IdUtils;
import io.basc.framework.cloud.commons.util.InetUtils;
import io.basc.framework.netflix.eureka.CloudEurekaInstanceConfig;
import io.basc.framework.netflix.eureka.EurekaInstanceConfigBean;
import io.basc.framework.netflix.eureka.metadata.DefaultManagementMetadataProvider;
import io.basc.framework.netflix.eureka.metadata.ManagementMetadata;
import io.basc.framework.netflix.eureka.metadata.ManagementMetadataProvider;
import io.basc.framework.util.StringUtils;

public class EurekaInstanceConfigBeanDefinition extends DefaultBeanDefinition {

	public EurekaInstanceConfigBeanDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, EurekaInstanceConfigBean.class);
	}
	
	@Override
	public Collection<String> getNames() {
		return Arrays.asList(CloudEurekaInstanceConfig.class.getName(), EurekaInstanceConfig.class.getName());
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
		int serverPort = ApplicationUtils.getServerPort(application.getEnvironment());

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
	
	private static void setupJmxPort(EurekaInstanceConfigBean instance, Integer jmxPort) {
		Map<String, String> metadataMap = instance.getMetadataMap();
		if (metadataMap.get("jmx.port") == null && jmxPort != null) {
			metadataMap.put("jmx.port", String.valueOf(jmxPort));
		}
	}
}
