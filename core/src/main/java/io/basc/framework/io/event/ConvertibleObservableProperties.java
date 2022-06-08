package io.basc.framework.io.event;

import java.util.Properties;
import java.util.function.Function;

import io.basc.framework.event.ConvertibleObservables;
import io.basc.framework.io.Resource;
import io.basc.framework.util.PropertiesCombiner;

public class ConvertibleObservableProperties<T> extends ConvertibleObservables<Properties, T> {

	public ConvertibleObservableProperties(Function<Properties, T> converter) {
		super(converter, PropertiesCombiner.DEFAULT);
	}

	public void combine(Resource resource, Function<Resource, Properties> processor) {
		combine(new ObservableResource<Properties>(resource, processor));
	}
}
