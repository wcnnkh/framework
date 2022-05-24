package io.basc.framework.event;

import java.util.List;
import java.util.function.Function;

public class Observables<T> extends ConvertibleObservables<T, T> {

	public Observables(Function<List<T>, ? extends T> combiner) {
		super(Function.identity(), combiner);
	}
}
