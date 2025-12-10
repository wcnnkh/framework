package run.soeasy.framework.core.exchange;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

import lombok.NonNull;

/**
 * 基于Stage状态机的Promise实现，整合异步结果获取和监听能力
 * 
 * @param <V> 异步结果类型
 * @author soeasy.run
 */
public class ListenableStage<V> extends Stage implements Promise<V> {
	/** 异步成功结果（仅SUCCESS状态有效，volatile保证多线程可见性） */
	private volatile V result;
	/** 不可取消标记（原子布尔保证线程安全） */
	private final AtomicBoolean uncancellable = new AtomicBoolean(false);
	/** 事件分发器：处理监听器注册和状态变更事件发布 */
	private final DisposableDispatcher<ListenableFuture<V>> dispatcher = new DisposableDispatcher<>();

	// ========== 构造方法 ==========
	/**
	 * 构造无回滚逻辑的ListenableStage
	 */
	public ListenableStage() {
		super(null);
	}

	/**
	 * 构造带回滚逻辑的ListenableStage
	 * 
	 * @param rollbackLogic 回滚执行逻辑，返回true表示回滚成功
	 */
	public ListenableStage(BooleanSupplier rollbackLogic) {
		super(rollbackLogic);
	}

	// ========== 核心改造：重写状态变更成功钩子，统一发布事件 ==========
	/**
	 * 状态变更成功回调，统一发布事件到监听器
	 * 
	 * @param oldState 变更前状态
	 * @param newState 变更后状态
	 * @param cause    失败原因（仅FAILURE状态非null）
	 */
	@Override
	protected void onStateChangeSuccess(State oldState, State newState, Throwable cause) {
		// 所有状态变更成功后，发布事件通知监听器
		dispatcher.publish(this);
	}

	// ========== ListenableFuture<V> 核心方法实现 ==========
	/**
	 * 获取当前已完成的结果（非阻塞）
	 * 
	 * @return 成功态返回结果，其他状态返回null
	 */
	@Override
	public V getNow() {
		return isSuccess() ? result : null;
	}

	/**
	 * 注册监听器
	 * 
	 * @param listener 监听器实例（不可为null）
	 * @return 操作结果（成功/失败）
	 */
	@Override
	public Operation registerListener(@NonNull Listener<ListenableFuture<V>> listener) {
		if (isDone()) {
			// 第一步：发布事件触发dispatcher中所有残留监听器
			dispatcher.publish(this);
			// 第二步：触发当前注册的监听器（避免遗漏）
			listener.accept(this);
			return Operation.success();
		}
		// 未完成状态：注册监听器，等待状态变更触发
		return dispatcher.registerListener(listener);
	}

	// ========== Future<V> 接口实现 ==========
	/**
	 * 取消异步操作
	 * 
	 * @param mayInterruptIfRunning 是否中断运行中的任务（当前实现忽略该参数）
	 * @return true=取消成功，false=不可取消/已完成/取消失败
	 */
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		if (uncancellable.get()) {
			return false;
		}
		// 复用Stage的tryCancel，事件发布由钩子方法统一处理
		return tryCancel();
	}

	/**
	 * 阻塞获取结果（无超时）
	 * 
	 * @return 成功态的结果
	 * @throws InterruptedException 线程中断异常
	 * @throws ExecutionException   操作失败异常（包装失败原因）
	 */
	@Override
	public V get() throws InterruptedException, ExecutionException {
		await();
		if (isCancelled()) {
			throw new java.util.concurrent.CancellationException("ListenableStage cancelled");
		}
		if (isFailure()) {
			throw new ExecutionException(cause());
		}
		return result;
	}

	/**
	 * 阻塞获取结果（带超时）
	 * 
	 * @param timeout 超时时间
	 * @param unit    时间单位
	 * @return 成功态的结果
	 * @throws InterruptedException 线程中断异常
	 * @throws ExecutionException   操作失败异常（包装失败原因）
	 * @throws TimeoutException     超时异常
	 */
	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		boolean awaitSuccess = await(timeout, unit);
		if (!awaitSuccess) {
			throw new TimeoutException("ListenableStage await timeout: " + timeout + " " + unit);
		}
		if (isCancelled()) {
			throw new java.util.concurrent.CancellationException("ListenableStage cancelled");
		}
		if (isFailure()) {
			throw new ExecutionException(cause());
		}
		return result;
	}

	// ========== Promise<V> 接口核心强制实现方法 ==========
	/**
	 * 标记为不可取消状态
	 * 
	 * @return true=标记成功/已完成且未取消，false=已取消
	 */
	@Override
	public boolean setUncancellable() {
		if (isCancelled()) {
			return false;
		}
		uncancellable.set(true);
		return true;
	}

	/**
	 * 尝试标记为成功状态（带泛型结果）
	 * 
	 * @param result 成功结果（可为null）
	 * @return true=标记成功，false=不可取消/已完成
	 */
	@Override
	public boolean trySuccess(V result) {
		if (uncancellable.get()) {
			return false;
		}
		// 核心修复：先赋值result（volatile保证可见性），再执行状态变更
		this.result = result;
		// 复用Stage的trySuccess，事件发布由钩子方法统一处理
		return super.trySuccess();
	}

	/**
	 * 尝试标记为失败状态
	 * 
	 * @param cause 失败原因（不可为null）
	 * @return true=标记成功，false=不可取消/已完成
	 */
	@Override
	public boolean tryFailure(@NonNull Throwable cause) {
		if (uncancellable.get()) {
			return false;
		}
		// 复用Stage的tryFailure，事件发布由钩子方法统一处理
		return super.tryFailure(cause);
	}
}