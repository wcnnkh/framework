package run.soeasy.framework.core.exchange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 批量操作实现类
 * <p>
 * 核心语义： - AND 模式：所有子操作满足条件（完成/成功等），批量操作才满足； - OR 模式：任意子操作满足条件，批量操作即满足。
 * <p>
 * 边界规则： - 空操作列表：视为“完成/成功”（AND 模式 allMatch 空列表返回 true，OR 模式 anyMatch 空列表返回
 * false）； - 超时时间=0：立即检查状态，不阻塞； - 子操作为 null：构造函数直接抛异常。
 */
public interface Operations<O extends Operation> extends Operation, Streamable<O> {
	/**
	 * 批量操作组合模式
	 */
	public static enum Mode {
		/** 所有子操作满足，批量才满足（空列表返回 true） */
		AND,
		/** 任意子操作满足，批量即满足（空列表返回 false） */
		OR;

		public <E extends Operation> boolean match(@NonNull Stream<? extends E> operations,
				@NonNull Predicate<? super E> predicate) {
			return this == Mode.AND ? operations.allMatch(predicate) : operations.anyMatch(predicate);
		}
	}

	Mode getMode();

	default boolean match(@NonNull Predicate<? super O> predicate) {
		return getMode().match(stream(), predicate);
	}

	@Override
	default boolean cancel() {
		return getMode().match(stream().filter((e) -> e.isCancellable() && !isCancelled()), Operation::cancel);
	}

	@Override
	default boolean rollback() {
		return getMode().match(stream().filter((e) -> e.isRollbackSupported() && !isRollback()), Operation::rollback);
	}

	@Override
	default boolean isCancellable() {
		return stream().anyMatch(Operation::isCancellable);
	}

	@Override
	default boolean isRollbackSupported() {
		return stream().anyMatch(Operation::isRollbackSupported);
	}

	@Override
	default boolean isCancelled() {
		return match(Operation::isCancelled);
	}

	@Override
	default boolean isRollback() {
		return match(Operation::isRollback);
	}

	@Override
	default boolean isDone() {
		return match(Operation::isDone);
	}

	@Override
	default boolean isSuccess() {
		return match(Operation::isSuccess);
	}

	@Override
	default Throwable cause() {
		List<Throwable> causes = filter(op -> !op.isSuccess()).map(Operation::cause).filter(Objects::nonNull)
				.collect(Collectors.toList());

		if (causes.isEmpty()) {
			return null;
		}
		if (causes.size() == 1) {
			return causes.get(0);
		}

		// 复合异常：仅用String构造，避免InterruptedException同理问题
		RuntimeException compositeCause = new RuntimeException(
				String.format("Batch operation has %d failure reasons: %s", causes.size(),
						causes.stream().map(Throwable::getMessage).collect(Collectors.joining("; "))));
		// 手动添加抑制异常（兼容Java 8+）
		causes.forEach(compositeCause::addSuppressed);
		return compositeCause;
	}

	// ==================== 核心 await 实现（修复所有Bug） ====================
	@Override
	default void await() throws InterruptedException {
		// 快速返回：已满足模式条件
		if (isDone()) {
			return;
		}

		Collection<O> operations = toCollection();
		// 空列表：AND模式isDone()=true（已快速返回），OR模式isDone()=false，无需等待
		if (operations.isEmpty()) {
			return;
		}

		Mode mode = getMode();
		if (mode == Mode.AND) {
			// AND模式：串行等待所有子操作
			for (Operation op : operations) {
				op.await();
				// 实时检查中断状态，确保中断响应
				if (Thread.currentThread().isInterrupted()) {
					throw new InterruptedException(
							String.format("Batch operation [%s] AND mode await interrupted", op.toString()));
				}
			}
		} else {
			// OR模式：并行等待任意子操作（修复：先初始化所有线程再启动，避免并发修改）
			AtomicBoolean hasCompleted = new AtomicBoolean(false);
			List<Thread> waitThreads = new ArrayList<>(operations.size());

			// 第一步：初始化所有等待线程（未启动）
			int i = 0;
			for (Operation op : operations) {
				int finalI = i++;
				Thread waitThread = new Thread(() -> {
					try {
						op.await();
						// 原子标记：第一个完成的子操作中断其他线程
						if (hasCompleted.compareAndSet(false, true)) {
							// 此时waitThreads已初始化完成，无并发修改风险
							waitThreads.forEach(Thread::interrupt);
						}
					} catch (InterruptedException e) {
						// 被中断：仅标记线程状态，不处理
						Thread.currentThread().interrupt();
					}
				}, String.format("%s-OR-Waiter-%d", op.toString(), finalI));
				waitThread.setDaemon(true); // 守护线程避免进程阻塞
				waitThreads.add(waitThread);
			}

			// 第二步：启动所有线程（避免子线程提前操作waitThreads）
			waitThreads.forEach(Thread::start);

			// 第三步：等待所有线程结束（或被中断）
			Thread interrupted = null;
			for (Thread t : waitThreads) {
				try {
					t.join();
				} catch (InterruptedException e) {
					// 捕获中断：标记状态，中断所有子线程
					interrupted = t;
					waitThreads.forEach(Thread::interrupt);
					break;
				}
			}

			// 处理中断：恢复状态并抛出（修复InterruptedException构造方法）
			if (interrupted != null) {
				Thread.currentThread().interrupt();
				throw new InterruptedException(
						String.format("Batch operation [%s] OR mode await interrupted", interrupted.toString()));
			}

			// 最终校验：确保有子操作完成（空列表已提前返回）
			if (!isDone()) {
				throw new InterruptedException(
						"Batch operation OR mode await interrupted before any operation completed");
			}
		}
	}

	@Override
	default boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		// 1. 参数合法性校验
		if (timeout < 0) {
			throw new IllegalArgumentException("Timeout must not be negative");
		}
		Objects.requireNonNull(unit, "TimeUnit must not be null");

		// 2. 快速返回：已满足模式条件
		if (isDone()) {
			return true;
		}

		Mode mode = getMode();
		Collection<O> operations = toCollection();
		// 3. 空列表处理：AND模式返回true，OR模式返回false
		if (operations.isEmpty()) {
			return mode == Mode.AND;
		}

		// 4. 超时时间转换（纳秒，高精度）
		long timeoutNanos = unit.toNanos(timeout);
		long startNanos = System.nanoTime();
		if (mode == Mode.AND) {
			// AND模式：串行等待，剩余时间递减
			for (Operation op : operations) {

				long elapsedNanos = System.nanoTime() - startNanos;
				long remainingNanos = timeoutNanos - elapsedNanos;

				// 剩余时间耗尽：返回false
				if (remainingNanos <= 0) {
					return false;
				}

				// 当前子操作超时未完成 → 整体超时
				if (!op.await(remainingNanos, TimeUnit.NANOSECONDS)) {
					return false;
				}

				// 检查中断状态
				if (Thread.currentThread().isInterrupted()) {
					throw new InterruptedException(
							String.format("Batch operation [%s] AND mode await interrupted", op.toString()));
				}
			}
			// 所有子操作完成
			return true;
		} else {
			// OR模式：并行等待（修复timeoutFlag语义错误）
			AtomicBoolean hasCompleted = new AtomicBoolean(false);
			// 改为：统计超时的子操作数，只有所有子操作都超时且无完成时才标记超时
			AtomicLong timeoutCount = new AtomicLong(0);
			List<Thread> waitThreads = new ArrayList<>(operations.size());

			// 第一步：初始化所有线程（未启动）
			int i = 0;
			for (Operation op : operations) {
				int finalI = i++;
				Thread waitThread = new Thread(() -> {
					try {
						long elapsedNanos = System.nanoTime() - startNanos;
						long remainingNanos = timeoutNanos - elapsedNanos;

						// 剩余时间耗尽：统计超时数
						if (remainingNanos <= 0) {
							timeoutCount.incrementAndGet();
							return;
						}

						// 子操作完成：标记并中断其他线程
						if (op.await(remainingNanos, TimeUnit.NANOSECONDS)) {
							if (hasCompleted.compareAndSet(false, true)) {
								waitThreads.forEach(Thread::interrupt);
							}
						} else {
							// 子操作超时：统计超时数
							timeoutCount.incrementAndGet();
						}
					} catch (InterruptedException e) {
						// 被中断：标记状态
						Thread.currentThread().interrupt();
					}
				}, String.format("%s-OR-TimeoutWaiter-%d", op.toString(), finalI));
				waitThread.setDaemon(true);
				waitThreads.add(waitThread);
			}

			// 第二步：启动所有线程
			waitThreads.forEach(Thread::start);

			// 第三步：等待线程结束（修复join参数负数问题）
			Thread interrupted = null;
			long remainingJoinNanos = timeoutNanos;
			for (Thread t : waitThreads) {
				// 提前退出：已有子操作完成 或 所有子操作都超时
				if (hasCompleted.get() || timeoutCount.get() >= operations.size()) {
					break;
				}

				// 校验剩余时间：≤0时跳出循环
				if (remainingJoinNanos <= 0) {
					break;
				}

				try {
					long joinStart = System.nanoTime();
					// Thread.join参数：millis（剩余时间/1e6），nanos（剩余时间%1e6）
					long millis = remainingJoinNanos / 1_000_000;
					int nanos = (int) (remainingJoinNanos % 1_000_000);
					t.join(millis, nanos);

					// 更新剩余时间（补偿join耗时）
					remainingJoinNanos -= (System.nanoTime() - joinStart);
				} catch (InterruptedException e) {
					// 捕获中断：标记状态，中断所有子线程
					interrupted = t;
					waitThreads.forEach(Thread::interrupt);
					break;
				}
			}

			// 第四步：清理线程（中断未结束的守护线程）
			waitThreads.forEach(t -> {
				if (t.isAlive()) {
					t.interrupt();
				}
			});

			// 处理中断：恢复状态并抛出
			if (interrupted != null) {
				Thread.currentThread().interrupt();
				throw new InterruptedException(
						String.format("Batch operation [%s] OR mode await interrupted", interrupted.toString()));
			}

			// 第五步：返回结果（修复语义错误）
			// OR模式：有子操作完成 → true；所有子操作超时 → false
			return hasCompleted.get();
		}
	}
}
