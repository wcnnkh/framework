package io.basc.framework.io.event;

import java.util.Properties;

import io.basc.framework.event.ConvertibleObservables;
import io.basc.framework.io.Resource;
import io.basc.framework.util.PropertiesCombiner;
import io.basc.framework.util.stream.Processor;

public class ConvertibleObservableProperties<T> extends ConvertibleObservables<Properties, T> {

	public ConvertibleObservableProperties(Processor<Properties, T, ? extends RuntimeException> converter) {
		super(converter, PropertiesCombiner.DEFAULT);
	}

	public void combine(Resource resource, Processor<Resource, Properties, ? extends RuntimeException> processor) {
		combine(new ObservableResource<Properties>(resource, processor));
	}
}
