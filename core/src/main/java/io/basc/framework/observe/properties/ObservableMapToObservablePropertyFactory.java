package io.basc.framework.observe.properties;

import io.basc.framework.core.convert.Value;
import io.basc.framework.util.Elements;

class ObservableMapToObservablePropertyFactory extends ObservableMapToObservableValueFactory<String>
		implements ObservablePropertyFactory {

	public ObservableMapToObservablePropertyFactory(ObservableMap<String, Value> observableMap) {
		super(observableMap);
	}

	@Override
	public Elements<String> keys() {
		return Elements.of(getObservableMap().keySet());
	}

}
