package run.soeasy.framework.core.spi;

import java.util.Comparator;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.exchange.CompositeOperation.Mode;
import run.soeasy.framework.core.exchange.Operation;
import run.soeasy.framework.core.function.ThrowingFunction;
import run.soeasy.framework.core.streaming.Streamable;

@Getter
public class Services<S> extends ServiceRegistry<S> implements ServiceInjector<S> {
	private final ServiceInjectors<S> injectors = new ServiceInjectors<>();

	public Services(@NonNull Comparator<? super S> comparator) {
		super(comparator);
	}

	@Override
	public Operation register(S element) {
		Operation injected = inject(element);
		Operation registed = super.register(element);
		return Operation.batch(Streamable.array(injected, registed), Mode.AND, ThrowingFunction.identity());
	}

	@Override
	public Operation inject(S service) {
		return injectors.inject(service);
	}

	public Operation injectAll() {
		return Operation.batch(this, Mode.AND, this::inject);
	}
}