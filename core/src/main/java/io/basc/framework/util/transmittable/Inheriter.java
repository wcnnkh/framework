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
import java.util.stream.Collectors;

import io.basc.framework.util.collection.CollectionUtils;
import io.basc.framework.util.function.Wrapped;
import lombok.NonNull;

/**
 * 
 * B b = replay(capture()); try{ codeing... } finally{ restore(b); }
 * 
 * @author wcnnkh
 *
 * @param <A>
 * @param <B>
 */
public interface Inheriter<A, B> {
	public static class Inheritable<A, B, I extends Inheriter<A, B>, W> extends Wrapped<W> {
		@NonNull
		protected final I inheriter;

		public Inheritable(W source, I inheriter) {
			super(source);
			this.inheriter = inheriter;
		}
	}

	public static class InheritableExecutorService<A, B, I extends Inheriter<A, B>, W extends ExecutorService>
			extends InhertableExecutor<A, B, I, W> implements ExecutorService {

		public InheritableExecutorService(W source, I inheriter) {
			super(source, inheriter);
		}

		@Override
		public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
			return getSource().awaitTermination(timeout, unit);
		}

		@Override
		public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
			return getSource().invokeAll(CollectionUtils.isEmpty(tasks) ? Collections.emptyList()
					: tasks.stream().map((e) -> inheriter.wrapCallable(e)).collect(Collectors.toList()));
		}

		@Override
		public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
				throws InterruptedException {
			return getSource().invokeAll(
					CollectionUtils.isEmpty(tasks) ? Collections.emptyList()
							: tasks.stream().map((e) -> inheriter.wrapCallable(e)).collect(Collectors.toList()),
					timeout, unit);
		}

		@Override
		public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
				throws InterruptedException, ExecutionException {
			return getSource().invokeAny(CollectionUtils.isEmpty(tasks) ? Collections.emptyList()
					: tasks.stream().map((e) -> inheriter.wrapCallable(e)).collect(Collectors.toList()));
		}

		@Override
		public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
				throws InterruptedException, ExecutionException, TimeoutException {
			return getSource().invokeAny(
					CollectionUtils.isEmpty(tasks) ? Collections.emptyList()
							: tasks.stream().map((e) -> inheriter.wrapCallable(e)).collect(Collectors.toList()),
					timeout, unit);
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
		public void shutdown() {
			getSource().shutdown();
		}

		@Override
		public List<Runnable> shutdownNow() {
			return getSource().shutdownNow();
		}

		@Override
		public <T> Future<T> submit(Callable<T> task) {
			return getSource().submit(inheriter.wrapCallable(task));
		}

		@Override
		public Future<?> submit(Runnable task) {
			return getSource().submit(inheriter.wrapRunnable(task));
		}

		@Override
		public <T> Future<T> submit(Runnable task, T result) {
			return getSource().submit(inheriter.wrapRunnable(task), result);
		}
	}

	public static class InheritableScheduledExecutorService<A, B, I extends Inheriter<A, B>, W extends ScheduledExecutorService>
			extends InheritableExecutorService<A, B, I, W> implements ScheduledExecutorService {

		public InheritableScheduledExecutorService(W source, I inheriter) {
			super(source, inheriter);
		}

		@Override
		public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
			return getSource().schedule(inheriter.wrapCallable(callable), delay, unit);
		}

		@Override
		public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
			return getSource().schedule(inheriter.wrapRunnable(command), delay, unit);
		}

		@Override
		public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
			return getSource().scheduleAtFixedRate(inheriter.wrapRunnable(command), initialDelay, period, unit);
		}

		@Override
		public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay,
				TimeUnit unit) {
			return scheduleWithFixedDelay(inheriter.wrapRunnable(command), initialDelay, delay, unit);
		}
	}

	public static class InhertableExecutor<A, B, I extends Inheriter<A, B>, W extends Executor>
			extends Inheritable<A, B, I, W> implements Executor {

		public InhertableExecutor(W source, I inheriter) {
			super(source, inheriter);
		}

		@Override
		public void execute(Runnable command) {
			source.execute(inheriter.wrapRunnable(command));
		}
	}

	public static class WrappedCallable<A, B, I extends Inheriter<A, B>, T, E extends Throwable, W extends io.basc.framework.util.function.Callable<? extends T, ? extends E>>
			extends Wrapper<A, B, I, W> implements io.basc.framework.util.function.Callable<T, E> {

		public WrappedCallable(W source, I inheriter) {
			super(source, inheriter);
		}

		public T call() throws E {
			B backup = inheriter.replay(capture);
			try {
				return this.source.call();
			} finally {
				inheriter.restore(backup);
			}
		};
	}

	public static class WrappedConsumer<A, B, I extends Inheriter<A, B>, S, E extends Throwable, W extends io.basc.framework.util.function.Consumer<? super S, ? extends E>>
			extends Wrapper<A, B, I, W> implements io.basc.framework.util.function.Consumer<S, E> {

		public WrappedConsumer(W source, I inheriter) {
			super(source, inheriter);
		}

		public void accept(S source) throws E {
			B backup = inheriter.replay(capture);
			try {
				this.source.accept(source);
			} finally {
				inheriter.restore(backup);
			}
		}
	}

	public static class WrappedFunction<A, B, I extends Inheriter<A, B>, S, T, E extends Throwable, W extends io.basc.framework.util.function.Function<? super S, ? extends T, ? extends E>>
			extends Wrapper<A, B, I, W> implements io.basc.framework.util.function.Function<S, T, E> {

		public WrappedFunction(W source, I inheriter) {
			super(source, inheriter);
		}

		@Override
		public T apply(S source) throws E {
			B backup = inheriter.replay(capture);
			try {
				return this.source.apply(source);
			} finally {
				inheriter.restore(backup);
			}
		}
	}

	public static class WrappedNativeCallable<A, B, I extends Inheriter<A, B>, T, W extends Callable<? extends T>>
			extends Wrapper<A, B, I, W> implements Callable<T> {

		public WrappedNativeCallable(W source, I inheriter) {
			super(source, inheriter);
		}

		public T call() throws Exception {
			B backup = inheriter.replay(capture);
			try {
				return this.source.call();
			} finally {
				inheriter.restore(backup);
			}
		};
	}

	public static class WrappedNativeConsumer<A, B, I extends Inheriter<A, B>, S, W extends Consumer<? super S>>
			extends Wrapper<A, B, I, W> implements Consumer<S> {

		public WrappedNativeConsumer(W source, I inheriter) {
			super(source, inheriter);
		}

		public void accept(S source) {
			B backup = inheriter.replay(capture);
			try {
				this.source.accept(source);
			} finally {
				inheriter.restore(backup);
			}
		}
	}

	public static class WrappedNativeFunction<A, B, I extends Inheriter<A, B>, S, T, W extends Function<? super S, ? extends T>>
			extends Wrapper<A, B, I, W> implements Function<S, T> {

		public WrappedNativeFunction(W source, I inheriter) {
			super(source, inheriter);
		}

		@Override
		public T apply(S source) {
			B backup = inheriter.replay(capture);
			try {
				return this.source.apply(source);
			} finally {
				inheriter.restore(backup);
			}
		}
	}

	public static class WrappedNativeRunnable<A, B, I extends Inheriter<A, B>, W extends Runnable>
			extends Wrapper<A, B, I, W> implements Runnable {

		public WrappedNativeRunnable(W source, I inheriter) {
			super(source, inheriter);
		}

		@Override
		public void run() {
			B backup = inheriter.replay(capture);
			try {
				this.source.run();
			} finally {
				inheriter.restore(backup);
			}
		}

	}

	public static class WrappedRunnable<A, B, I extends Inheriter<A, B>, E extends Throwable, W extends io.basc.framework.util.function.Runnable<? extends E>>
			extends Wrapper<A, B, I, W> implements io.basc.framework.util.function.Runnable<E> {

		public WrappedRunnable(W source, I inheriter) {
			super(source, inheriter);
		}

		@Override
		public void run() throws E {
			B backup = inheriter.replay(capture);
			try {
				this.source.run();
			} finally {
				inheriter.restore(backup);
			}
		}

	}

	public static class Wrapper<A, B, I extends Inheriter<A, B>, W> extends Inheritable<A, B, I, W> {
		protected final A capture;

		public Wrapper(W source, I inheriter) {
			super(source, inheriter);
			this.capture = inheriter.capture();
		}
	}

	A capture();

	B clear();

	B replay(A capture);

	void restore(B backup);

	default Executor wrapExecutor(Executor executor) {
		return new InhertableExecutor<>(executor, this);
	}

	default ExecutorService wrapExecutorService(ExecutorService executorService) {
		return new InheritableExecutorService<>(executorService, this);
	}

	default ScheduledExecutorService wrapScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
		return new InheritableScheduledExecutorService<>(scheduledExecutorService, this);
	}

	default <T> Callable<T> wrapCallable(@NonNull Callable<? extends T> callable) {
		return new WrappedNativeCallable<>(callable, this);
	}

	default <T, E extends Throwable> io.basc.framework.util.function.Callable<T, E> wrapCallable(
			@NonNull io.basc.framework.util.function.Callable<? extends T, ? extends E> callable) {
		return new WrappedCallable<>(callable, this);
	}

	default <S> Consumer<S> wrapConsumer(@NonNull Consumer<? super S> consumer) {
		return new WrappedNativeConsumer<>(consumer, this);
	}

	default <S, E extends Throwable> io.basc.framework.util.function.Consumer<S, E> wrapConsumer(
			@NonNull io.basc.framework.util.function.Consumer<? super S, ? extends E> consumer) {
		return new WrappedConsumer<>(consumer, this);
	}

	default <S, T> Function<S, T> wrapFunction(@NonNull Function<? super S, ? extends T> function) {
		return new WrappedNativeFunction<>(function, this);
	}

	default <S, T, E extends Throwable> io.basc.framework.util.function.Function<S, T, E> wrapFunction(
			@NonNull io.basc.framework.util.function.Function<? super S, ? extends T, ? extends E> function) {
		return new WrappedFunction<>(function, this);
	}

	default <E extends Throwable> io.basc.framework.util.function.Runnable<E> wrapRunnable(
			io.basc.framework.util.function.Runnable<? extends E> runnable) {
		return new WrappedRunnable<>(runnable, this);
	}

	default Runnable wrapRunnable(Runnable runnable) {
		return new WrappedNativeRunnable<>(runnable, this);
	}
}