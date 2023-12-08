package io.basc.framework.value;

import java.util.Set;

import io.basc.framework.event.broadcast.BroadcastEventDispatcher;
import io.basc.framework.event.broadcast.BroadcastEventRegistry;
import io.basc.framework.event.broadcast.support.StandardBroadcastEventDispatcher;
import io.basc.framework.observe.ObservableEvent;
import io.basc.framework.util.Assert;
import io.basc.framework.util.element.ElementSet;
import io.basc.framework.util.element.Elements;

/**
 * 频繁修改属性建议使用此类, 不会触发数据收集
 * 
 * @author wcnnkh
 *
 */
public class PropertyFactories extends ValueFactories<String, PropertyFactory> implements DynamicPropertyFactory {
	private final BroadcastEventDispatcher<ObservableEvent<Elements<String>>> keyEventDispatcher;

	public PropertyFactories() {
		this(new StandardBroadcastEventDispatcher<>());
	}

	public PropertyFactories(BroadcastEventDispatcher<ObservableEvent<Elements<String>>> keyEventDispatcher) {
		Assert.requiredArgument(keyEventDispatcher != null, "keyEventDispatcher");
		this.keyEventDispatcher = keyEventDispatcher;
		setServiceClass(PropertyFactory.class);
		getElementEventDispatcher().registerListener((e) -> {
			Set<String> registerKeys = e.getSource().flatMap((p) -> p.keys()).toSet();
			ElementSet<String> changeKeys = new ElementSet<>(registerKeys);
			keyEventDispatcher.publishEvent(new ObservableEvent<>(e, changeKeys));
		});
	}

	public BroadcastEventDispatcher<ObservableEvent<Elements<String>>> getKeyEventDispatcher() {
		return keyEventDispatcher;
	}

	@Override
	public boolean containsKey(String key) {
		for (PropertyFactory factory : getServices()) {
			if (factory == null || factory == this) {
				continue;
			}

			if (factory.containsKey(key)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Elements<String> keys() {
		return getServices().flatMap((e) -> e.keys()).distinct();
	}

	@Override
	public BroadcastEventRegistry<ObservableEvent<Elements<String>>> getKeyEventRegistry() {
		return keyEventDispatcher;
	}
}
