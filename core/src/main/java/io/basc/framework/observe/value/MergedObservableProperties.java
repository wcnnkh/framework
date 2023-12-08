package io.basc.framework.observe.value;

import java.util.Properties;

import io.basc.framework.util.select.PropertiesCombiner;
import io.basc.framework.util.select.Selector;

public class MergedObservableProperties extends MergedObservableMap<Properties, Object, Object> {
	public MergedObservableProperties() {
		// 默认使用合并策略
		setSelector(PropertiesCombiner.INSTANCE);
	}

	@Override
	public void setSelector(Selector<Properties> selector) {
		super.setSelector(selector == null ? PropertiesCombiner.INSTANCE : selector);
	}
}
