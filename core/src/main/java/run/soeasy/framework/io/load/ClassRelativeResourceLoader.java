package run.soeasy.framework.io.load;

import lombok.NonNull;
import run.soeasy.framework.core.DefaultClassLoaderAccessor;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.io.ClassPathResource;
import run.soeasy.framework.io.Resource;

/**
 * {@link ResourceLoader} implementation that interprets plain resource paths as
 * relative to a given {@code java.lang.Class}.
 *
 */
public class ClassRelativeResourceLoader extends DefaultResourceLoader {

	private final Class<?> clazz;

	/**
	 * Create a new ClassRelativeResourceLoader for the given class.
	 * 
	 * @param clazz the class to load resources through
	 */
	public ClassRelativeResourceLoader(@NonNull Class<?> clazz) {
		this.clazz = clazz;
		setClassLoaderProvider(new DefaultClassLoaderAccessor(clazz));
	}

	@Override
	protected Resource getResourceByPath(String path) {
		return new ClassRelativeContextResource(path, this.clazz);
	}

	/**
	 * ClassPathResource that explicitly expresses a context-relative path through
	 * implementing the ContextResource interface.
	 */
	private static class ClassRelativeContextResource extends ClassPathResource implements ContextResource {

		private final Class<?> clazz;

		public ClassRelativeContextResource(String path, Class<?> clazz) {
			super(path, clazz);
			this.clazz = clazz;
		}

		public String getPathWithinContext() {
			return getPath();
		}

		@Override
		public Resource createRelative(String relativePath) {
			String pathToUse = StringUtils.applyRelativePath(getPath(), relativePath);
			return new ClassRelativeContextResource(pathToUse, this.clazz);
		}
	}

}
