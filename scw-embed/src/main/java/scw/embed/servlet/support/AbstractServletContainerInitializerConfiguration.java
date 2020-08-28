package scw.embed.servlet.support;

import java.util.Set;

import scw.beans.BeanUtils;
import scw.embed.servlet.ServletContainerInitializerConfiguration;
import scw.util.ClassScanner;
import scw.value.ValueFactory;

public abstract class AbstractServletContainerInitializerConfiguration implements ServletContainerInitializerConfiguration {
	private ValueFactory<String> propertyFactory;
	
	public AbstractServletContainerInitializerConfiguration(ValueFactory<String> propertyFactory){
		this.propertyFactory = propertyFactory;
	}
	
	public ValueFactory<String> getPropertyFactory() {
		return propertyFactory;
	}

	public Set<Class<?>> getClassSet() {
		return ClassScanner.getInstance()
				.getClasses(BeanUtils.getScanAnnotationPackageName(propertyFactory));
	}
}
