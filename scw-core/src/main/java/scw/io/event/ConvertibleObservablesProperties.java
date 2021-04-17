package scw.io.event;

import java.util.Properties;

import scw.event.ConvertibleObservables;
import scw.util.Combiners;

public abstract class ConvertibleObservablesProperties<T> extends ConvertibleObservables<Properties, T> {

	public ConvertibleObservablesProperties(boolean concurrent) {
		super(concurrent, Combiners.PROPERTIES);
	}
}
