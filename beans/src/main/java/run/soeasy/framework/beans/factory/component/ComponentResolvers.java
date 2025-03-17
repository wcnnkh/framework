package run.soeasy.framework.beans.factory.component;

import java.util.NoSuchElementException;

import run.soeasy.framework.beans.factory.config.BeanDefinition;
import run.soeasy.framework.beans.factory.config.BeanDefinitionRegistry;
import run.soeasy.framework.core.env.EnvironmentCapable;
import run.soeasy.framework.core.type.AnnotatedTypeMetadata;
import run.soeasy.framework.core.type.AnnotationMetadata;
import run.soeasy.framework.core.type.MethodMetadata;
import run.soeasy.framework.util.collections.Elements;
import run.soeasy.framework.util.spi.ConfigurableServices;

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
