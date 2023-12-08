package io.basc.framework.beans.factory.spi;

import java.util.ServiceLoader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.support.DefaultServiceLoaderFactory;
import io.basc.framework.observe.register.ServiceRegistry;
import io.basc.framework.util.element.Elements;

/**
 * spi实现
 * 
 * @see ServiceLoader
 * @author shuchaowen
 *
 */
public class SPI extends DefaultServiceLoaderFactory {
	private static volatile ScheduledExecutorService defaultScheduledExecutorService;

	private static volatile SPI global;

	private static ScheduledExecutorService getDefaultScheduledExecutorService() {
		if (defaultScheduledExecutorService == null) {
			synchronized (SPI.class) {
				if (defaultScheduledExecutorService == null) {
					defaultScheduledExecutorService = Executors
							.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);
					Runtime.getRuntime().addShutdownHook(new Thread(() -> defaultScheduledExecutorService.shutdown()));
				}
			}
		}
		return defaultScheduledExecutorService;
	}

	public static ScheduledExecutorService getScheduledExecutorService() {
		return getServices(ScheduledExecutorService.class).findFirst()
				.orElseGet(() -> getDefaultScheduledExecutorService());
	}

	/**
	 * 使用全局的spi获取支持的服务
	 * 
	 * @see #global()
	 * @param <S>
	 * @param serviceClass
	 * @return
	 */
	public static <S> Elements<S> getServices(Class<S> serviceClass) {
		return global().getServiceLoader(serviceClass).getServices().cacheable();
	}

	/**
	 * 全局默认的spi
	 * 
	 * @return
	 */
	public static SPI global() {
		if (global == null) {
			synchronized (SPI.class) {
				if (global == null) {
					global = new SPI(Scope.DEFAULT);
					Thread thread = new Thread(() -> global.destroySingletons());
					Runtime.getRuntime().addShutdownHook(thread);
				}
			}
		}
		return global;
	}

	public SPI(Scope scope) {
		super(scope);
	}

	@Override
	protected <S> void postProcessorServiceRegistry(ServiceRegistry<S> serviceRegistry, Class<S> serviceClass) {
		super.postProcessorServiceRegistry(serviceRegistry, serviceClass);
		ServiceLoader<S> serviceLoader = ServiceLoader.load(serviceClass);
		JdkServiceLoader<S> jdkServiceLoader = new JdkServiceLoader<>(serviceLoader);
		serviceRegistry.registerServiceLoader(
				jdkServiceLoader.convert((elements) -> elements.peek((e) -> getServiceInjectors().inject(e))));
	}
}
