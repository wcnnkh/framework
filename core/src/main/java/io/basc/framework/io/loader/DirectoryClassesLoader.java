package io.basc.framework.io.loader;

import java.io.File;

import io.basc.framework.core.type.classreading.CachingMetadataReaderFactory;
import io.basc.framework.io.DefaultResourceLoader;
import io.basc.framework.io.FileSystemResource;
import io.basc.framework.io.FileSystemResourceLoader;
import io.basc.framework.io.FileUtils;
import io.basc.framework.io.ResourceLoader;
import io.basc.framework.util.Assert;
import io.basc.framework.util.element.Elements;

public class DirectoryClassesLoader extends ResourceClassesLoader {

	public DirectoryClassesLoader(File directory) {
		this(directory, new FileSystemResourceLoader());
	}

	public DirectoryClassesLoader(File directory, ResourceLoader resourceLoader) {
		super(Elements.of(() -> {
			return FileUtils.stream(directory, (f) -> f.getName().endsWith(FILE_SUFFIX))
					.map((f) -> new FileSystemResource(f.getFile()));
		}));
		Assert.requiredArgument(resourceLoader != null, "resourceLoader");
		DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
		defaultResourceLoader.getResourceLoaders().register(resourceLoader);
		setMetadataReaderFactory(new CachingMetadataReaderFactory(defaultResourceLoader));
	}

	public DirectoryClassesLoader(String directory) {
		this(new File(directory));
	}

	public DirectoryClassesLoader(String directory, ResourceLoader resourceLoader) {
		this(new File(directory), resourceLoader);
	}
}
