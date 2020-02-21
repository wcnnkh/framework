package scw.embed.servlet.support;

import java.util.Collection;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.PropertyFactory;
import scw.embed.servlet.ServletContainerInitializerConfiguration;

public class RootServletContainerInitializerConfiguration implements
		ServletContainerInitializerConfiguration {
	private Collection<ServletContainerInitializer> initializers;

	public RootServletContainerInitializerConfiguration(
			BeanFactory beanFactory, PropertyFactory propertyFactory) {
		this.initializers = BeanUtils.getConfigurationList(
				ServletContainerInitializer.class, null, beanFactory,
				propertyFactory);
	}

	public Collection<? extends ServletContainerInitializer> getServletContainerInitializers() {
		return initializers;
	}

	public Set<Class<?>> getClassSet() {
		return null;
	}

}
