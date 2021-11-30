package io.basc.framework.context.support;

import java.io.File;
import java.util.stream.Stream;

import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.env.Environment;
import io.basc.framework.io.DefaultResourceLoader;
import io.basc.framework.io.FileSystemResource;
import io.basc.framework.io.FileSystemResourceLoader;
import io.basc.framework.io.FileUtils;
import io.basc.framework.io.Resource;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ClassUtils;

public class DirectoryClassesLoader extends AbstractClassesLoader {
	private final File directory;
	private final TypeFilter typeFilter;

	public DirectoryClassesLoader(String directory, @Nullable Environment environment) {
		this(new File(directory), environment);
	}

	public DirectoryClassesLoader(File directory, @Nullable Environment environment) {
		ContextTypeFilter typeFilter = new ContextTypeFilter(environment);
		this.directory = directory;
		this.typeFilter = typeFilter;
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
		resourceLoader.addResourceLoader(new FileSystemResourceLoader());

		Stream<Resource> resources = FileUtils
				.stream(directory, (f) -> f.getName().endsWith(ClassUtils.CLASS_FILE_SUFFIX))
				.map((f) -> new FileSystemResource(f.getFile()));
		return ClassUtils.forResources(resourceLoader, resources, classLoader, null, typeFilter);
	};
}
