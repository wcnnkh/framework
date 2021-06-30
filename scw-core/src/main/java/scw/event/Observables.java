package scw.event;

import scw.convert.IdentityConverter;
import scw.util.Combiner;

public class Observables<T> extends ConvertibleObservables<T, T> {

	public Observables(Combiner<T> combiner) {
		super(new IdentityConverter<T>(), combiner);
	}
}
