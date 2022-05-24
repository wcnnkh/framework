package io.basc.framework.event;

import java.util.List;

import io.basc.framework.convert.IdentityConverter;
import io.basc.framework.util.stream.Processor;

public class Observables<T> extends ConvertibleObservables<T, T> {

	public Observables(Processor<List<T>, T, RuntimeException> combiner) {
		super(new IdentityConverter<>(), combiner);
	}
}
