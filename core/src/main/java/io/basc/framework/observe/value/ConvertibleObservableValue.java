package io.basc.framework.observe.value;

import java.util.function.Function;

import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.actor.EventRegistrationException;
import io.basc.framework.util.actor.batch.BatchEventListener;
import io.basc.framework.util.register.Registration;
import lombok.Data;

@Data
public class ConvertibleObservableValue<S, V> implements ObservableValue<V> {
	private final ObservableValue<? extends S> source;
	private final Function<? super S, ? extends V> converter;

	@Override
	public V orElse(V other) {
		S value = source.orElse(null);
		if (value == null) {
			return other;
		}
		return converter.apply(value);
	}

	@Override
	public Registration registerBatchListener(BatchEventListener<ChangeEvent> batchEventListener)
			throws EventRegistrationException {
		return source.registerBatchListener(batchEventListener);
	}

}
