package io.basc.framework.beans.factory.component;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.beans.factory.config.BeanDefinitionRegistry;
import io.basc.framework.beans.factory.config.BeanDefinitionRegistryPostProcessor;
import io.basc.framework.beans.factory.config.ConfigurableListableBeanFactory;
import io.basc.framework.core.env.Environment;
import io.basc.framework.core.env.EnvironmentCapable;
import io.basc.framework.core.env.config.DefaultEnvironment;
import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.util.Elements;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ComponentRegistryPostProcessor
		implements BeanDefinitionRegistryPostProcessor, EnvironmentCapable {
	private final ComponentResolvers resolvers = new DefaultComponentResolvers();
	private final Environment environment = new DefaultEnvironment();

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		ComponentResolvers resolvers = new ComponentResolvers();
		resolvers.setLast(getResolvers());
		resolvers.doConfigure(beanFactory);
		postProcessBeanDefinitionRegistry(resolvers, beanFactory);
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		postProcessBeanDefinitionRegistry(getResolvers(), registry);
	}

	public void postProcessBeanDefinitionRegistry(ComponentResolver componentResolver, BeanDefinitionRegistry registry)
			throws BeansException {
		EnvironmentCapable context = null;
		if (registry instanceof EnvironmentCapable) {
			context = ((EnvironmentCapable) registry);
		}

		if (context == null) {
			context = this;
		}

		postProcessBeanDefinitionRegistry(componentResolver, context, registry);
	}

	public abstract void postProcessBeanDefinitionRegistry(ComponentResolver componentResolver,
			EnvironmentCapable context, BeanDefinitionRegistry registry) throws BeansException;

	public BeanDefinition registerComponent(ComponentResolver componentResolver, EnvironmentCapable context,
			BeanDefinitionRegistry registry, AnnotationMetadata annotationMetadata, ClassLoader classLoader) {
		if (!componentResolver.isComponent(annotationMetadata)) {
			return null;
		}
		BeanDefinition beanDefinition = componentResolver.createComponent(annotationMetadata, classLoader);
		if (!registerComponent(componentResolver, context, registry, beanDefinition)) {
			return null;
		}
		return beanDefinition;
	}

	public boolean registerComponent(ComponentResolver componentResolver, EnvironmentCapable context,
			BeanDefinitionRegistry registry, BeanDefinition beanDefinition) {
		if (!componentResolver.matchs(context, registry, beanDefinition.getExecutionStrategy())) {
			return false;
		}

		registry.registerBeanDefinition(beanDefinition.getName(), beanDefinition);
		Elements<String> aliasNames = componentResolver.getAliasNames(beanDefinition);
		aliasNames.forEach((alias) -> registry.registerAlias(beanDefinition.getName(), alias));
		return true;
	}
}
