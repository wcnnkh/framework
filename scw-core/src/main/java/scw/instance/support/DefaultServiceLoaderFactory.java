package scw.instance.support;

import scw.core.Constants;
import scw.env.Environment;
import scw.instance.NoArgsInstanceFactory;
import scw.instance.ServiceLoader;
import scw.lang.Nullable;
import scw.util.Accept;
import scw.value.ValueFactory;

public class DefaultServiceLoaderFactory extends SpiServiceLoaderFactory {
	private final Accept<Class<?>> spiAccept;
	private final ValueFactory<String> configFactory;

	public DefaultServiceLoaderFactory(ValueFactory<String> valueFactory) {
		this(valueFactory, new DefaultSpiAccept());
	}

	/**
	 * 默念的服务加载行为
	 * 
	 * @param environment
	 */
	public DefaultServiceLoaderFactory(Environment environment) {
		this(new DefaultInstanceFactory(environment, false), environment);
	}

	/**
	 * 默认仅对{@link Constants#SYSTEM_PACKAGE_NAME}}包下的类使用spi
	 * 
	 * @param instanceFactory
	 * @param configFactory
	 */
	public DefaultServiceLoaderFactory(NoArgsInstanceFactory instanceFactory, ValueFactory<String> configFactory) {
		this(instanceFactory, configFactory, new DefaultSpiAccept());
	}

	/**
	 * @param configFactory
	 * @param spiAccept     如果为空就对所有的类都使用spi机制
	 */
	public DefaultServiceLoaderFactory(ValueFactory<String> configFactory, @Nullable Accept<Class<?>> spiAccept) {
		super(new SimpleNoArgsInstanceFactory());
		this.spiAccept = spiAccept;
		this.configFactory = configFactory;
	}

	/**
	 * @param instanceFactory
	 * @param configFactory
	 * @param spiAccept       如果为空就对所有的类都使用spi机制
	 */
	public DefaultServiceLoaderFactory(NoArgsInstanceFactory instanceFactory, ValueFactory<String> configFactory,
			@Nullable Accept<Class<?>> spiAccept) {
		super(instanceFactory);
		this.spiAccept = spiAccept;
		this.configFactory = configFactory;
	}

	public <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass) {
		ServiceLoader<S> configServiceLoader = new ConfigServiceLoader<S>(serviceClass, configFactory,
				getInstanceFactory());
		ServiceLoader<S> spiServiceLoader = null;
		if (spiAccept == null || spiAccept.accept(serviceClass)) {
			spiServiceLoader = super.getServiceLoader(serviceClass);
		}
		return new ServiceLoaders<S>(configServiceLoader, spiServiceLoader);
	}

	private static final class DefaultSpiAccept implements Accept<Class<?>> {

		public boolean accept(Class<?> e) {
			return e.getName().startsWith(Constants.SYSTEM_PACKAGE_NAME);
		}

	}
}
