package io.basc.framework.io;

import java.io.File;
import java.util.stream.Stream;

import io.basc.framework.core.type.classreading.CachingMetadataReaderFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Cursor;
import io.basc.framework.util.ResourceClassesLoader;

public class DirectoryClassesLoader extends ResourceClassesLoader {

	public DirectoryClassesLoader(File directory) {
		this(directory, new FileSystemResourceLoader());
	}

	public DirectoryClassesLoader(File directory, ResourceLoader resourceLoader) {
		super(() -> {
			Stream<Resource> stream = FileUtils.stream(directory, (f) -> f.getName().endsWith(SUFFIX))
					.map((f) -> new FileSystemResource(f.getFile()));
			return Cursor.of(stream);
		});
		Assert.requiredArgument(resourceLoader != null, "resourceLoader");
		DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
		defaultResourceLoader.getResourceLoaders().addService(resourceLoader);
		setMetadataReaderFactory(new CachingMetadataReaderFactory(defaultResourceLoader));
	}

	public DirectoryClassesLoader(String directory) {
		this(new File(directory));
	}

	public DirectoryClassesLoader(String directory, ResourceLoader resourceLoader) {
		this(new File(directory), resourceLoader);
	}
}
