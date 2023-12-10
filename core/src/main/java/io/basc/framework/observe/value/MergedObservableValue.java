package io.basc.framework.observe.value;

import io.basc.framework.observe.register.ObservableRegistry;
import io.basc.framework.observe.register.Registry;
import io.basc.framework.util.Assert;
import io.basc.framework.util.select.Selector;

public class MergedObservableValue<V> extends AtomicObservableValue<V> {
	private final Registry<? extends ObservableValue<V>> registry;
	private Selector<V> selector = Selector.first();

	public MergedObservableValue() {
		this(new ObservableRegistry<>());
	}

	public MergedObservableValue(Registry<? extends ObservableValue<V>> registry) {
		this.registry = registry;
		registry.registerBatchListener((e) -> reload());
	}

	public Registry<? extends ObservableValue<V>> getRegistry() {
		return registry;
	}

	public V getMergedValue() {
		return getSelector().apply(getRegistry().getServices().map((e) -> e.orElse(null)).filter((e) -> e != null));
	}

	public void reload() {
		setValue(getMergedValue());
	}

	public Selector<V> getSelector() {
		return selector;
	}

	public void setSelector(Selector<V> selector) {
		Assert.requiredArgument(selector != null, "selector");
		this.selector = selector;
		reload();
	}
}
