package scw.core.resource;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.util.Collection;

import scw.core.Consumer;
import scw.core.Converter;

public abstract class AbstractResourceLookup implements ResourceLookup, ResourceConstants {

	public abstract boolean lookup(String resource, Consumer<InputStream> consumer);

	public boolean lookup(String resource) {
		return lookup(resource, null);
	}

	public <T> T getResource(String resource, Converter<InputStream, T> converter) {
		InputStreamConvertConsumer<T> inputStreamConvertConsumer = new InputStreamConvertConsumer<T>(converter);
		lookup(resource, inputStreamConvertConsumer);
		return inputStreamConvertConsumer.getValue();
	}

	public Collection<Class<?>> getClasses(String resource) {
		return getClasses(resource, null);
	}

	protected void findAndAddClassesInPackageByFile(String packageName, String packagePath,
			Collection<Class<?>> classes) {
		File dir = new File(packagePath);
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		File[] dirfiles = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return (file.isDirectory()) || (file.getName().endsWith(CLASS_SUFFIX));
			}
		});
		for (File file : dirfiles) {
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), classes);
			} else {
				String className = file.getName().substring(0, file.getName().length() - 6);
				if (packageName.startsWith(".")) {
					packageName = packageName.substring(1);
				}

				Class<?> clz = null;
				try {
					clz = Class.forName(packageName + '.' + className);
				} catch (Throwable e) {
				}

				if (clz != null) {
					classes.add(clz);
				}
			}
		}
	}
}
