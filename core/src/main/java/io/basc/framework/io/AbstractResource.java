package io.basc.framework.io;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventDispatcher;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistration;
import io.basc.framework.io.event.SimpleResourceEventDispatcher;
import io.basc.framework.lang.NestedIOException;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.util.Assert;
import io.basc.framework.util.JavaVersion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Convenience base class for {@link Resource} implementations, pre-implementing
 * typical behavior.
 *
 * <p>
 * The "exists" method will check whether a File or InputStream can be opened;
 * "isOpen" will always return false; "getURL" and "getFile" throw an exception;
 * and "toString" will return the description.
 *
 */
public abstract class AbstractResource implements Resource, EventDispatcher<ChangeEvent<Resource>> {
	private static final Constructor<EventDispatcher<ChangeEvent<Resource>>> WATCH_SERVICE_CONSTRUCTOR = ReflectionUtils
			.findConstructor("io.basc.framework.io.event.WatchServiceResourceEventDispatcher", null, true,
					Resource.class);

	private volatile EventDispatcher<ChangeEvent<Resource>> eventDispatcher;

	private EventDispatcher<ChangeEvent<Resource>> getEventDispatcher() {
		if (eventDispatcher == null) {
			synchronized (this) {
				if (eventDispatcher == null) {
					if (JavaVersion.INSTANCE.getMasterVersion() >= 7) {
						try {
							eventDispatcher = WATCH_SERVICE_CONSTRUCTOR.newInstance(this);
						} catch (Exception e) {
							ReflectionUtils.handleReflectionException(e);
						}
					}

					if (eventDispatcher == null) {
						eventDispatcher = new SimpleResourceEventDispatcher(this);
					}
				}
			}
		}
		return eventDispatcher;
	}

	/**
	 * 是否监听jar包
	 * 
	 * @return
	 */
	protected boolean isObserableJar() {
		return false;
	}

	public boolean isObservable() {
		if (exists()) {
			try {
				if (ResourceUtils.isJarURL(getURL())) {
					return isObserableJar();
				}
			} catch (IOException e) {
			}
		}
		return true;
	}

	@Override
	public EventRegistration registerListener(EventListener<ChangeEvent<Resource>> eventListener) {
		if (!isObservable()) {
			return EventRegistration.EMPTY;
		}
		return getEventDispatcher().registerListener(eventListener);
	}

	@Override
	public void publishEvent(ChangeEvent<Resource> event) {
		if (isObservable()) {
			getEventDispatcher().publishEvent(event);
		}
	}

	/**
	 * This implementation checks whether a File can be opened, falling back to
	 * whether an InputStream can be opened. This will cover both directories and
	 * content resources.
	 */
	public boolean exists() {
		// Try file existence: can we find the file in the file system?
		try {
			return getFile().exists();
		} catch (IOException ex) {
			// Fall back to stream existence: can we open the stream?
			try {
				getInputStream().close();
				return true;
			} catch (Throwable isEx) {
				return false;
			}
		}
	}

	/**
	 * This implementation always returns {@code true}.
	 */
	public boolean isReadable() {
		return true;
	}

	/**
	 * This implementation always returns {@code false}.
	 */
	public boolean isOpen() {
		return false;
	}

	/**
	 * This implementation throws a FileNotFoundException, assuming that the
	 * resource cannot be resolved to a URL.
	 */
	public URL getURL() throws IOException {
		throw new FileNotFoundException(getDescription() + " cannot be resolved to URL");
	}

	/**
	 * This implementation builds a URI based on the URL returned by
	 * {@link #getURL()}.
	 */
	public URI getURI() throws IOException {
		URL url = getURL();
		try {
			return ResourceUtils.toURI(url);
		} catch (URISyntaxException ex) {
			throw new NestedIOException("Invalid URI [" + url + "]", ex);
		}
	}

	/**
	 * This implementation throws a FileNotFoundException, assuming that the
	 * resource cannot be resolved to an absolute file path.
	 */
	public File getFile() throws IOException {
		throw new FileNotFoundException(getDescription() + " cannot be resolved to absolute file path");
	}

	/**
	 * This implementation reads the entire InputStream to calculate the content
	 * length. Subclasses will almost always be able to provide a more optimal
	 * version of this, e.g. checking a File length.
	 * 
	 * @see #getInputStream()
	 */
	public long contentLength() throws IOException {
		InputStream is = getInputStream();
		Assert.state(is != null, "Resource InputStream must not be null");
		try {
			long size = 0;
			byte[] buf = new byte[256];
			int read;
			while ((read = is.read(buf)) != -1) {
				size += read;
			}
			return size;
		} finally {
			try {
				is.close();
			} catch (IOException ex) {
			}
		}
	}

	/**
	 * This implementation checks the timestamp of the underlying File, if
	 * available.
	 * 
	 * @see #getFileForLastModifiedCheck()
	 */
	public long lastModified() throws IOException {
		File fileToCheck = getFileForLastModifiedCheck();
		long lastModified = fileToCheck.lastModified();
		if (lastModified == 0L && !fileToCheck.exists()) {
			throw new FileNotFoundException(getDescription()
					+ " cannot be resolved in the file system for checking its last-modified timestamp");
		}
		return lastModified;
	}

	/**
	 * Determine the File to use for timestamp checking.
	 * <p>
	 * The default implementation delegates to {@link #getFile()}.
	 * 
	 * @return the File to use for timestamp checking (never {@code null})
	 * @throws FileNotFoundException if the resource cannot be resolved as an
	 *                               absolute file path, i.e. is not available in a
	 *                               file system
	 * @throws IOException           in case of general resolution/reading failures
	 */
	protected File getFileForLastModifiedCheck() throws IOException {
		return getFile();
	}

	/**
	 * This implementation throws a FileNotFoundException, assuming that relative
	 * resources cannot be created for this resource.
	 */
	public Resource createRelative(String relativePath) throws IOException {
		throw new FileNotFoundException("Cannot create a relative resource for " + getDescription());
	}

	/**
	 * This implementation always returns {@code null}, assuming that this resource
	 * type does not have a filename.
	 */
	public String getName() {
		return null;
	}

	/**
	 * This implementation returns the description of this resource.
	 * 
	 * @see #getDescription()
	 */
	public String toString() {
		return getDescription();
	}

	/**
	 * This implementation compares description strings.
	 * 
	 * @see #getDescription()
	 */
	public boolean equals(Object obj) {
		return (obj == this || (obj instanceof Resource && ((Resource) obj).getDescription().equals(getDescription())));
	}

	/**
	 * This implementation returns the description's hash code.
	 * 
	 * @see #getDescription()
	 */
	public int hashCode() {
		return getDescription().hashCode();
	}

	public boolean isWritable() {
		return false;
	}

	public OutputStream getOutputStream() throws IOException {
		throw new NotSupportedException(getDescription());
	}
}
