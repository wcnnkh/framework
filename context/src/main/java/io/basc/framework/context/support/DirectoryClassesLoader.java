package io.basc.framework.context.support;

import io.basc.framework.io.FileSystemResource;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DirectoryClassesLoader extends AbstractResourceClassesLoader {
	private final File directory;

	public DirectoryClassesLoader(String directory) {
		this(new File(directory));
	}

	public DirectoryClassesLoader(File directory) {
		this.directory = directory;
	}

	private void appendResources(File item, Collection<Resource> resources) {
		if (item.isDirectory()) {
			File[] files = item.listFiles();
			if (files == null) {
				return;
			}

			for (File file : files) {
				appendResources(file, resources);
			}
		} else {
			if (item.isFile() && item.getName().endsWith(SUFFIX)) {
				resources.add(new FileSystemResource(item));
			}
		}
	}

	@Override
	protected Collection<Resource> getResources(ResourceLoader resourceLoader,
			ClassLoader classLoader) throws IOException {
		List<Resource> list = new ArrayList<Resource>();
		appendResources(directory, list);
		return list;
	}
}
