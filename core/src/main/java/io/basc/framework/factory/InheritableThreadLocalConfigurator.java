package io.basc.framework.factory;

import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.AnyInheriterRegistry;
import io.basc.framework.util.Source;
import io.basc.framework.util.Status;

public class InheritableThreadLocalConfigurator<T> extends InheritableThreadLocal<T> implements Configurable {
	private static Logger logger = LoggerFactory.getLogger(InheritableThreadLocalConfigurator.class);
	private final Class<T> serviceClass;
	private boolean configured;
	private volatile T defaultService;

	public InheritableThreadLocalConfigurator(Class<T> serviceClass) {
		this(serviceClass, null);
	}

	public InheritableThreadLocalConfigurator(Class<T> serviceClass, @Nullable ServiceLoaderFactory serviceLoaderFactory) {
		this.serviceClass = serviceClass;
		if (serviceLoaderFactory != null) {
			configure(serviceLoaderFactory);
		}
		AnyInheriterRegistry.global().register(this);
	}

	@Override
	public boolean isConfigured() {
		return configured;
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		if (setDefaultService(() -> serviceLoaderFactory.getServiceLoader(serviceClass).first()).isSuccess()) {
			this.configured = true;
		}
	}

	/**
	 * 设置默认服务
	 * 
	 * @param source 默认服务来源
	 * @return 如果设置失败, 原因1表示获取服务为空，2表示获取异常
	 */
	public Status setDefaultService(Source<? extends T, ? extends Throwable> source) {
		try {
			T service = source.get();
			if (service == null) {
				return Status.error(1, "Get configuration is empty");
			}

			this.defaultService = service;
			configured = true;
			logger.info("Set the default {} to {}", serviceClass, service);
			return Status.success();
		} catch (Throwable e) {
			logger.error(e, "Failed to set the default {}", serviceClass);
			return Status.error(2, e.getMessage());
		}
	}

	/**
	 * 如果默认服务不存在，默认设置服务
	 * 
	 * @param source 服务来源
	 * @return this
	 */
	public InheritableThreadLocalConfigurator<T> ifAbsentDefaultService(Source<? extends T, ? extends Throwable> source) {
		setDefaultService(source);
		return this;
	}

	@Nullable
	public T getDefaultService() {
		return defaultService;
	}

	public void setDefaultService(T defaultService) {
		this.defaultService = defaultService;
	}

	public Class<T> getServiceClass() {
		return serviceClass;
	}

	@Override
	protected T initialValue() {
		if (logger.isDebugEnabled()) {
			logger.debug("The initial {} is {}", serviceClass, defaultService);
		}
		return defaultService;
	}

	@Override
	public void set(T value) {
		if (logger.isDebugEnabled()) {
			logger.debug("Set the current {} to {}", serviceClass, value);
		}
		super.set(value);
	}

	@Override
	public void remove() {
		if (logger.isDebugEnabled()) {
			logger.debug("Remove the current {}", serviceClass);
		}
		super.remove();
	}

	@Override
	public String toString() {
		return "The configuration of " + serviceClass + " is " + defaultService + " by default, and is currently "
				+ get();
	}
}