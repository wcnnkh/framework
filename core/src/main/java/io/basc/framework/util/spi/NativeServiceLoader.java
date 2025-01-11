package io.basc.framework.util.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

import lombok.RequiredArgsConstructor;

/**
 * jdk原生实现
 * 
 * @author shuchaowen
 *
 * @param <S>
 */
@RequiredArgsConstructor
public class NativeServiceLoader<S> implements io.basc.framework.util.collections.ServiceLoader<S> {
	/**
	 * Creates a new service loader for the given service type, using the current
	 * thread's {@linkplain java.lang.Thread#getContextClassLoader context class
	 * loader}.
	 *
	 * <p>
	 * An invocation of this convenience method of the form
	 *
	 * <blockquote>
	 * 
	 * <pre>
	 * ServiceLoader.load(<i>service</i>)
	 * </pre>
	 * 
	 * </blockquote>
	 *
	 * is equivalent to
	 *
	 * <blockquote>
	 * 
	 * <pre>
	 * ServiceLoader.load(<i>service</i>,
	 *                    Thread.currentThread().getContextClassLoader())
	 * </pre>
	 * 
	 * </blockquote>
	 *
	 * @param <S>     the class of the service type
	 *
	 * @param service The interface or abstract class representing the service
	 *
	 * @return A new service loader
	 */
	public static <S> io.basc.framework.util.collections.ServiceLoader<S> load(Class<S> service) {
		ServiceLoader<S> serviceLoader = ServiceLoader.load(service);
		return new NativeServiceLoader<>(serviceLoader);
	}

	/**
	 * Creates a new service loader for the given service type and class loader.
	 *
	 * @param <S>     the class of the service type
	 *
	 * @param service The interface or abstract class representing the service
	 *
	 * @param loader  The class loader to be used to load provider-configuration
	 *                files and provider classes, or <tt>null</tt> if the system
	 *                class loader (or, failing that, the bootstrap class loader) is
	 *                to be used
	 *
	 * @return A new service loader
	 */
	public static <S> io.basc.framework.util.collections.ServiceLoader<S> load(Class<S> service, ClassLoader loader) {
		ServiceLoader<S> serviceLoader = ServiceLoader.load(service, loader);
		return new NativeServiceLoader<>(serviceLoader);
	}

	/**
	 * Creates a new service loader for the given service type, using the extension
	 * class loader.
	 *
	 * <p>
	 * This convenience method simply locates the extension class loader, call it
	 * <tt><i>extClassLoader</i></tt>, and then returns
	 *
	 * <blockquote>
	 * 
	 * <pre>
	 * ServiceLoader.load(<i>service</i>, <i>extClassLoader</i>)
	 * </pre>
	 * 
	 * </blockquote>
	 *
	 * <p>
	 * If the extension class loader cannot be found then the system class loader is
	 * used; if there is no system class loader then the bootstrap class loader is
	 * used.
	 *
	 * <p>
	 * This method is intended for use when only installed providers are desired.
	 * The resulting service will only find and load providers that have been
	 * installed into the current Java virtual machine; providers on the
	 * application's class path will be ignored.
	 *
	 * @param <S>     the class of the service type
	 *
	 * @param service The interface or abstract class representing the service
	 *
	 * @return A new service loader
	 */
	public static <S> io.basc.framework.util.collections.ServiceLoader<S> loadInstalled(Class<S> service) {
		ServiceLoader<S> serviceLoader = ServiceLoader.loadInstalled(service);
		return new NativeServiceLoader<>(serviceLoader);
	}

	private final ServiceLoader<S> serviceLoader;

	@Override
	public Iterator<S> iterator() {
		return serviceLoader.iterator();
	}

	@Override
	public void reload() {
		serviceLoader.reload();
	}
}
