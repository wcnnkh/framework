package run.soeasy.framework.core.exchange.container;

import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class DisposableKeyValueRegistration<K, V> extends AbstractLifecycleRegistration
		implements KeyValueRegistration<K, V> {
	private final K key;
	private final V value;
	@NonNull
	private final BiPredicate<? super K, ? super V> runnable;

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public boolean cancel(BooleanSupplier cancel) {
		return super.cancel(() -> {
			if (cancel.getAsBoolean()) {
				return runnable.test(key, value);
			}
			return false;
		});
	}
}