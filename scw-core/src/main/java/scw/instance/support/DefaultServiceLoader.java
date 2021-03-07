package scw.instance.support;

import java.util.Iterator;

import scw.instance.NoArgsInstanceFactory;
import scw.instance.ServiceLoader;
import scw.util.MultiIterator;
import scw.value.factory.ValueFactory;

public class DefaultServiceLoader<S> implements ServiceLoader<S> {
	private final ServiceLoader<S> configServiceLoader;
	private final ServiceLoader<S> spiServiceLoader;
	private final ServiceLoader<S> staticServiceLoader;

	public DefaultServiceLoader(Class<S> clazz,
			NoArgsInstanceFactory instanceFactory,
			ValueFactory<String> configFactory, String... defaultNames) {
		this.configServiceLoader = new ConfigServiceLoader<S>(clazz,
				configFactory, instanceFactory);
		this.spiServiceLoader = new SpiServiceLoader<S>(clazz, instanceFactory);
		this.staticServiceLoader = new StaticServiceLoader<S>(instanceFactory,
				defaultNames);
	}

	public void reload() {
		configServiceLoader.reload();
		spiServiceLoader.reload();
		staticServiceLoader.reload();
	}

	@SuppressWarnings("unchecked")
	public Iterator<S> iterator() {
		return new MultiIterator<S>(configServiceLoader.iterator(),
				spiServiceLoader.iterator(), staticServiceLoader.iterator());
	}

}
