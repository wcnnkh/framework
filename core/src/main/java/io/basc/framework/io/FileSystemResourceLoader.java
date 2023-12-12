package io.basc.framework.io;

/**
 * {@link ResourceLoader} implementation that resolves plain paths as file
 * system resources rather than as class path resources (the latter is
 * {@link DefaultResourceLoader}'s default strategy).
 *
 * <p>
 * <b>NOTE:</b> Plain paths will always be interpreted as relative to the
 * current VM working directory, even if they start with a slash. (This is
 * consistent with the semantics in a Servlet container.) <b>Use an explicit
 * "file:" prefix to enforce an absolute file path.</b>
 *
 */
public class FileSystemResourceLoader extends DefaultResourceLoader {

	/**
	 * Resolve resource paths as file system paths.
	 * <p>
	 * Note: Even if a given path starts with a slash, it will get interpreted as
	 * relative to the current VM working directory.
	 * 
	 * @param path the path to the resource
	 * @return the corresponding Resource handle
	 * @see FileSystemResource
	 */
	@Override
	protected Resource getResourceByPath(String path) {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		return new FileSystemContextResource(path);
	}

	/**
	 * FileSystemResource that explicitly expresses a context-relative path through
	 * implementing the ContextResource interface.
	 */
	private static class FileSystemContextResource extends FileSystemResource implements ContextResource {

		public FileSystemContextResource(String path) {
			super(path);
		}

		public String getPathWithinContext() {
			return getPath();
		}
	}

}
