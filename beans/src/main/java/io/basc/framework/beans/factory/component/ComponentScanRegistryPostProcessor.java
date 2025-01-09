package io.basc.framework.beans.factory.component;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.config.BeanDefinitionRegistry;
import io.basc.framework.core.env.EnvironmentCapable;
import io.basc.framework.core.scan.PackagePatternMetadataReaderScanner;
import io.basc.framework.core.scan.TypeScanner;
import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.collection.Elements;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ComponentScanRegistryPostProcessor extends ComponentRegistryPostProcessor {
	private TypeScanner typeScanner;
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
		TypeScanner typeScanner = null;
		if (context instanceof TypeScanner) {
			typeScanner = (TypeScanner) context;
		}

		if (typeScanner == null) {
			typeScanner = getTypeScanner();
		}

		if (typeScanner == null) {
			typeScanner = new PackagePatternMetadataReaderScanner();
		}

		postProcessBeanDefinitionRegistry(componentResolver, context, registry, typeScanner, classLoader);
	}

	public void postProcessBeanDefinitionRegistry(ComponentResolver componentResolver, EnvironmentCapable context,
			BeanDefinitionRegistry registry, TypeScanner typeScanner, ClassLoader classLoader) throws BeansException {
		for (AnnotationMetadata annotationMetadata : scan(context, typeScanner)) {
			registerComponent(componentResolver, context, registry, annotationMetadata, classLoader);
		}
	}

	protected abstract Elements<AnnotationMetadata> scan(EnvironmentCapable context, TypeScanner classScanner);
}
