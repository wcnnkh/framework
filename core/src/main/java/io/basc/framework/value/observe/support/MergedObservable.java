package io.basc.framework.value.observe.support;

import io.basc.framework.observe.register.Registry;
import io.basc.framework.util.select.Selector;
import io.basc.framework.value.observe.Observable;

/**
 * 合并值
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public class MergedObservable<T> extends DefaultObservable<T> {
	private final Registry<? extends Observable<? extends T>> registry;
	/**
	 * 合并时的选择器
	 */
	private Selector<T> selector = Selector.first();

	public MergedObservable(Registry<? extends Observable<? extends T>> registry) {
		this.registry = registry;
		reload();
		registry.registerListener((e) -> reload());
	}

	public T getSelected() {
		return getSelector().apply(getRegistry().getServices().map((e) -> e.orElse(null)).filter((e) -> e != null));
	}

	public void reload() {
		set(getSelected());
	}

	public Selector<T> getSelector() {
		return selector;
	}

	public void setSelector(Selector<T> selector) {
		this.selector = selector;
		reload();
	}

	public Registry<? extends Observable<? extends T>> getRegistry() {
		return registry;
	}
}
