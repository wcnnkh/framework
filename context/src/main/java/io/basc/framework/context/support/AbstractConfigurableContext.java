package io.basc.framework.context.support;

import java.io.IOException;

import io.basc.framework.context.ClassesLoader;
import io.basc.framework.context.ClassesLoaderFactory;
import io.basc.framework.context.ConfigurableClassesLoader;
import io.basc.framework.context.ConfigurableContext;
import io.basc.framework.context.annotation.AbstractProviderServiceLoaderFactory;
import io.basc.framework.context.annotation.ComponentScan;
import io.basc.framework.context.annotation.ComponentScans;
import io.basc.framework.context.annotation.EnableConditionUtils;
import io.basc.framework.context.annotation.Indexed;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.core.type.ClassMetadata;
import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.core.type.scanner.ConfigurableClassScanner;
import io.basc.framework.core.type.scanner.DefaultClassScanner;
import io.basc.framework.env.ConfigurableEnvironment;
import io.basc.framework.env.DefaultEnvironment;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.lang.Constants;
import io.basc.framework.lang.NestedExceptionUtils;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.value.ValueFactory;

public abstract class AbstractConfigurableContext extends AbstractProviderServiceLoaderFactory
		implements ConfigurableContext, Configurable, TypeFilter {
	private static Logger logger = LoggerFactory.getLogger(AbstractConfigurableContext.class);
	private final DefaultClassScanner classScanner = new DefaultClassScanner();
	private final DefaultClassesLoaderFactory classesLoaderFactory;
	private final LinkedHashSetClassesLoader sourceClasses = new LinkedHashSetClassesLoader();
	private final DefaultEnvironment environment = new DefaultEnvironment(this);
	private final DefaultClassesLoader contextClassesLoader = new DefaultClassesLoader();

	public AbstractConfigurableContext(boolean cache) {
		super(cache);
		this.classesLoaderFactory = new DefaultClassesLoaderFactory(classScanner, this);
		// 添加默认的类
		contextClassesLoader.add(sourceClasses);

		// 扫描框架类，忽略(.test.)路径
		componentScan(Constants.SYSTEM_PACKAGE_NAME, (e, m) -> !e.getClassMetadata().getClassName().contains(".test."));
	}

	/**
	 * @see #componentScan(String)
	 * @see #componentScan(String, TypeFilter)
	 */
	@Override
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
			throws IOException {
		ClassMetadata classMetadata = metadataReader.getClassMetadata();
		if (classMetadata.isEnum() || classMetadata.isAnnotation()) {
			return false;
		}

		AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
		if (annotationMetadata.getAnnotationTypes().isEmpty()
				&& !annotationMetadata.hasAnnotatedMethods(Indexed.class.getName())
				&& !annotationMetadata.hasMetaAnnotation(Indexed.class.getName())) {
			return false;
		}

		return classMetadata.isPublic() && EnableConditionUtils.enable(metadataReader, environment);
	}

	@Override
	protected boolean useSpi(Class<?> serviceClass) {
		for (Class<?> sourceClass : sourceClasses) {
			Package pg = sourceClass.getPackage();
			if (pg == null) {
				continue;
			}

			if (serviceClass.getName().startsWith(pg.getName())) {
				return true;
			}
		}
		return super.useSpi(serviceClass);
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		environment.configure(serviceLoaderFactory);
		contextClassesLoader.configure(serviceLoaderFactory);
	}

	@Override
	protected ClassesLoader getScanClassesLoader() {
		return contextClassesLoader;
	}

	@Override
	protected ValueFactory<String> getConfigFactory() {
		return environment;
	}

	@Override
	public ConfigurableEnvironment getEnvironment() {
		return environment;
	}

	@Override
	public ConfigurableClassesLoader getContextClasses() {
		return contextClassesLoader;
	}

	@Override
	public ClassesLoaderFactory getClassesLoaderFactory() {
		return classesLoaderFactory;
	}

	@Override
	public ConfigurableClassScanner getClassScanner() {
		return classScanner;
	}

	@Override
	public LinkedHashSetClassesLoader getSourceClasses() {
		return sourceClasses;
	}

	@Override
	public void source(Class<?> sourceClass) {
		if (!sourceClasses.add(sourceClass)) {
			throw new IllegalArgumentException("Already source " + sourceClass);
		}

		if (sourceClass.getPackage() != null) {
			componentScan(sourceClass.getPackage().getName());
		}

		ComponentScan componentScan = sourceClass.getAnnotation(ComponentScan.class);
		if (componentScan != null) {
			componentScan(componentScan);
		}

		ComponentScans componentScans = sourceClass.getAnnotation(ComponentScans.class);
		if (componentScans != null) {
			for (ComponentScan scan : componentScans.value()) {
				componentScan(scan);
			}
		}
	}

	private void componentScan(ComponentScan componentScan) {
		for (String name : componentScan.value()) {
			componentScan(name);
		}

		for (String name : componentScan.basePackages()) {
			componentScan(name);
		}
	}

	public void componentScan(String packageName) {
		componentScan(packageName, null);
	}

	public void componentScan(String packageName, TypeFilter typeFilter) {
		ClassesLoader classesLoader = getClassesLoaderFactory().getClassesLoader(packageName,
				(e, m) -> match(e, m) && (typeFilter == null || typeFilter.match(e, m)));
		getContextClasses().add(new AcceptClassesLoader(classesLoader, (c) -> {
			return ClassUtils.isAvailable(c) && ReflectionUtils.isAvailable(c, (e) -> {
				if (logger.isTraceEnabled()) {
					logger.error(e, "This class[{}] cannot be included because:", c.getName());
				} else if (logger.isDebugEnabled()) {
					logger.debug("This class[{}] cannot be included because: {}", c.getName(),
							NestedExceptionUtils.getNonEmptyMessage(e, false));
				}
				return false;
			});
		}, false));
	}
}
