package run.soeasy.framework.core.spi;

import java.util.Comparator;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.exchange.Signal;
import run.soeasy.framework.core.streaming.Streamable;

@Getter
public class Services<S> extends ServiceRegistry<S> implements ServiceInjector<S> {
	private final ServiceInjectors<S> injectors = new ServiceInjectors<>();

	public Services(@NonNull Comparator<? super S> comparator) {
		super(comparator);
	}

	@Override
	public Signal register(S element) {
		Signal injected = inject(element);
		Signal registed = super.register(element);
		return Signal.batch(Streamable.array(injected, registed));
	}

	@Override
	public Signal inject(S service) {
		return injectors.inject(service);
	}

	public Signal injectAll() {
		return Signal.batch(this.map(this::inject));
	}
}