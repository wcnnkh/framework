package io.basc.framework.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import io.basc.framework.lang.NotFoundException;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.function.Processor;
import io.basc.framework.util.observe.watch.Variable;

public interface Resource extends InputStreamSource, Variable {
	/**
	 * 是否存在
	 * 
	 * @return
	 */
	boolean exists();

	/**
	 * 是否可读,比如一个目录是不可读的，或没有可读权限
	 * 
	 * @return
	 */
	default boolean isReadable() {
		return exists();
	}

	/**
	 * 是否是已打开的资源,如果是的那么无需关闭
	 * 
	 * @see InputStreamResource#isOpen()
	 * @return
	 */
	default boolean isOpen() {
		return false;
	}

	URL getURL() throws IOException;

	URI getURI() throws IOException;

	default boolean isFile() {
		return false;
	}

	File getFile() throws IOException, FileNotFoundException;

	long contentLength() throws IOException;

	Resource createRelative(String relativePath) throws IOException;

	/**
	 * 获取资源名称，如果是文件那应该是文件名
	 * 
	 * @return
	 */
	String getName();

	String getDescription();

	@Override
	default <T, E extends Throwable> T read(Processor<? super InputStream, ? extends T, ? extends E> processor)
			throws IOException, E {
		if (!exists()) {
			throw new NotFoundException("not found: " + getDescription());
		}

		if (!isReadable()) {
			throw new UnsupportedException("not read: " + getDescription());
		}

		InputStream is = null;
		try {
			is = getInputStream();
			return processor.process(is);
		} finally {
			if (is != null && !isOpen()) {
				is.close();
			}
		}
	}
}
