package run.soeasy.framework.util.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;

import run.soeasy.framework.util.reflect.ReflectionUtils;

/**
 * Utility for detecting and accessing JBoss VFS in the classpath.
 *
 * <p>
 * This class supports VFS 3.x on JBoss AS 6+ (package {@code org.jboss.vfs})
 * and is in particular compatible with JBoss AS 7 and WildFly 8+.
 *
 * <p>
 * Thanks go to Marius Bogoevici for the initial implementation.
 *
 * <p>
 * <b>Note:</b> This is an internal class and should not be used outside the
 * framework.
 *
 * @author https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/core/io/VfsUtils.java
 */
public abstract class VfsUtils {

	private static final String VFS3_PKG = "org.jboss.vfs.";
	private static final String VFS_NAME = "VFS";

	private static final Method VFS_METHOD_GET_ROOT_URL;
	private static final Method VFS_METHOD_GET_ROOT_URI;

	private static final Method VIRTUAL_FILE_METHOD_EXISTS;
	private static final Method VIRTUAL_FILE_METHOD_GET_INPUT_STREAM;
	private static final Method VIRTUAL_FILE_METHOD_GET_SIZE;
	private static final Method VIRTUAL_FILE_METHOD_GET_LAST_MODIFIED;
	private static final Method VIRTUAL_FILE_METHOD_TO_URL;
	private static final Method VIRTUAL_FILE_METHOD_TO_URI;
	private static final Method VIRTUAL_FILE_METHOD_GET_NAME;
	private static final Method VIRTUAL_FILE_METHOD_GET_PATH_NAME;
	private static final Method VIRTUAL_FILE_METHOD_GET_PHYSICAL_FILE;
	private static final Method VIRTUAL_FILE_METHOD_GET_CHILD;

	protected static final Class<?> VIRTUAL_FILE_VISITOR_INTERFACE;
	protected static final Method VIRTUAL_FILE_METHOD_VISIT;

	private static final Field VISITOR_ATTRIBUTES_FIELD_RECURSE;

	static {
		ClassLoader loader = VfsUtils.class.getClassLoader();
		try {
			Class<?> vfsClass = loader.loadClass(VFS3_PKG + VFS_NAME);
			VFS_METHOD_GET_ROOT_URL = vfsClass.getMethod("getChild", URL.class);
			VFS_METHOD_GET_ROOT_URI = vfsClass.getMethod("getChild", URI.class);

			Class<?> virtualFile = loader.loadClass(VFS3_PKG + "VirtualFile");
			VIRTUAL_FILE_METHOD_EXISTS = virtualFile.getMethod("exists");
			VIRTUAL_FILE_METHOD_GET_INPUT_STREAM = virtualFile.getMethod("openStream");
			VIRTUAL_FILE_METHOD_GET_SIZE = virtualFile.getMethod("getSize");
			VIRTUAL_FILE_METHOD_GET_LAST_MODIFIED = virtualFile.getMethod("getLastModified");
			VIRTUAL_FILE_METHOD_TO_URI = virtualFile.getMethod("toURI");
			VIRTUAL_FILE_METHOD_TO_URL = virtualFile.getMethod("toURL");
			VIRTUAL_FILE_METHOD_GET_NAME = virtualFile.getMethod("getName");
			VIRTUAL_FILE_METHOD_GET_PATH_NAME = virtualFile.getMethod("getPathName");
			VIRTUAL_FILE_METHOD_GET_PHYSICAL_FILE = virtualFile.getMethod("getPhysicalFile");
			VIRTUAL_FILE_METHOD_GET_CHILD = virtualFile.getMethod("getChild", String.class);

			VIRTUAL_FILE_VISITOR_INTERFACE = loader.loadClass(VFS3_PKG + "VirtualFileVisitor");
			VIRTUAL_FILE_METHOD_VISIT = virtualFile.getMethod("visit", VIRTUAL_FILE_VISITOR_INTERFACE);

			Class<?> visitorAttributesClass = loader.loadClass(VFS3_PKG + "VisitorAttributes");
			VISITOR_ATTRIBUTES_FIELD_RECURSE = visitorAttributesClass.getField("RECURSE");
		} catch (Throwable ex) {
			throw new IllegalStateException("Could not detect JBoss VFS infrastructure", ex);
		}
	}

	protected static Object invokeVfsMethod(Method method, Object target, Object... args) throws IOException {
		try {
			return method.invoke(target, args);
		} catch (InvocationTargetException ex) {
			Throwable targetEx = ex.getTargetException();
			if (targetEx instanceof IOException) {
				throw (IOException) targetEx;
			}
			ReflectionUtils.handleInvocationTargetException(ex);
		} catch (Exception ex) {
			ReflectionUtils.handleReflectionException(ex);
		}

		throw new IllegalStateException("Invalid code path reached");
	}

	static boolean exists(Object vfsResource) {
		try {
			return (Boolean) invokeVfsMethod(VIRTUAL_FILE_METHOD_EXISTS, vfsResource);
		} catch (IOException ex) {
			return false;
		}
	}

	static boolean isReadable(Object vfsResource) {
		try {
			return (Long) invokeVfsMethod(VIRTUAL_FILE_METHOD_GET_SIZE, vfsResource) > 0;
		} catch (IOException ex) {
			return false;
		}
	}

	static long getSize(Object vfsResource) throws IOException {
		return (Long) invokeVfsMethod(VIRTUAL_FILE_METHOD_GET_SIZE, vfsResource);
	}

	static long getLastModified(Object vfsResource) throws IOException {
		return (Long) invokeVfsMethod(VIRTUAL_FILE_METHOD_GET_LAST_MODIFIED, vfsResource);
	}

	static InputStream getInputStream(Object vfsResource) throws IOException {
		return (InputStream) invokeVfsMethod(VIRTUAL_FILE_METHOD_GET_INPUT_STREAM, vfsResource);
	}

	static URL getURL(Object vfsResource) throws IOException {
		return (URL) invokeVfsMethod(VIRTUAL_FILE_METHOD_TO_URL, vfsResource);
	}

	static URI getURI(Object vfsResource) throws IOException {
		return (URI) invokeVfsMethod(VIRTUAL_FILE_METHOD_TO_URI, vfsResource);
	}

	static String getName(Object vfsResource) {
		try {
			return (String) invokeVfsMethod(VIRTUAL_FILE_METHOD_GET_NAME, vfsResource);
		} catch (IOException ex) {
			throw new IllegalStateException("Cannot get resource name", ex);
		}
	}

	static Object getRelative(URL url) throws IOException {
		return invokeVfsMethod(VFS_METHOD_GET_ROOT_URL, null, url);
	}

	static Object getChild(Object vfsResource, String path) throws IOException {
		return invokeVfsMethod(VIRTUAL_FILE_METHOD_GET_CHILD, vfsResource, path);
	}

	static File getFile(Object vfsResource) throws IOException {
		return (File) invokeVfsMethod(VIRTUAL_FILE_METHOD_GET_PHYSICAL_FILE, vfsResource);
	}

	static Object getRoot(URI url) throws IOException {
		return invokeVfsMethod(VFS_METHOD_GET_ROOT_URI, null, url);
	}

	// protected methods used by the support sub-package

	protected static Object getRoot(URL url) throws IOException {
		return invokeVfsMethod(VFS_METHOD_GET_ROOT_URL, null, url);
	}

	protected static Object doGetVisitorAttributes() {
		return ReflectionUtils.get(VISITOR_ATTRIBUTES_FIELD_RECURSE, null);
	}

	protected static String doGetPath(Object resource) {
		return (String) ReflectionUtils.invoke(VIRTUAL_FILE_METHOD_GET_PATH_NAME, resource);
	}

}
