package scw.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.EventRegistry;
import scw.lang.NotFoundException;
import scw.lang.NotSupportedException;

public interface Resource extends InputStreamSource, EventRegistry<ChangeEvent<Resource>> {
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

	long lastModified() throws IOException;

	Resource createRelative(String relativePath) throws IOException;

	/**
	 * 获取资源名称，如果是文件那应该是文件名
	 * 
	 * @return
	 */
	String getName();

	String getDescription();

	default EventRegistration registerListener(EventListener<ChangeEvent<Resource>> eventListener) {
		return EventRegistration.EMPTY;
	}

	default <T> T read(IoProcessor<InputStream, ? extends T> processor) throws IOException {
		if (!exists()) {
			throw new NotFoundException("not found: " + getDescription());
		}

		if (!isReadable()) {
			throw new NotSupportedException("not read: " + getDescription());
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
