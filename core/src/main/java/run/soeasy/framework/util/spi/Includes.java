package run.soeasy.framework.util.spi;

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

import run.soeasy.framework.util.collection.Reloadable;
import run.soeasy.framework.util.exchange.Registrations;

@FunctionalInterface
public interface Includes<S, I extends Include<S>> extends Registrations<I>, Include<S> {
	@Override
	default void reload() {
		getElements().forEach(Reloadable::reload);
	}

	@Override
	default Stream<S> stream() {
		return getElements().flatMap(Function.identity()).stream();
	}

	@Override
	default Iterator<S> iterator() {
		return getElements().flatMap(Function.identity()).iterator();
	}

	@Override
	default boolean hasElements() {
		return Registrations.super.hasElements();
	}
}
