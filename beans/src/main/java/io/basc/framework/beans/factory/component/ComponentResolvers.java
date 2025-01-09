package io.basc.framework.beans.factory.component;

import java.util.NoSuchElementException;

import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.beans.factory.config.BeanDefinitionRegistry;
import io.basc.framework.core.env.EnvironmentCapable;
import io.basc.framework.core.type.AnnotatedTypeMetadata;
import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.core.type.MethodMetadata;
import io.basc.framework.util.Elements;
import io.basc.framework.util.spi.ConfigurableServices;

public class ComponentResolvers extends ConfigurableServices<ComponentResolver> implements ComponentResolver {
	public ComponentResolvers() {
		setServiceClass(ComponentResolver.class);
	}

	@Override
	public boolean isComponent(AnnotatedTypeMetadata annotatedTypeMetadata) {
		for (ComponentResolver resolver : this) {
			if (resolver.isComponent(annotatedTypeMetadata)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Elements<String> getAliasNames(BeanDefinition beanDefinition) {
		Elements<String> aliasNames = Elements.empty();
		for (ComponentResolver resolver : this) {
			aliasNames = aliasNames.concat(resolver.getAliasNames(beanDefinition));
		}
		return aliasNames;
	}

	@Override
	public BeanDefinition createComponent(AnnotationMetadata componentAnnotationMetadata, ClassLoader classLoader) {
		for (ComponentResolver resolver : this) {
			if (resolver.isComponent(componentAnnotationMetadata)) {
				return resolver.createComponent(componentAnnotationMetadata, classLoader);
			}
		}
		throw new NoSuchElementException("ComponentResolver");
	}

	@Override
	public boolean isConfiguration(BeanDefinition component) {
		for (ComponentResolver resolver : this) {
			if (resolver.isConfiguration(component)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public BeanDefinition createComponent(BeanDefinition component, MethodMetadata methodMetadata) {
		for (ComponentResolver resolver : this) {
			if (resolver.isComponent(methodMetadata)) {
				return resolver.createComponent(component, methodMetadata);
			}
		}
		throw new NoSuchElementException("ComponentResolver");
	}

	@Override
	public boolean matchs(EnvironmentCapable context, BeanDefinitionRegistry registry,
			AnnotatedTypeMetadata annotatedTypeMetadata) {
		for (ComponentResolver resolver : this) {
			if (!resolver.matchs(context, registry, annotatedTypeMetadata)) {
				return false;
			}
		}
		return true;
	}
}
