package io.basc.framework.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface Reactor<S, T, E extends Throwable> extends Pipeline<S, T, E> {
	@RequiredArgsConstructor
	public static class MappedReactor<S, T, E extends Throwable, V> implements Reactor<S, V, E> {
		@NonNull
		protected final Reactor<S, T, E> source;
		@NonNull
		protected final Pipeline<? super T, ? extends V, ? extends E> pipeline;
		protected final Endpoint<? super V, ? extends E> endpoint;

		@Override
		public V apply(S source) throws E {
			T target = this.source.apply(source);
			try {
				return pipeline.apply(target);
			} finally {
				this.source.close(target);
			}
		}

		@Override
		public void close(V target) throws E {
			if (endpoint != null) {
				endpoint.accept(target);
			}
		}
	}

	@RequiredArgsConstructor
	public static class ReactorChannel<S, T, E extends Throwable> implements Channel<T, E> {
		@NonNull
		protected final Reactor<S, T, E> reactor;
		@NonNull
		protected final Source<? extends S, ? extends E> source;

		@Override
		public T get() throws E {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void close() throws E {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean isClosed() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	@Override
	default <R> Reactor<S, R, E> map(@NonNull Pipeline<? super T, ? extends R, ? extends E> pipeline) {
		return new MappedReactor<>(this, pipeline, null);
	}

	void close(T target) throws E;

	default Channel<T, E> newChannel(Source<? extends S, ? extends E> source) {
		return new ReactorChannel<>(this, source);
	}
}
