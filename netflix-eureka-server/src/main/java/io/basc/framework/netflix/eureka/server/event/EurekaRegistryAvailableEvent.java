package io.basc.framework.netflix.eureka.server.event;

import io.basc.framework.boot.ApplicationEvent;

import com.netflix.eureka.EurekaServerConfig;

public class EurekaRegistryAvailableEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	/**
	 * @param eurekaServerConfig
	 *            {@link EurekaServerConfig} event source
	 */
	public EurekaRegistryAvailableEvent(EurekaServerConfig eurekaServerConfig) {
		super(eurekaServerConfig);
	}
}
