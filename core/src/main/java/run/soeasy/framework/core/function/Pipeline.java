package run.soeasy.framework.core.function;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface Pipeline<S, T, E extends Throwable> extends ThrowingFunction<S, T, E> {
	public static interface PipelineWrapper<S, T, E extends Throwable, W extends Pipeline<S, T, E>>
			extends Pipeline<S, T, E>, ThrowingFunctionWrapper<S, T, E, W> {

		@Override
		default <R> Pipeline<S, R, E> andThen(
				@NonNull ThrowingFunction<? super T, ? extends R, ? extends E> pipeline) {
			return getSource().andThen(pipeline);
		}

		@Override
		default void close(T target) throws E {
			getSource().close(target);
		}

		@Override
		default Source<T, E> newSupplier(ThrowingSupplier<? extends S, ? extends E> supplier) {
			return getSource().newSupplier(supplier);
		}
	}

	@RequiredArgsConstructor
	public static class MappedPipeline<S, T, E extends Throwable, V>
			implements Pipeline<S, V, E> {
		@NonNull
		protected final Pipeline<S, T, E> source;
		@NonNull
		protected final ThrowingFunction<? super T, ? extends V, ? extends E> pipeline;
		protected final ThrowingConsumer<? super V, ? extends E> endpoint;

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

	public static class PipelineToSource<S, T, E extends Throwable, P extends Pipeline<? super S, T, ? extends E>>
			extends
			ThrowingFunctionToSource<S, T, E, ThrowingSupplier<? extends S, ? extends E>, P> {

		public PipelineToSource(
				@NonNull ThrowingSupplier<? extends S, ? extends E> source, @NonNull P pipeline,
				ThrowingRunnable<? extends E> processor) {
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
	default <R> Pipeline<S, R, E> andThen(
			@NonNull ThrowingFunction<? super T, ? extends R, ? extends E> pipeline) {
		return new MappedPipeline<>(this, pipeline, null);
	}

	default Source<T, E> newSupplier(ThrowingSupplier<? extends S, ? extends E> supplier) {
		return new PipelineToSource<>(supplier, this, null);
	}
}
