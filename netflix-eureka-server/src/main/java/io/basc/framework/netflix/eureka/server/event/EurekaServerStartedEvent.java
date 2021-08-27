package io.basc.framework.netflix.eureka.server.event;

import io.basc.framework.boot.ApplicationEvent;

import com.netflix.eureka.EurekaServerConfig;

public class EurekaServerStartedEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	/**
	 * @param eurekaServerConfig
	 *            {@link EurekaServerConfig} event source
	 */
	public EurekaServerStartedEvent(EurekaServerConfig eurekaServerConfig) {
		super(eurekaServerConfig);
	}

}
