/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package scw.io;


/**
 * {@link ResourceLoader} implementation that resolves plain paths as
 * file system resources rather than as class path resources
 * (the latter is {@link DefaultResourceLoader}'s default strategy).
 *
 * <p><b>NOTE:</b> Plain paths will always be interpreted as relative
 * to the current VM working directory, even if they start with a slash.
 * (This is consistent with the semantics in a Servlet container.)
 * <b>Use an explicit "file:" prefix to enforce an absolute file path.</b>
 *
 */
public class FileSystemResourceLoader extends DefaultResourceLoader {

	/**
	 * Resolve resource paths as file system paths.
	 * <p>Note: Even if a given path starts with a slash, it will get
	 * interpreted as relative to the current VM working directory.
	 * @param path the path to the resource
	 * @return the corresponding Resource handle
	 * @see FileSystemResource
	 */
	@Override
	protected Resource getResourceByPath(String path) {
		if (path != null && path.startsWith("/")) {
			path = path.substring(1);
		}
		FileSystemResource fileSystemResource = new FileSystemContextResource(path);
		if(ignoreClassPathResource(fileSystemResource)){
			return fileSystemResource;
		}
		Resource resource = super.getResourceByPath(path);
		return new AutomaticResource(fileSystemResource, resource);
	}
	
	/**
	 * 是否应该继续查找classpath
	 * @param resource
	 * @return
	 */
	protected boolean ignoreClassPathResource(FileSystemResource resource){
		return resource.exists();
	}


	/**
	 * FileSystemResource that explicitly expresses a context-relative path
	 * through implementing the ContextResource interface.
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
