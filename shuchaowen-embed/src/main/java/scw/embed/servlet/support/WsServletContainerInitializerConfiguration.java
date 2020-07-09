package scw.embed.servlet.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;

import org.apache.tomcat.websocket.server.WsSci;

import scw.beans.BeanUtils;
import scw.core.Constants;
import scw.core.instance.annotation.Configuration;
import scw.embed.servlet.ServletContainerInitializerConfiguration;
import scw.util.ClassScanner;

@Configuration(order=Integer.MIN_VALUE)
public class WsServletContainerInitializerConfiguration implements ServletContainerInitializerConfiguration{
	private static final WsSci WS_SCI = new WsSci();
	
	public Collection<? extends ServletContainerInitializer> getServletContainerInitializers() {
		return Arrays.asList(WS_SCI);
	}

	public Set<Class<?>> getClassSet() {
		return ClassScanner.getInstance().getClasses(Arrays.asList(Constants.SYSTEM_PACKAGE_NAME, BeanUtils.getScanAnnotationPackageName()));
	}


}
