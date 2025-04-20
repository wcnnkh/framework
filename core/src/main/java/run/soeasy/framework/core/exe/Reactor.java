package run.soeasy.framework.core.exe;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface Reactor<S, T, E extends Throwable> extends Function<S, T, E> {
	@RequiredArgsConstructor
	public static class MappedReactor<S, T, E extends Throwable, V> implements Reactor<S, V, E> {
		@NonNull
		protected final Reactor<S, T, E> source;
		@NonNull
		protected final Function<? super T, ? extends V, ? extends E> pipeline;
		protected final Consumer<? super V, ? extends E> endpoint;

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

	public static class ReactorPipeline<S, T, E extends Throwable, P extends Reactor<? super S, T, ? extends E>>
			extends FunctionPipeline<S, T, E, Supplier<? extends S, ? extends E>, P> {

		public ReactorPipeline(@NonNull Supplier<? extends S, ? extends E> source, @NonNull P pipeline,
				Runnable<? extends E> processor) {
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
	default <R> Reactor<S, R, E> andThen(@NonNull Function<? super T, ? extends R, ? extends E> pipeline) {
		return new MappedReactor<>(this, pipeline, null);
	}

	default Pipeline<T, E> newPipeline(Supplier<? extends S, ? extends E> source) {
		return new ReactorPipeline<>(source, this, null);
	}
}
