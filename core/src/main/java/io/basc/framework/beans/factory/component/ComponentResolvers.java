package io.basc.framework.beans.factory.component;

import java.util.NoSuchElementException;

import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.beans.factory.config.BeanDefinitionRegistry;
import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.core.type.AnnotatedTypeMetadata;
import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.core.type.MethodMetadata;
import io.basc.framework.env.EnvironmentCapable;
import io.basc.framework.util.element.Elements;

public class ComponentResolvers extends ConfigurableServices<ComponentResolver> implements ComponentResolver {
	public ComponentResolvers() {
		setServiceClass(ComponentResolver.class);
	}

	@Override
	public boolean isComponent(AnnotatedTypeMetadata annotatedTypeMetadata) {
		for (ComponentResolver resolver : getServices()) {
			if (resolver.isComponent(annotatedTypeMetadata)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Elements<String> getAliasNames(BeanDefinition beanDefinition) {
		Elements<String> aliasNames = Elements.empty();
		for (ComponentResolver resolver : getServices()) {
			aliasNames = aliasNames.concat(resolver.getAliasNames(beanDefinition));
		}
		return aliasNames;
	}

	@Override
	public BeanDefinition createComponent(AnnotationMetadata componentAnnotationMetadata, ClassLoader classLoader) {
		for (ComponentResolver resolver : getServices()) {
			if (resolver.isComponent(componentAnnotationMetadata)) {
				return resolver.createComponent(componentAnnotationMetadata, classLoader);
			}
		}
		throw new NoSuchElementException("ComponentResolver");
	}

	@Override
	public boolean isConfiguration(BeanDefinition component) {
		for (ComponentResolver resolver : getServices()) {
			if (resolver.isConfiguration(component)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public BeanDefinition createComponent(BeanDefinition component, MethodMetadata methodMetadata) {
		for (ComponentResolver resolver : getServices()) {
			if (resolver.isComponent(methodMetadata)) {
				return resolver.createComponent(component, methodMetadata);
			}
		}
		throw new NoSuchElementException("ComponentResolver");
	}

	@Override
	public boolean matchs(EnvironmentCapable context, BeanDefinitionRegistry registry,
			AnnotatedTypeMetadata annotatedTypeMetadata) {
		for (ComponentResolver resolver : getServices()) {
			if (!resolver.matchs(context, registry, annotatedTypeMetadata)) {
				return false;
			}
		}
		return true;
	}
}
