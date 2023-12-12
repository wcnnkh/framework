package io.basc.framework.observe.properties;

import io.basc.framework.util.element.Elements;
import io.basc.framework.value.Value;

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
