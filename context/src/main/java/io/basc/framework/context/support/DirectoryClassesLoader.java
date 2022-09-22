package io.basc.framework.context.support;

import java.io.File;
import java.util.stream.Stream;

import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.io.DefaultResourceLoader;
import io.basc.framework.io.FileSystemResource;
import io.basc.framework.io.FileSystemResourceLoader;
import io.basc.framework.io.FileUtils;
import io.basc.framework.io.Resource;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.value.ValueFactory;

public class DirectoryClassesLoader extends AbstractClassesLoader {
	private final File directory;
	private final TypeFilter typeFilter;

	public DirectoryClassesLoader(String directory, @Nullable ValueFactory<String> propertyFactory) {
		this(new File(directory), propertyFactory);
	}

	public DirectoryClassesLoader(File directory, @Nullable ValueFactory<String> propertyFactory) {
		this.directory = directory;
		this.typeFilter = new ContextTypeFilter(propertyFactory);
	}

	public DirectoryClassesLoader(String directory, @Nullable TypeFilter typeFilter) {
		this(new File(directory), typeFilter);
	}

	public DirectoryClassesLoader(File directory, @Nullable TypeFilter typeFilter) {
		this.directory = directory;
		this.typeFilter = typeFilter;
	}

	@Override
	protected Stream<Class<?>> load(ClassLoader classLoader) {
		DefaultResourceLoader resourceLoader = new DefaultResourceLoader(classLoader);
		resourceLoader.getResourceLoaders().addService(new FileSystemResourceLoader());

		Stream<Resource> resources = FileUtils
				.stream(directory, (f) -> f.getName().endsWith(ClassUtils.CLASS_FILE_SUFFIX))
				.map((f) -> new FileSystemResource(f.getFile()));
		return ClassUtils.forResources(resourceLoader, resources, classLoader, null, typeFilter);
	};
}
