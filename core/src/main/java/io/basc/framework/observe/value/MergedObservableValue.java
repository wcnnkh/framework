package io.basc.framework.observe.value;

import io.basc.framework.observe.container.ServiceRegistry;
import io.basc.framework.observe.register.ObservableRegistry;
import io.basc.framework.util.Assert;
import io.basc.framework.util.select.Selector;

public class MergedObservableValue<V> extends AtomicObservableValue<V> {
	private final NavigableElementRegistry<? extends ObservableValue<V>> registry;
	private Selector<V> selector = Selector.first();

	public MergedObservableValue() {
		this(new ObservableRegistry<>());
	}

	public MergedObservableValue(ServiceRegistry<? extends ObservableValue<V>> registry) {
		this.registry = registry;
		registry.registerBatchListener((e) -> reload());
	}

	public NavigableElementRegistry<? extends ObservableValue<V>> getRegistry() {
		return registry;
	}

	public V getMergedValue() {
		return getSelector().select(getRegistry().getServices().map((e) -> e.orElse(null)).filter((e) -> e != null));
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
