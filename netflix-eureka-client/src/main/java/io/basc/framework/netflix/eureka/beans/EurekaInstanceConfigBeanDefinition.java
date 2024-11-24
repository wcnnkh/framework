package io.basc.framework.netflix.eureka.beans;

import java.util.Arrays;
import java.util.Map;

import com.netflix.appinfo.EurekaInstanceConfig;

import io.basc.framework.beans.BeansException;
import io.basc.framework.boot.Application;
import io.basc.framework.cloud.commons.util.IdUtils;
import io.basc.framework.cloud.commons.util.InetUtils;
import io.basc.framework.core.env.Environment;
import io.basc.framework.core.env.EnvironmentBeanDefinition;
import io.basc.framework.netflix.eureka.CloudEurekaInstanceConfig;
import io.basc.framework.netflix.eureka.EurekaInstanceConfigBean;
import io.basc.framework.netflix.eureka.metadata.DefaultManagementMetadataProvider;
import io.basc.framework.netflix.eureka.metadata.ManagementMetadata;
import io.basc.framework.netflix.eureka.metadata.ManagementMetadataProvider;
import io.basc.framework.util.Elements;
import io.basc.framework.util.StringUtils;

public class EurekaInstanceConfigBeanDefinition extends EnvironmentBeanDefinition {

	public EurekaInstanceConfigBeanDefinition(Environment environment) {
		super(environment, EurekaInstanceConfigBean.class);
	}

	@Override
	public Elements<String> getNames() {
		return Arrays.asList(CloudEurekaInstanceConfig.class.getName(), EurekaInstanceConfig.class.getName());
	}

	@Override
	public boolean isInstance() {
		return getBeanFactory().isInstance(InetUtils.class) && getBeanFactory().isInstance(Application.class);
	}

	@Override
	public Object create() throws BeansException {
		InetUtils inetUtils = getBeanFactory().getInstance(InetUtils.class);
		ManagementMetadataProvider managementMetadataProvider = getBeanFactory().isInstance(
				ManagementMetadataProvider.class) ? getBeanFactory().getInstance(ManagementMetadataProvider.class)
						: new DefaultManagementMetadataProvider();
		String hostname = getEnvironment().getProperties().getAsString("eureka.instance.hostname");
		boolean preferIpAddress = getEnvironment().getProperties().getAsBoolean("eureka.instance.prefer-ip-address");
		String ipAddress = getEnvironment().getProperties().getAsString("eureka.instance.ip-address");
		boolean isSecurePortEnabled = getEnvironment().getProperties()
				.getAsBoolean("eureka.instance.secure-port-enabled");

		String serverContextPath = getEnvironment().getProperties().get("server.servlet.context-path").or("/")
				.getAsString();
		Application application = getBeanFactory().getInstance(Application.class);
		Integer managementPort = getEnvironment().getProperties().getAsObject("management.server.port", Integer.class);
		String managementContextPath = getEnvironment().getProperties()
				.getAsString("management.server.servlet.context-path");
		Integer jmxPort = getEnvironment().getProperties().getAsObject("com.sun.management.jmxremote.port",
				Integer.class);
		EurekaInstanceConfigBean instance = new EurekaInstanceConfigBean(inetUtils);

		if (application.getPort().isPresent()) {
			instance.setNonSecurePort(application.getPort().getAsInt());
		}

		instance.setInstanceId(IdUtils.getDefaultInstanceId(getEnvironment().getProperties()));
		instance.setPreferIpAddress(preferIpAddress);
		instance.setSecurePortEnabled(isSecurePortEnabled);
		if (StringUtils.hasText(ipAddress)) {
			instance.setIpAddress(ipAddress);
		}

		if (isSecurePortEnabled && application.getPort().isPresent()) {
			instance.setSecurePort(application.getPort().getAsInt());
		}

		if (StringUtils.hasText(hostname)) {
			instance.setHostname(hostname);
		}
		String statusPageUrlPath = getEnvironment().getProperties().getAsString("eureka.instance.status-page-url-path");
		String healthCheckUrlPath = getEnvironment().getProperties()
				.getAsString("eureka.instance.health-check-url-path");

		if (StringUtils.hasText(statusPageUrlPath)) {
			instance.setStatusPageUrlPath(statusPageUrlPath);
		}
		if (StringUtils.hasText(healthCheckUrlPath)) {
			instance.setHealthCheckUrlPath(healthCheckUrlPath);
		}

		ManagementMetadata metadata = managementMetadataProvider.get(instance, instance.getNonSecurePort(),
				serverContextPath, managementContextPath, managementPort);

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
