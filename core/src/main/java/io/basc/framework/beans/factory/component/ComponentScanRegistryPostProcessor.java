package io.basc.framework.beans.factory.component;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.config.BeanDefinitionRegistry;
import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.env.EnvironmentCapable;
import io.basc.framework.io.scan.ClassScanner;
import io.basc.framework.io.scan.MetadataReaderScanner;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.element.Elements;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ComponentScanRegistryPostProcessor extends ComponentRegistryPostProcessor {
	private ClassScanner classScanner;
	private ClassLoader classLoader;

	@Override
	public void postProcessBeanDefinitionRegistry(ComponentResolver componentResolver, EnvironmentCapable context,
			BeanDefinitionRegistry registry) throws BeansException {
		ClassLoader classLoader = null;
		if (registry instanceof ClassLoaderProvider) {
			classLoader = ((ClassLoaderProvider) registry).getClassLoader();
		}

		if (context instanceof ClassLoaderProvider) {
			classLoader = ((ClassLoaderProvider) context).getClassLoader();
		}
		postProcessBeanDefinitionRegistry(componentResolver, context, registry, classLoader);
	}

	public final void postProcessBeanDefinitionRegistry(ComponentResolver componentResolver, EnvironmentCapable context,
			BeanDefinitionRegistry registry, ClassLoader classLoader) throws BeansException {
		ClassScanner classScanner = null;
		if (context instanceof ClassScanner) {
			classScanner = (ClassScanner) context;
		}

		if (classScanner == null) {
			classScanner = getClassScanner();
		}

		if (classScanner == null) {
			classScanner = new MetadataReaderScanner();
		}

		postProcessBeanDefinitionRegistry(componentResolver, context, registry, classScanner, classLoader);
	}

	public void postProcessBeanDefinitionRegistry(ComponentResolver componentResolver, EnvironmentCapable context,
			BeanDefinitionRegistry registry, ClassScanner classScanner, ClassLoader classLoader) throws BeansException {
		for (AnnotationMetadata annotationMetadata : scan(context, classScanner)) {
			registerComponent(componentResolver, context, registry, annotationMetadata, classLoader);
		}
	}

	protected abstract Elements<AnnotationMetadata> scan(EnvironmentCapable context, ClassScanner classScanner);
}
