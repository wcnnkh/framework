package io.basc.framework.io.event;

import io.basc.framework.convert.Converter;
import io.basc.framework.event.ConvertibleObservables;
import io.basc.framework.io.Resource;
import io.basc.framework.util.PropertiesCombiner;

import java.util.Properties;

public class ConvertibleObservableProperties<T> extends ConvertibleObservables<Properties, T> {

	public ConvertibleObservableProperties(Converter<Properties, T> converter) {
		super(converter, PropertiesCombiner.DEFAULT);
	}

	public void combine(Resource resource,
			Converter<Resource, Properties> converter) {
		combine(new ObservableResource<Properties>(resource, converter));
	}
}
