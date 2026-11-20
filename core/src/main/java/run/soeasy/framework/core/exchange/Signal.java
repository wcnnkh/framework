package run.soeasy.framework.core.exchange;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.exchange.Signals.CompositePattern;
import run.soeasy.framework.core.exchange.Signals.CompositeSignals;
import run.soeasy.framework.core.streaming.Streamable;

public interface Signal {
	public static abstract class And extends MatchCompositePattern {

		@Override
		public <E extends Signal> boolean cancel(@NonNull Streamable<E> elements, boolean mayInterruptIfRunning) {
			// 取消所有的，只对结果进行判断
			return elements.mapPure((e) -> e.cancel(mayInterruptIfRunning)).toList().stream()
					.allMatch(Boolean.TRUE::equals);
		}

		@Override
		public <E extends Signal> boolean match(Streamable<E> elements, Predicate<? super E> predicate) {
			return elements.allMatch(predicate);
		}
	}

	public static abstract class MatchCompositePattern implements CompositePattern {
		@Override
		public <E extends Signal> boolean isCancelled(@NonNull Streamable<E> elements) {
			return match(elements, Signal::isCancelled);
		}

		@Override
		public <E extends Signal> boolean isDone(@NonNull Streamable<E> elements) {
			return match(elements, Signal::isDone);
		}

		@Override
		public <E extends Signal> boolean isSuccess(@NonNull Streamable<E> elements) {
			return match(elements, Signal::isSuccess);
		}

		@Override
		public <E extends Signal> boolean isRollbackSupported(@NonNull Streamable<E> elements) {
			return match(elements, Signal::isRollbackSupported);
		}

		public abstract <E extends Signal> boolean match(Streamable<E> elements, Predicate<? super E> predicate);
	}

	public static abstract class Or extends MatchCompositePattern {
		@Override
		public <E extends Signal> boolean cancel(@NonNull Streamable<E> elements, boolean mayInterruptIfRunning) {
			// 取消所有的，只对结果进行判断
			return elements.mapPure((e) -> e.cancel(mayInterruptIfRunning)).toList().stream()
					.anyMatch(Boolean.TRUE::equals);
		}

		@Override
		public <E extends Signal> boolean match(Streamable<E> elements, Predicate<? super E> predicate) {
			return elements.anyMatch(predicate);
		}
	}

	@RequiredArgsConstructor
	@Getter
	public static class ParallelAnd extends And {
		/** 执行器，为 null 时使用 ForkJoinPool.commonPool */
		private final Executor executor;

		@Override
		public <E extends Signal> void await(@NonNull Streamable<E> elements)
				throws InterruptedException, ExecutionException {
			List<? extends Signal> list = elements.toList();
			if (isDone(Streamable.of(list))) {
				return;
			}

			@SuppressWarnings("rawtypes")
			CompletableFuture[] completableFutures = list.stream().map((stage) -> {
				Runnable runnable = () -> {
					try {
						stage.await();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					} catch (ExecutionException e) {
						throw new SignalException(e, stage);
					}
				};
				return executor == null ? CompletableFuture.runAsync(runnable)
						: CompletableFuture.runAsync(runnable, executor);
			}).toArray(CompletableFuture[]::new);
			CompletableFuture<?> completableFuture = CompletableFuture.allOf(completableFutures);
			completableFuture.get();
		}

		@Override
		public <E extends Signal> void await(@NonNull Streamable<E> elements, long timeout, @NonNull TimeUnit unit)
				throws InterruptedException, ExecutionException, TimeoutException {
			List<? extends Signal> list = elements.toList();
			if (isDone(Streamable.of(list))) {
				return;
			}

			@SuppressWarnings("rawtypes")
			CompletableFuture[] completableFutures = list.stream().map((stage) -> {
				Runnable runnable = () -> {
					try {
						stage.await();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					} catch (ExecutionException e) {
						throw new SignalException(e, stage);
					}
				};
				return executor == null ? CompletableFuture.runAsync(runnable)
						: CompletableFuture.runAsync(runnable, executor);
			}).toArray(CompletableFuture[]::new);
			CompletableFuture<?> completableFuture = CompletableFuture.allOf(completableFutures);
			completableFuture.get(timeout, unit);
		}
	}

	@RequiredArgsConstructor
	@Getter
	public static class ParallelOr extends Or {
		private static final ParallelOr INSTANCE = new ParallelOr(null);
		/** 执行器，为 null 时使用 ForkJoinPool.commonPool */
		private final Executor executor;

		@Override
		public <E extends Signal> void await(@NonNull Streamable<E> elements)
				throws InterruptedException, ExecutionException {
			List<? extends Signal> list = elements.toList();
			if (isDone(Streamable.of(list))) {
				return;
			}

			@SuppressWarnings("rawtypes")
			CompletableFuture[] completableFutures = list.stream().map((stage) -> {
				Runnable runnable = () -> {
					try {
						stage.await();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					} catch (ExecutionException e) {
						throw new SignalException(e, stage);
					}
				};
				return executor == null ? CompletableFuture.runAsync(runnable)
						: CompletableFuture.runAsync(runnable, executor);
			}).toArray(CompletableFuture[]::new);
			CompletableFuture<?> completableFuture = CompletableFuture.anyOf(completableFutures);
			completableFuture.get();
		}

		@Override
		public <E extends Signal> void await(@NonNull Streamable<E> elements, long timeout, @NonNull TimeUnit unit)
				throws InterruptedException, ExecutionException, TimeoutException {
			List<? extends Signal> list = elements.toList();
			if (isDone(Streamable.of(list))) {
				return;
			}

			@SuppressWarnings("rawtypes")
			CompletableFuture[] completableFutures = list.stream().map((stage) -> {
				Runnable runnable = () -> {
					try {
						stage.await();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					} catch (ExecutionException e) {
						throw new SignalException(e, stage);
					}
				};
				return executor == null ? CompletableFuture.runAsync(runnable)
						: CompletableFuture.runAsync(runnable, executor);
			}).toArray(CompletableFuture[]::new);
			CompletableFuture<?> completableFuture = CompletableFuture.anyOf(completableFutures);
			completableFuture.get(timeout, unit);
		}
	}

	public static class SequentialAnd extends And {
		private static final SequentialAnd INSTANCE = new SequentialAnd();

		@Override
		public <E extends Signal> void await(@NonNull Streamable<E> elements)
				throws InterruptedException, ExecutionException {
			try (Stream<? extends Signal> stream = elements.stream()) {
				Iterator<? extends Signal> iterator = stream.iterator();
				while (iterator.hasNext()) {
					Signal element = iterator.next();
					if (element.isDone()) {
						continue;
					}
					element.await();
				}
			}
		}

		@Override
		public <E extends Signal> void await(@NonNull Streamable<E> elements, long timeout, @NonNull TimeUnit unit)
				throws InterruptedException, ExecutionException, TimeoutException {
			long deadline = System.nanoTime() + unit.toNanos(timeout);
			try (Stream<? extends Signal> stream = elements.stream()) {
				Iterator<? extends Signal> iterator = stream.iterator();
				while (iterator.hasNext()) {
					Signal element = iterator.next();
					if (element.isDone()) {
						continue;
					}

					long t = deadline - System.nanoTime();
					if (t < 0) {
						throw new SignalTimeoutException(element);
					}

					element.await(t, TimeUnit.NANOSECONDS);
				}
			}
		}
	}

	static <E extends Signal> Signals<E> batch(@NonNull Streamable<E> elements) {
		return new CompositeSignals<>(SequentialAnd.INSTANCE, Streamable.of(elements.toList()));
	}

	public static <V> Promise<V> promise() {
		return new Promise<>();
	}

	/**
	 * 通用成功
	 * 
	 * @param <V>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <V> Promise<V> success() {
		return (Promise<V>) Promise.SUCCESS;
	}

	/**
	 * 通用错误
	 * 
	 * @param <V>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <V> Promise<V> failure() {
		return (Promise<V>) Promise.FAILURE;
	}

	public static <V> Task<V> run(@NonNull Runnable runnable) {
		Task<V> task = new Task<V>(runnable);
		task.run();
		return task;
	}

	public static <V> Task<V> run(@NonNull Runnable runnable, @NonNull Executor executor) {
		Task<V> task = new Task<V>(runnable);
		executor.execute(task);
		return task;
	}

	public static <V> Task<V> call(@NonNull Callable<V> callable) {
		Task<V> task = new Task<V>(callable);
		task.run();
		return task;
	}

	public static <V> Task<V> call(@NonNull Callable<V> callable, @NonNull Executor executor) {
		Task<V> task = new Task<V>(callable);
		executor.execute(task);
		return task;
	}

	/**
	 * 串行 And 组合：先等待当前信号，再依次等待 {@code others}。
	 *
	 * <p>
	 * 返回的组合信号同样实现 {@link Signal}，可继续参与外层编排；若外层仍是串行 And，会自动拍平为一层。
	 *
	 * @param others 其他信号
	 * @return 串行 And 组合信号
	 */
	default Signal and(@NonNull Signal... others) {
		return composite(SequentialAnd.INSTANCE, others);
	}

	void await() throws InterruptedException, ExecutionException;

	void await(long timeout, @NonNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;

	default Throwable cause() {
		if (isDone()) {
			try {
				await();
			} catch (Throwable e) {
				return SignalException.unwrap(e);
			}
		}
		return null;
	}

	default boolean isSuccess() {
		try {
			await();
			return true;
		} catch (Throwable e) {
		}
		return false;
	}

	default Signal boxed() {
		return this;
	}

	boolean cancel(boolean mayInterruptIfRunning);

	/**
	 * 使用指定组合模式，将当前信号与 {@code others} 合并为组合信号。
	 *
	 * @param compositePattern 组合规则 And / Or 及其具体实现
	 * @param others           其他信号
	 * @return 组合信号
	 */
	default Signal composite(@NonNull CompositePattern compositePattern, @NonNull Signal... others) {
		return new CompositeSignals<>(compositePattern, Streamable.array(this).concat(others));
	}

	boolean isCancelled();

	boolean isDone();

	/**
	 * 并行 Or 组合：当前信号与 {@code others} 并行等待，任一完成即整体完成。
	 *
	 * <p>
	 * 返回的组合信号同样实现 {@link Signal}，可继续参与外层编排；若外层仍是并行 Or，会自动拍平为一层。
	 *
	 * @param others 其他信号
	 * @return 并行 Or 组合信号
	 */
	default Signal or(@NonNull Signal... others) {
		return composite(ParallelOr.INSTANCE, others);
	}

	boolean isRollbackSupported();

	Signal rollback();

	default Signals<? extends Signal> unboxed() {
		return new CompositeSignals<>(SequentialAnd.INSTANCE, Streamable.singleton(this));
	}
}
