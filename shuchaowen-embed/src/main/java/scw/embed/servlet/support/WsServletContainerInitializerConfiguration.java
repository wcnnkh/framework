package scw.embed.servlet.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;

import org.apache.tomcat.websocket.server.WsSci;

import scw.application.ApplicationConfigUtils;
import scw.beans.annotation.Configuration;
import scw.core.utils.ClassUtils;
import scw.embed.servlet.ServletContainerInitializerConfiguration;
import scw.util.value.property.PropertyFactory;

@Configuration
public class WsServletContainerInitializerConfiguration implements ServletContainerInitializerConfiguration{
	private static final WsSci WS_SCI = new WsSci();
	private PropertyFactory propertyFactory;
	
	public WsServletContainerInitializerConfiguration(PropertyFactory propertyFactory){
		this.propertyFactory = propertyFactory;
	}
	
	public Collection<? extends ServletContainerInitializer> getServletContainerInitializers() {
		return Arrays.asList(WS_SCI);
	}

	public Set<Class<?>> getClassSet() {
		return ClassUtils.getClassSet("scw", ApplicationConfigUtils.getAnnotationPackage(propertyFactory));
	}


}
