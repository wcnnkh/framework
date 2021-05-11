package scw.io.event;

import java.util.Properties;

import scw.convert.Converter;
import scw.event.ConvertibleObservables;
import scw.io.Resource;
import scw.util.PropertiesCombiner;

public class ConvertibleObservableProperties<T> extends ConvertibleObservables<Properties, T> {

	public ConvertibleObservableProperties(Converter<Properties, T> converter) {
		super(converter, PropertiesCombiner.DEFAULT);
	}

	public void combine(Resource resource,
			Converter<Resource, Properties> converter) {
		combine(new ObservableResource<Properties>(resource, converter));
	}
}
