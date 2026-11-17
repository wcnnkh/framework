package run.soeasy.framework.core.exchange2;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.Getter;
import lombok.NonNull;

/**
 * 带返回值的信号节点：继承 {@link Signal}，同时实现 {@link Future<V>}，承载任务结果、异常信息
 *
 * <p>
 * 定位：
 * <ul>
 * <li>Signal：无返回值、面向编排状态原语；Stage：Signal 的带值扩展，承载单次异步任务结果（one-shot）</li>
 * <li>和 Signal 保持完全一致的终态契约：成功 / 失败 / 取消都属于 done；失败、取消时 isSuccess = false</li>
 * <li>复用 Future 标准获取结果能力，同时提供 {@link #await()} 这套不抛业务异常、只抛出中断的编排接口</li>
 * </ul>
 *
 * <p>
 * 重要契约：
 * <ul>
 * <li>{@link #get()}/{@link #get(long, TimeUnit)}：标准 Future 语义，失败会包装为
 * {@link ExecutionException}</li>
 * <li>{@link #await()}/{@link #await(long, TimeUnit)}：编排专用，吃掉业务异常，只对外抛出
 * {@link InterruptedException}； await 返回值仅代表等待是否在时限内等到完成，不区分成功还是失败</li>
 * <li>{@link #getCause()}：用于读取失败根因；成功、取消时返回 null；回滚产生的异常，去 rollback 生成的 Stage
 * 上读取</li>
 * <li>isSuccess 默认规则：已完成、未取消，并且 get() 正常返回不抛出异常，判定为成功</li>
 * <li>{@link #getNow()}：非阻塞快照取值，仅成功状态尝试读取结果，失败/未完成/取消直接返回null，不等待、不抛异常</li>
 * </ul>
 *
 * <p>
 * 与组合编排的关系： Stage 本身是 Signal，可以直接放进 {@link Signals}、SequentialAnd / ParallelAnd
 * / ParallelOr 参与组合； 组合器只聚合状态，不感知内部 result、cause。
 * </p>
 *
 * @param <V> 任务结果类型
 */
public interface Stage<V> extends Signal, Future<V> {
	@Getter
	public static class DoneStage<E> extends DoneSignal implements Stage<E> {
		/** 成功、无返回值、无异常的固定完成节点 */
		private static final Stage<Object> TRUE = new DoneStage<Object>(true, null, null);
		/** 失败、无返回值、无异常的固定完成节点 */
		private static final Stage<Object> FALSE = new DoneStage<Object>(false, null, null);

		/** 任务结果，失败时为 null */
		private final E result;
		/** 失败根因，成功时为 null */
		private final Throwable cause;

		public DoneStage(boolean success, E result, Throwable cause) {
			super(success);
			this.result = result;
			this.cause = cause;
		}

		@Override
		public E get() throws InterruptedException, ExecutionException {
			return result;
		}

		@Override
		public E get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
			return result;
		}
	}

	/**
	 * 构造失败终态Stage，无异常对象
	 *
	 * @param <T> 结果泛型
	 * @return 已失败完成的Stage
	 */
	@SuppressWarnings("unchecked")
	public static <T> Stage<T> failure() {
		return (Stage<T>) DoneStage.FALSE;
	}

	/**
	 * 构造失败终态Stage，携带异常根因
	 *
	 * @param cause 失败异常；传null等价于 {@link #failure()}
	 * @param <E>   结果泛型
	 * @return 已失败完成的Stage
	 */
	public static <E> Stage<E> failure(Throwable cause) {
		if (cause == null) {
			return failure();
		}
		return new DoneStage<>(false, null, cause);
	}

	public static <E> Stage<E> future(@NonNull Future<? extends E> future) {
		return new FutureStage<E>(future);
	}

	/**
	 * 构造成功终态Stage，结果为null，无异常
	 *
	 * @param <T> 结果泛型
	 * @return 已成功完成的Stage
	 */
	@SuppressWarnings("unchecked")
	public static <T> Stage<T> success() {
		return (Stage<T>) DoneStage.TRUE;
	}

	/**
	 * 构造成功终态Stage，携带返回结果，无异常
	 *
	 * @param result 业务结果
	 * @param <T>    结果泛型
	 * @return 已成功完成的Stage
	 */
	public static <T> Stage<T> success(T result) {
		if (result == null) {
			return success();
		}
		return new DoneStage<T>(true, result, null);
	}

	/**
	 * 编排阻塞等待：等待至进入终态，业务异常内部吞掉，仅抛出中断
	 *
	 * @throws InterruptedException 等待线程被中断
	 */
	@Override
	default void await() throws InterruptedException {
		if (isDone()) {
			return;
		}

		try {
			get();
		} catch (Exception e) {
			// 业务异常、执行异常全部静默，交由状态方法 getCause / isSuccess 读取
		}
	}

	/**
	 * 编排带超时等待：等待至进入终态或者等待超时
	 * <p>
	 * 返回false = 等待侧超时；返回true = 已经到达终态（不管是成功、失败、取消） 业务异常内部吞掉，仅抛出中断
	 * </p>
	 *
	 * @param timeout 超时时长
	 * @param unit    时间单位
	 * @return false：等待超时；true：在时限内到达终态
	 * @throws InterruptedException 等待线程被中断
	 */
	@Override
	default boolean await(long timeout, @NonNull TimeUnit unit) throws InterruptedException {
		if (isDone()) {
			return true;
		}

		try {
			get(timeout, unit);
		} catch (TimeoutException e) {
			return false;
		} catch (Exception e) {
			// 业务异常、执行异常静默
		}
		return true;
	}

	/**
	 * 获取任务失败根因
	 *
	 * @return 失败异常；成功、取消状态返回null； 回滚动作产生的异常，需要从 rollback() 返回的 Stage 对象上读取
	 */
	default Throwable getCause() {
		if (isSuccess()) {
			return null;
		}

		if (isDone()) {
			try {
				get();
			} catch (ExecutionException e) {
				return e.getCause();
			} catch (Throwable e) {
				// 捕获其他异常，不向外抛出
			}
		}
		return null;
	}

	/**
	 * 非阻塞立刻取值，不等待
	 * <p>
	 * 仅在 isSuccess() 为true时尝试读取结果；未完成、失败、已取消直接返回null 不会抛出任何受检异常
	 * </p>
	 * 
	 * @return 成功时的结果；其他情况返回null
	 */
	default V getNow() {
		if (isSuccess()) {
			try {
				return get();
			} catch (InterruptedException | ExecutionException e) {
				// 理论上isSuccess成立时不会走到这里，防御性捕获
			}
		}
		return null;
	}

	/**
	 * 默认成功判定：已完成、未取消，并且 get() 正常返回无异常
	 *
	 * @return true 代表任务正常成功；false：未完成 / 已取消 / 执行异常
	 */
	@Override
	default boolean isSuccess() {
		if (isDone() && !isCancelled()) {
			try {
				get();
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}
}
