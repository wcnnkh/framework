package io.basc.framework.event;

import io.basc.framework.convert.IdentityConverter;
import io.basc.framework.util.Combiner;

public class Observables<T> extends ConvertibleObservables<T, T> {

	public Observables(Combiner<T> combiner) {
		super(new IdentityConverter<T>(), combiner);
	}
}
