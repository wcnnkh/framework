package io.basc.framework.util.transmittable;

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

import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Endpoint;
import io.basc.framework.util.Pipeline;
import io.basc.framework.util.Source;
import io.basc.framework.util.Wrapper;
import lombok.NonNull;

public abstract class InheriterDecorator<A, B> implements Inheriter<A, B> {
	/**
	 * 是否可以嵌套
	 */
	private boolean nestedExecutor = true;

	public boolean isNestedExecutor() {
		return nestedExecutor;
	}

	public void setNestedExecutor(boolean nestedExecutor) {
		this.nestedExecutor = nestedExecutor;
	}

	private final class InheritableProcessor<S, T, E extends Throwable> implements Pipeline<S, T, E> {
		private A capture = capture();
		private final Pipeline<? super S, ? extends T, ? extends E> processor;

		InheritableProcessor(Pipeline<? super S, ? extends T, ? extends E> processor) {
			this.processor = processor;
		}

		@Override
		public T apply(S source) throws E {
			B backup = replay(capture);
			try {
				return processor.apply(source);
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

	public final <S, E extends Throwable> Endpoint<S, E> decorateConsumeProcessor(
			@NonNull Endpoint<? super S, ? extends E> consumeProcessor) {
		Pipeline<S, ?, E> processor = decorateProcessor((s) -> {
			consumeProcessor.accept(s);
			return null;
		});
		return (s) -> processor.apply(s);
	}

	public final <T> Consumer<T> decorateConsumer(@NonNull Consumer<? super T> consumer) {
		Endpoint<T, RuntimeException> consumeProcessor = decorateConsumeProcessor(consumer::accept);
		return (s) -> consumeProcessor.accept(s);
	}

	public final <T, R> Function<T, R> decorateFunction(Function<? super T, ? extends R> function) {
		Assert.requiredArgument(function != null, "function");
		Pipeline<T, R, RuntimeException> processor = decorateProcessor(function::apply);
		return (s) -> processor.apply(s);
	}

	public <S, T, E extends Throwable> Pipeline<S, T, E> decorateProcessor(
			Pipeline<? super S, ? extends T, ? extends E> processor) {
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
		if (executor instanceof InhertableExecutor) {
			InhertableExecutor<?, ?, ?> inhertableExecutor = (InhertableExecutor<?, ?, ?>) executor;
			if (isNestedExecutor() ? (this == inhertableExecutor.inheriterDecorator)
					: inhertableExecutor.isNested(this)) {
				return executor;
			}
		}
		return new InhertableExecutor<>(this, executor);
	}

	public final ExecutorService decorateExecutorService(ExecutorService executorService) {
		Assert.requiredArgument(executorService != null, "executorService");
		if (executorService instanceof InhertableExecutor) {
			InhertableExecutor<?, ?, ?> inhertableExecutor = (InhertableExecutor<?, ?, ?>) executorService;
			if (isNestedExecutor() ? (this == inhertableExecutor.inheriterDecorator)
					: inhertableExecutor.isNested(this)) {
				return executorService;
			}
		}
		return new InheritableExecutorService<>(this, executorService);
	}

	public final ScheduledExecutorService decorateScheduledExecutorService(
			ScheduledExecutorService scheduledExecutorService) {
		Assert.requiredArgument(scheduledExecutorService != null, "scheduledExecutorService");
		if (scheduledExecutorService instanceof InhertableExecutor) {
			InhertableExecutor<?, ?, ?> inhertableExecutor = (InhertableExecutor<?, ?, ?>) scheduledExecutorService;
			if (isNestedExecutor() ? (this == inhertableExecutor.inheriterDecorator)
					: inhertableExecutor.isNested(this)) {
				return scheduledExecutorService;
			}
		}
		return new InheritableScheduledExecutorService<>(this, scheduledExecutorService);
	}

	private static class InhertableExecutor<X, Y, W extends Executor> implements Executor, Wrapper<W> {
		private final W source;
		protected final InheriterDecorator<X, Y> inheriterDecorator;

		public InhertableExecutor(InheriterDecorator<X, Y> inheriterDecorator, W source) {
			this.inheriterDecorator = inheriterDecorator;
			this.source = source;
		}

		@Override
		public W getSource() {
			return source;
		}

		public boolean isNested(InheriterDecorator<?, ?> inheriterDecorator) {
			if (this.inheriterDecorator == inheriterDecorator) {
				return true;
			}

			if (this.source instanceof InhertableExecutor) {
				return ((InhertableExecutor<?, ?, ?>) this.source).isNested(inheriterDecorator);
			}
			return false;
		}

		@Override
		public void execute(Runnable command) {
			source.execute(inheriterDecorator.decorateRunnable(command));
		}
	}

	private static class InheritableExecutorService<X, Y, W extends ExecutorService> extends InhertableExecutor<X, Y, W>
			implements ExecutorService {

		public InheritableExecutorService(InheriterDecorator<X, Y> inheriterDecorator, W source) {
			super(inheriterDecorator, source);
		}

		@Override
		public void shutdown() {
			getSource().shutdown();
		}

		@Override
		public List<Runnable> shutdownNow() {
			return getSource().shutdownNow();
		}

		@Override
		public boolean isShutdown() {
			return getSource().isShutdown();
		}

		@Override
		public boolean isTerminated() {
			return getSource().isTerminated();
		}

		@Override
		public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
			return getSource().awaitTermination(timeout, unit);
		}

		@Override
		public <T> Future<T> submit(Callable<T> task) {
			return getSource().submit(inheriterDecorator.decorateCallable(task));
		}

		@Override
		public <T> Future<T> submit(Runnable task, T result) {
			return getSource().submit(inheriterDecorator.decorateRunnable(task), result);
		}

		@Override
		public Future<?> submit(Runnable task) {
			return getSource().submit(inheriterDecorator.decorateRunnable(task));
		}

		@Override
		public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
			return getSource().invokeAll(CollectionUtils.isEmpty(tasks) ? Collections.emptyList()
					: tasks.stream().map((e) -> inheriterDecorator.decorateCallable(e)).collect(Collectors.toList()));
		}

		@Override
		public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
				throws InterruptedException {
			return getSource().invokeAll(CollectionUtils.isEmpty(tasks) ? Collections.emptyList()
					: tasks.stream().map((e) -> inheriterDecorator.decorateCallable(e)).collect(Collectors.toList()),
					timeout, unit);
		}

		@Override
		public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
				throws InterruptedException, ExecutionException {
			return getSource().invokeAny(CollectionUtils.isEmpty(tasks) ? Collections.emptyList()
					: tasks.stream().map((e) -> inheriterDecorator.decorateCallable(e)).collect(Collectors.toList()));
		}

		@Override
		public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
				throws InterruptedException, ExecutionException, TimeoutException {
			return getSource().invokeAny(CollectionUtils.isEmpty(tasks) ? Collections.emptyList()
					: tasks.stream().map((e) -> inheriterDecorator.decorateCallable(e)).collect(Collectors.toList()),
					timeout, unit);
		}
	}

	private static class InheritableScheduledExecutorService<X, Y, W extends ScheduledExecutorService>
			extends InheritableExecutorService<X, Y, W> implements ScheduledExecutorService {

		public InheritableScheduledExecutorService(InheriterDecorator<X, Y> inheriterDecorator, W wrappedTarget) {
			super(inheriterDecorator, wrappedTarget);
		}

		@Override
		public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
			return getSource().schedule(inheriterDecorator.decorateRunnable(command), delay, unit);
		}

		@Override
		public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
			return getSource().schedule(inheriterDecorator.decorateCallable(callable), delay, unit);
		}

		@Override
		public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
			return getSource().scheduleAtFixedRate(inheriterDecorator.decorateRunnable(command), initialDelay, period,
					unit);
		}

		@Override
		public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay,
				TimeUnit unit) {
			return scheduleWithFixedDelay(inheriterDecorator.decorateRunnable(command), initialDelay, delay, unit);
		}
	}
}
