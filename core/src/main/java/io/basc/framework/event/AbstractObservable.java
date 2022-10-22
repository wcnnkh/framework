package io.basc.framework.event;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.basc.framework.util.AbstractOptional;
import io.basc.framework.util.Registration;

public abstract class AbstractObservable<V> extends AbstractOptional<V> implements Observable<V> {

	static class IfAbsentObservable<T> extends AbstractObservable<T> {
		private final Observable<T> source;
		private final Supplier<? extends T> other;

		public IfAbsentObservable(Observable<T> source, Supplier<? extends T> other) {
			this.source = source;
			this.other = other;
		}

		@Override
		public Registration registerListener(EventListener<ObservableChangeEvent<T>> eventListener) {
			return this.source.registerListener(eventListener);
		}
		
		@Override
		protected T getValue() {
			return source.orElse(other.get());
		}
	}

	static class FilterObservable<T> extends AbstractObservable<T> {
		private final Observable<T> source;
		private final Predicate<? super T> predicate;

		FilterObservable(Observable<T> source, Predicate<? super T> predicate) {
			this.source = source;
			this.predicate = predicate;
		}

		@Override
		public Registration registerListener(EventListener<ObservableChangeEvent<T>> eventListener) {
			return this.source.registerListener(eventListener);
		}

		@Override
		protected T getValue() {
			T value = source.orElse(null);
			if (value == null) {
				return null;
			}
			return predicate.test(value) ? value : null;
		}
	}

	static class ConvertObservable<S, T> extends AbstractObservable<T> {
		private final Observable<S> source;
		private final Function<? super S, ? extends T> mapper;

		ConvertObservable(Observable<S> source, Function<? super S, ? extends T> mapper) {
			this.source = source;
			this.mapper = mapper;
		}

		@Override
		public Registration registerListener(EventListener<ObservableChangeEvent<T>> eventListener) {
			return source.registerListener((e) -> eventListener.onEvent(new ObservableChangeEvent<>(e, mapper)));
		}

		@Override
		protected T getValue() {
			S value = source.orElse(null);
			return mapper.apply(value);
		}

	}
}
