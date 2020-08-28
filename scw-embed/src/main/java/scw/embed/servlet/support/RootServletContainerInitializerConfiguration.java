package scw.embed.servlet.support;

import java.util.Collection;

import javax.servlet.ServletContainerInitializer;

import scw.beans.BeanFactory;
import scw.core.instance.InstanceUtils;
import scw.value.property.PropertyFactory;

public class RootServletContainerInitializerConfiguration extends AbstractServletContainerInitializerConfiguration {
	private Collection<ServletContainerInitializer> initializers;

	public RootServletContainerInitializerConfiguration(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		super(propertyFactory);
		this.initializers = InstanceUtils.getConfigurationList(ServletContainerInitializer.class, beanFactory, propertyFactory);
	}

	public Collection<? extends ServletContainerInitializer> getServletContainerInitializers() {
		return initializers;
	}
}
