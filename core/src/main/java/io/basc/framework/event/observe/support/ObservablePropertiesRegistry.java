package io.basc.framework.event.observe.support;

import java.util.Map;
import java.util.Properties;

import io.basc.framework.event.observe.Observable;
import io.basc.framework.util.registry.Registration;
import io.basc.framework.util.select.PropertiesCombiner;
import io.basc.framework.util.select.Selector;

public class ObservablePropertiesRegistry extends ObservableRegistry<Properties> {

	public ObservablePropertiesRegistry() {
		// 默认使用合并策略
		setSelector(PropertiesCombiner.INSTANCE);
	}

	public Registration registerMap(Observable<? extends Map<?, ?>> observableMap) {
		Observable<Properties> observableProperties = observableMap.map((e) -> {
			Properties properties = new Properties();
			properties.putAll(e);
			return properties;
		});
		return register(observableProperties);
	}

	@Override
	public void setSelector(Selector<Properties> selector) {
		super.setSelector(selector == null ? PropertiesCombiner.INSTANCE : selector);
	}
}
