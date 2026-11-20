package run.soeasy.framework.core.spi;

import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListMap;

import lombok.Getter;
import run.soeasy.framework.core.exchange.MapContainer;
import run.soeasy.framework.core.exchange.Signal;
import run.soeasy.framework.core.streaming.Streamable;

@Getter
public class ServiceMap<K, S> extends MapContainer<K, S, ConcurrentSkipListMap<K, S>>
		implements ServiceInjector<S> {
	private final ServiceInjectors<S> injectors = new ServiceInjectors<>();

	public ServiceMap(Comparator<? super K> comparator) {
		super(new ConcurrentSkipListMap<>(comparator));
	}

	@Override
	public Signal inject(S service) {
		return injectors.inject(service);
	}

	public Signal injectAll() {
		return Signal.batch(map((e) -> e.getValue()).map(this::inject));
	}

	@Override
	public Signal register(K key, S value) {
		Signal injected = inject(value);
		Signal registed = super.register(key, value);
		return Signal.batch(Streamable.array(injected, registed));
	}
}