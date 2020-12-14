package scw.util;

import java.util.Iterator;

import scw.lang.RequiredJavaVersion;

@RequiredJavaVersion(6)
public class Jdk6ServiceLoaderFactory implements ServiceLoaderFactory {

	public <S> ServiceLoader<S> getServiceLoader(Class<S> service, ClassLoader loader) {
		java.util.ServiceLoader<S> serviceLoader = java.util.ServiceLoader.load(service, loader);
		return new DefaultServiceLoader<S>(serviceLoader);
	}

	public <S> ServiceLoader<S> getServiceLoader(Class<S> service) {
		java.util.ServiceLoader<S> serviceLoader = java.util.ServiceLoader.load(service);
		return new DefaultServiceLoader<S>(serviceLoader);
	}

	public <S> ServiceLoader<S> getServiceLoaderInstalled(Class<S> service) {
		java.util.ServiceLoader<S> serviceLoader = java.util.ServiceLoader.loadInstalled(service);
		return new DefaultServiceLoader<S>(serviceLoader);
	}

	private static final class DefaultServiceLoader<S> implements ServiceLoader<S> {
		private java.util.ServiceLoader<S> serviceLoader;

		public DefaultServiceLoader(java.util.ServiceLoader<S> serviceLoader) {
			this.serviceLoader = serviceLoader;
		}

		public void reload() {
			serviceLoader.reload();
		}

		public Iterator<S> iterator() {
			return serviceLoader.iterator();
		}
	}
}
