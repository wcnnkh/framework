package io.basc.framework.value;

import java.util.Set;

import io.basc.framework.event.BroadcastEventDispatcher;
import io.basc.framework.event.BroadcastEventRegistry;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.support.StandardBroadcastEventDispatcher;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ElementSet;
import io.basc.framework.util.Elements;

/**
 * 频繁修改属性建议使用此类, 不会触发数据收集
 * 
 * @author wcnnkh
 *
 */
public class PropertyFactories extends ValueFactories<String, PropertyFactory> implements DynamicPropertyFactory {
	private final BroadcastEventDispatcher<ChangeEvent<Elements<String>>> keyEventDispatcher;

	public PropertyFactories() {
		this(new StandardBroadcastEventDispatcher<>());
	}

	public PropertyFactories(BroadcastEventDispatcher<ChangeEvent<Elements<String>>> keyEventDispatcher) {
		Assert.requiredArgument(keyEventDispatcher != null, "keyEventDispatcher");
		this.keyEventDispatcher = keyEventDispatcher;
		setServiceClass(PropertyFactory.class);
		getElementEventDispatcher().registerListener((e) -> {
			Set<String> registerKeys = e.getSource().flatMap((p) -> p.keys()).toSet();
			ElementSet<String> changeKeys = new ElementSet<>(registerKeys);
			keyEventDispatcher.publishEvent(new ChangeEvent<>(e, changeKeys));
		});
	}

	public BroadcastEventDispatcher<ChangeEvent<Elements<String>>> getKeyEventDispatcher() {
		return keyEventDispatcher;
	}

	@Override
	public boolean containsKey(String key) {
		for (PropertyFactory factory : this) {
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
		return Elements.of(() -> stream().flatMap((e) -> e.keys().stream()).distinct());
	}

	@Override
	public BroadcastEventRegistry<ChangeEvent<Elements<String>>> getKeyEventRegistry() {
		return keyEventDispatcher;
	}
}
