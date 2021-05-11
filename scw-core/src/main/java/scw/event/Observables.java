package scw.event;

import scw.convert.EmptyConverter;
import scw.util.Combiner;

public class Observables<T> extends ConvertibleObservables<T, T> {

	public Observables(Combiner<T> combiner) {
		super(new EmptyConverter<T>(), combiner);
	}
}
