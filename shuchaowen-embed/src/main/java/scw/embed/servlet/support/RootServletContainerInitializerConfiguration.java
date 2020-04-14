package scw.embed.servlet.support;

import java.util.Collection;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;

import scw.beans.BeanFactory;
import scw.core.instance.InstanceUtils;
import scw.embed.servlet.ServletContainerInitializerConfiguration;
import scw.util.value.property.PropertyFactory;

public class RootServletContainerInitializerConfiguration implements
		ServletContainerInitializerConfiguration {
	private Collection<ServletContainerInitializer> initializers;

	public RootServletContainerInitializerConfiguration(
			BeanFactory beanFactory, PropertyFactory propertyFactory) {
		this.initializers = InstanceUtils.getConfigurationList(
				ServletContainerInitializer.class, beanFactory, propertyFactory);
	}

	public Collection<? extends ServletContainerInitializer> getServletContainerInitializers() {
		return initializers;
	}

	public Set<Class<?>> getClassSet() {
		return null;
	}

}
