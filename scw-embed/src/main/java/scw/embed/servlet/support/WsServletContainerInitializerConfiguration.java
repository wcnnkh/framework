package scw.embed.servlet.support;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.ServletContainerInitializer;

import org.apache.tomcat.websocket.server.WsSci;

import scw.core.instance.annotation.Configuration;
import scw.value.property.PropertyFactory;

@Configuration(order = Integer.MIN_VALUE)
public class WsServletContainerInitializerConfiguration extends AbstractServletContainerInitializerConfiguration {
	private static final WsSci WS_SCI = new WsSci();
	
	public WsServletContainerInitializerConfiguration(PropertyFactory propertyFactory) {
		super(propertyFactory);
	}

	public Collection<? extends ServletContainerInitializer> getServletContainerInitializers() {
		return Arrays.asList(WS_SCI);
	}
}
