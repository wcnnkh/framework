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

	public static class ReactorChannel<S, T, E extends Throwable, P extends Reactor<? super S, T, ? extends E>>
			extends PipelineChannel<S, T, E, Source<? extends S, ? extends E>, P> {

		public ReactorChannel(@NonNull Source<? extends S, ? extends E> source, @NonNull P pipeline,
				Processor<? extends E> processor) {
			super(source, pipeline, processor);
		}

		@Override
		public void close() throws E {
			synchronized (this) {
				if (!isClosed()) {
					try {
						super.close();
					} finally {
						pipeline.close(get());
					}
				}
			}
		}
	}

	void close(T target) throws E;

	@Override
	default <R> Reactor<S, R, E> map(@NonNull Pipeline<? super T, ? extends R, ? extends E> pipeline) {
		return new MappedReactor<>(this, pipeline, null);
	}

	default Channel<T, E> newChannel(Source<? extends S, ? extends E> source) {
		return new ReactorChannel<>(source, this, null);
	}
}
