package scw.netflix.eureka.server.event;

import scw.boot.ApplicationEvent;

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
