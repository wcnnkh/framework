package run.soeasy.framework.core.spi;

import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListMap;

import lombok.Getter;
import run.soeasy.framework.core.exchange.CompositeOperation.Mode;
import run.soeasy.framework.core.exchange.MapRegistry;
import run.soeasy.framework.core.exchange.Operation;
import run.soeasy.framework.core.function.ThrowingFunction;
import run.soeasy.framework.core.streaming.Streamable;

@Getter
public class ServiceMap<K, S> extends MapRegistry<K, S, ConcurrentSkipListMap<K, S>>
		implements ServiceInjector<S> {
	private final ServiceInjectors<S> injectors = new ServiceInjectors<>();

	public ServiceMap(Comparator<? super K> comparator) {
		super(new ConcurrentSkipListMap<>(comparator));
	}

	@Override
	public Operation inject(S service) {
		return injectors.inject(service);
	}

	public Operation injectAll() {
		return Operation.batch(map((e) -> e.getValue()), Mode.AND, this::inject);
	}

	@Override
	public Operation register(K key, S value) {
		Operation injected = inject(value);
		Operation registed = super.register(key, value);
		return Operation.batch(Streamable.array(injected, registed), Mode.AND, ThrowingFunction.identity());
	}
}