package run.soeasy.framework.beans.factory.component;

import lombok.Getter;
import lombok.Setter;
import run.soeasy.framework.beans.BeansException;
import run.soeasy.framework.beans.factory.config.BeanDefinition;
import run.soeasy.framework.beans.factory.config.BeanDefinitionRegistry;
import run.soeasy.framework.beans.factory.config.BeanDefinitionRegistryPostProcessor;
import run.soeasy.framework.beans.factory.config.ConfigurableListableBeanFactory;
import run.soeasy.framework.core.env.Environment;
import run.soeasy.framework.core.env.EnvironmentCapable;
import run.soeasy.framework.core.env.config.DefaultEnvironment;
import run.soeasy.framework.core.type.AnnotationMetadata;
import run.soeasy.framework.util.collections.Elements;

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
