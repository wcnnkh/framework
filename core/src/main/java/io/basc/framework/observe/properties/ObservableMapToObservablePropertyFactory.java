package io.basc.framework.observe.properties;

import io.basc.framework.convert.lang.ObjectValue;
import io.basc.framework.util.Elements;

class ObservableMapToObservablePropertyFactory extends ObservableMapToObservableValueFactory<String>
		implements ObservablePropertyFactory {

	public ObservableMapToObservablePropertyFactory(ObservableMap<String, ObjectValue> observableMap) {
		super(observableMap);
	}

	@Override
	public Elements<String> keys() {
		return Elements.of(getObservableMap().keySet());
	}

}
