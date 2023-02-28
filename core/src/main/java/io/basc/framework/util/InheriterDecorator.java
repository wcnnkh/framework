package io.basc.framework.util;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class InheriterDecorator<A, B> implements Inheriter<A, B> {

	private class InheritableProcessor<S, T, E extends Throwable> implements Processor<S, T, E> {
		private A capture = capture();
		private final Processor<? super S, ? extends T, ? extends E> processor;

		InheritableProcessor(Processor<? super S, ? extends T, ? extends E> processor) {
			this.processor = processor;
		}

		@Override
		public T process(S source) throws E {
			B backup = replay(capture);
			try {
				return processor.process(source);
			} finally {
				restore(backup);
			}
		}
	}

	private class InheritableSource<T, E extends Throwable> implements Source<T, E> {
		private final A capture = capture();
		private final Source<? extends T, ? extends E> source;

		InheritableSource(Source<? extends T, ? extends E> source) {
			this.source = source;
		}

		@Override
		public T get() throws E {
			B backup = replay(capture);
			try {
				return source.get();
			} finally {
				restore(backup);
			}
		}
	}

	public final <V> Callable<V> decorateCallable(Callable<? extends V> callable) {
		Assert.requiredArgument(callable != null, "callable");
		return decorateSource(callable::call)::get;
	}

	public final <S, E extends Throwable> ConsumeProcessor<S, E> decorateConsumeProcessor(
			ConsumeProcessor<? super S, ? extends E> consumeProcessor) {
		Assert.requiredArgument(consumeProcessor != null, "consumeProcessor");
		Processor<S, ?, E> processor = decorateProcessor((s) -> {
			consumeProcessor.process(s);
			return null;
		});
		return (s) -> processor.process(s);
	}

	public final <T> Consumer<T> decorateConsumer(Consumer<? super T> consumer) {
		Assert.requiredArgument(consumer != null, "consumer");
		return decorateConsumeProcessor(consumer::accept)::process;
	}

	public final <T, R> Function<T, R> decorateFunction(Function<? super T, ? extends R> function) {
		Assert.requiredArgument(function != null, "function");
		return decorateProcessor(function::apply)::process;
	}

	public <S, T, E extends Throwable> Processor<S, T, E> decorateProcessor(
			Processor<? super S, ? extends T, ? extends E> processor) {
		Assert.requiredArgument(processor != null, "processor");
		return new InheritableProcessor<>(processor);
	}

	public final Runnable decorateRunnable(Runnable runnable) {
		Assert.requiredArgument(runnable != null, "runnable");
		Source<?, RuntimeException> source = decorateSource(() -> {
			runnable.run();
			return null;
		});
		return () -> source.get();
	}

	public <T, E extends Throwable> Source<T, E> decorateSource(Source<? extends T, ? extends E> source) {
		Assert.requiredArgument(source != null, "source");
		return new InheritableSource<>(source);
	}

	public final <T> Supplier<T> decorateSupplier(Supplier<? extends T> supplier) {
		Assert.requiredArgument(supplier != null, "supplier");
		return decorateSource(supplier::get)::get;
	}
}
