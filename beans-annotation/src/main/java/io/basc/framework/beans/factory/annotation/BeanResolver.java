package io.basc.framework.beans.factory.annotation;

import java.lang.reflect.Method;

import io.basc.framework.beans.factory.component.ComponentResolver;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.beans.factory.config.BeanDefinitionRegistry;
import io.basc.framework.beans.factory.ioc.ConfigurationPropertiesMappingStrategy;
import io.basc.framework.beans.factory.ioc.DefaultConfigurationPropertiesMappingStrategy;
import io.basc.framework.beans.factory.ioc.IocResolver;
import io.basc.framework.core.type.AnnotatedTypeMetadata;
import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.core.type.MethodMetadata;
import io.basc.framework.env.EnvironmentCapable;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.element.Elements;

public class BeanResolver implements IocResolver, ComponentResolver {

	@Override
	public boolean isInitMethod(Method method) {
		return method.isAnnotationPresent(InitMethod.class);
	}

	@Override
	public boolean isDestroyMethod(Method method) {
		return method.isAnnotationPresent(Destroy.class);
	}

	@Override
	public ConfigurationPropertiesMappingStrategy getConfigurationPropertiesMappingStrategy(Class<?> clazz) {
		ConfigurationProperties configurationProperties = clazz.getAnnotation(ConfigurationProperties.class);
		if (configurationProperties == null) {
			return null;
		}

		String prefix = configurationProperties.prefix();
		if (StringUtils.isEmpty(prefix)) {
			prefix = configurationProperties.value();
		}

		DefaultConfigurationPropertiesMappingStrategy mappingStrategy = new DefaultConfigurationPropertiesMappingStrategy();
		mappingStrategy.setKeyPatterns(Elements.singleton(prefix));
		return mappingStrategy;
	}

	@Override
	public boolean matchs(EnvironmentCapable context, BeanDefinitionRegistry registry,
			AnnotatedTypeMetadata annotatedTypeMetadata) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isComponent(AnnotatedTypeMetadata annotatedTypeMetadata) {
		return annotatedTypeMetadata.getAnnotations().isPresent(Component.class);
	}

	@Override
	public Elements<String> getAliasNames(BeanDefinition beanDefinition) {
		Bean bean = beanDefinition.getAnnotations().get(anno)
	}

	@Override
	public BeanDefinition createComponent(AnnotationMetadata componentAnnotationMetadata, ClassLoader classLoader) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfiguration(BeanDefinition component) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BeanDefinition createComponent(BeanDefinition component, MethodMetadata methodMetadata) {
		// TODO Auto-generated method stub
		return null;
	}

}
