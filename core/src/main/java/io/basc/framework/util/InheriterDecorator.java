package io.basc.framework.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class InheriterDecorator<A, B> implements Inheriter<A, B> {

	private final class InheritableProcessor<S, T, E extends Throwable> implements Processor<S, T, E> {
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

	private final class InheritableSource<T, E extends Throwable> implements Source<T, E> {
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
		ConsumeProcessor<T, RuntimeException> consumeProcessor = decorateConsumeProcessor(consumer::accept);
		return (s) -> consumeProcessor.process(s);
	}

	public final <T, R> Function<T, R> decorateFunction(Function<? super T, ? extends R> function) {
		Assert.requiredArgument(function != null, "function");
		Processor<T, R, RuntimeException> processor = decorateProcessor(function::apply);
		return (s) -> processor.process(s);
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
		Source<T, RuntimeException> source = decorateSource(supplier::get);
		return () -> source.get();
	}

	public final Executor decorateExecutor(Executor executor) {
		Assert.requiredArgument(executor != null, "executor");
		return new InhertableExecutor<>(executor);
	}

	public final ExecutorService decorate(ExecutorService executorService) {
		Assert.requiredArgument(executorService != null, "executorService");
		return new InheritableExecutorService<>(executorService);
	}

	public final ScheduledExecutorService decorate(ScheduledExecutorService scheduledExecutorService) {
		Assert.requiredArgument(scheduledExecutorService != null, "scheduledExecutorService");
		return new InheritableScheduledExecutorService<>(scheduledExecutorService);
	}

	private class InhertableExecutor<W extends Executor> extends Wrapper<W> implements Executor {

		public InhertableExecutor(W wrappedTarget) {
			super(wrappedTarget);
		}

		@Override
		public void execute(Runnable command) {
			wrappedTarget.execute(decorateRunnable(command));
		}
	}

	private class InheritableExecutorService<W extends ExecutorService> extends InhertableExecutor<W>
			implements ExecutorService {

		public InheritableExecutorService(W wrappedTarget) {
			super(wrappedTarget);
		}

		@Override
		public void shutdown() {
			wrappedTarget.shutdown();
		}

		@Override
		public List<Runnable> shutdownNow() {
			return wrappedTarget.shutdownNow();
		}

		@Override
		public boolean isShutdown() {
			return wrappedTarget.isShutdown();
		}

		@Override
		public boolean isTerminated() {
			return wrappedTarget.isTerminated();
		}

		@Override
		public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
			return wrappedTarget.awaitTermination(timeout, unit);
		}

		@Override
		public <T> Future<T> submit(Callable<T> task) {
			return wrappedTarget.submit(decorateCallable(task));
		}

		@Override
		public <T> Future<T> submit(Runnable task, T result) {
			return wrappedTarget.submit(decorateRunnable(task), result);
		}

		@Override
		public Future<?> submit(Runnable task) {
			return wrappedTarget.submit(decorateRunnable(task));
		}

		@Override
		public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
			return wrappedTarget.invokeAll(CollectionUtils.isEmpty(tasks) ? Collections.emptyList()
					: tasks.stream().map((e) -> decorateCallable(e)).collect(Collectors.toList()));
		}

		@Override
		public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
				throws InterruptedException {
			return wrappedTarget.invokeAll(
					CollectionUtils.isEmpty(tasks) ? Collections.emptyList()
							: tasks.stream().map((e) -> decorateCallable(e)).collect(Collectors.toList()),
					timeout, unit);
		}

		@Override
		public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
				throws InterruptedException, ExecutionException {
			return wrappedTarget.invokeAny(CollectionUtils.isEmpty(tasks) ? Collections.emptyList()
					: tasks.stream().map((e) -> decorateCallable(e)).collect(Collectors.toList()));
		}

		@Override
		public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
				throws InterruptedException, ExecutionException, TimeoutException {
			return wrappedTarget.invokeAny(
					CollectionUtils.isEmpty(tasks) ? Collections.emptyList()
							: tasks.stream().map((e) -> decorateCallable(e)).collect(Collectors.toList()),
					timeout, unit);
		}
	}

	private class InheritableScheduledExecutorService<W extends ScheduledExecutorService>
			extends InheritableExecutorService<W> implements ScheduledExecutorService {

		public InheritableScheduledExecutorService(W wrappedTarget) {
			super(wrappedTarget);
		}

		@Override
		public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
			return wrappedTarget.schedule(decorateRunnable(command), delay, unit);
		}

		@Override
		public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
			return wrappedTarget.schedule(decorateCallable(callable), delay, unit);
		}

		@Override
		public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
			return wrappedTarget.scheduleAtFixedRate(decorateRunnable(command), initialDelay, period, unit);
		}

		@Override
		public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay,
				TimeUnit unit) {
			return scheduleWithFixedDelay(decorateRunnable(command), initialDelay, delay, unit);
		}
	}
}
