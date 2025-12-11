package run.soeasy.framework.core.exchange;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;

/**
 * 操作阶段状态机
 */
public class Stage implements Confirm {
	/**
	 * 状态枚举
	 */
	public enum State {
		/** 初始状态 */
		NEW,
		/** 成功状态 */
		SUCCESS,
		/** 失败状态 */
		FAILURE,
		/** 取消状态 */
		CANCELLED,
		/** 回滚状态 */
		ROLLBACKED
	}

	private final AtomicReference<State> state = new AtomicReference<>(State.NEW);
	private volatile Throwable cause;
	private volatile CountDownLatch latch = new CountDownLatch(1);
	private final BooleanSupplier rollbackLogic;

	/**
	 * 构造无回滚逻辑的状态机
	 */
	public Stage() {
		this(null);
	}

	/**
	 * 构造带回滚逻辑的状态机
	 * 
	 * @param rollbackLogic 回滚执行逻辑，返回true表示回滚成功
	 */
	public Stage(BooleanSupplier rollbackLogic) {
		this.rollbackLogic = rollbackLogic;
	}

	/**
	 * 状态变更成功回调方法
	 * 
	 * @param oldState 变更前状态
	 * @param newState 变更后状态
	 * @param cause    失败原因（仅FAILURE状态非null）
	 */
	protected void onStateChangeSuccess(State oldState, State newState, Throwable cause) {
		// 空实现，子类按需重写
	}

	// ========== SUCCESS ==========
	/**
	 * 尝试从指定旧状态转换为成功状态
	 * 
	 * @param expectOldState 期望的旧状态
	 * @return true=转换成功，false=转换失败
	 */
	public boolean trySuccess(State expectOldState) {
		if (state.compareAndSet(expectOldState, State.SUCCESS)) {
			complete();
			onStateChangeSuccess(expectOldState, State.SUCCESS, null);
			return true;
		}
		return false;
	}

	/**
	 * 尝试从初始状态转换为成功状态
	 * 
	 * @return true=转换成功，false=转换失败
	 */
	public boolean trySuccess() {
		return trySuccess(State.NEW);
	}

	// ========== FAILURE ==========
	/**
	 * 尝试从指定旧状态转换为失败状态
	 * 
	 * @param expectOldState 期望的旧状态
	 * @param ex             失败原因，不可为null
	 * @return true=转换成功，false=转换失败
	 */
	public boolean tryFailure(State expectOldState, Throwable ex) {
		Objects.requireNonNull(ex, "Failure cause must not be null");
		if (state.compareAndSet(expectOldState, State.FAILURE)) {
			this.cause = ex;
			complete();
			onStateChangeSuccess(expectOldState, State.FAILURE, ex);
			return true;
		}
		return false;
	}

	/**
	 * 尝试从初始状态转换为失败状态
	 * 
	 * @param ex 失败原因，不可为null
	 * @return true=转换成功，false=转换失败
	 */
	public boolean tryFailure(Throwable ex) {
		return tryFailure(State.NEW, ex);
	}

	// ========== CANCELLED ==========
	/**
	 * 尝试从指定旧状态转换为取消状态
	 * 
	 * @param expectOldState 期望的旧状态
	 * @return true=转换成功，false=转换失败
	 */
	public boolean tryCancel(State expectOldState) {
		if (state.compareAndSet(expectOldState, State.CANCELLED)) {
			complete();
			onStateChangeSuccess(expectOldState, State.CANCELLED, null);
			return true;
		}
		return false;
	}

	/**
	 * 尝试从初始状态转换为取消状态
	 * 
	 * @return true=转换成功，false=转换失败
	 */
	public boolean tryCancel() {
		return tryCancel(State.NEW);
	}

	// ========== ROLLBACKED ==========
	/**
	 * 尝试从指定旧状态转换为回滚状态
	 * 
	 * @param expectOldState 期望的旧状态
	 * @return true=回滚成功，false=回滚失败
	 */
	public boolean tryRollback(State expectOldState) {
		if (rollbackLogic == null) {
			return false;
		}
		if (state.compareAndSet(expectOldState, State.ROLLBACKED)) {
			try {
				boolean rollbackOk = rollbackLogic.getAsBoolean();
				if (!rollbackOk) {
					if (state.compareAndSet(State.ROLLBACKED, expectOldState)) {
						onStateChangeSuccess(State.ROLLBACKED, expectOldState, null);
					}
					return false;
				}
				complete();
				onStateChangeSuccess(expectOldState, State.ROLLBACKED, null);
				return true;
			} catch (Throwable e) {
				if (state.compareAndSet(State.ROLLBACKED, expectOldState)) {
					onStateChangeSuccess(State.ROLLBACKED, expectOldState, null);
				}
				throw e;
			}
		}
		return false;
	}

	/**
	 * 尝试从成功状态转换为回滚状态
	 * 
	 * @return true=回滚成功，false=回滚失败
	 */
	public boolean tryRollback() {
		return tryRollback(State.SUCCESS);
	}

	// ========== 通用方法 ==========
	/**
	 * 完成状态转换，唤醒等待线程
	 */
	private void complete() {
		latch.countDown();
	}

	/**
	 * 重置状态机到指定状态
	 * 
	 * @param resetToState 目标重置状态
	 */
	public void reset(State resetToState) {
		latch.countDown();
		state.set(resetToState);
		this.cause = null;
		this.latch = new CountDownLatch(1);
	}

	/**
	 * 重置状态机到初始状态
	 */
	public void reset() {
		reset(State.NEW);
	}

	// ========== Confirm 接口实现 ==========
	@Override
	public boolean cancel() {
		return tryCancel();
	}

	@Override
	public boolean rollback() {
		return tryRollback();
	}

	@Override
	public boolean isCancellable() {
		return state.get() == State.NEW;
	}

	@Override
	public boolean isCancelled() {
		return state.get() == State.CANCELLED;
	}

	@Override
	public boolean isRollbackSupported() {
		return rollbackLogic != null;
	}

	@Override
	public boolean isRollback() {
		return state.get() == State.ROLLBACKED;
	}

	@Override
	public boolean isDone() {
		return state.get() != State.NEW;
	}

	@Override
	public boolean isSuccess() {
		return state.get() == State.SUCCESS;
	}

	@Override
	public Throwable cause() {
		return state.get() == State.FAILURE ? cause : null;
	}

	@Override
	public void await() throws InterruptedException {
		latch.await();
	}

	@Override
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		return latch.await(timeout, unit);
	}

	/**
	 * 获取当前状态
	 * 
	 * @return 状态枚举值
	 */
	public State getCurrentState() {
		return state.get();
	}

	@Override
	public boolean isFailure() {
		return state.get() == State.FAILURE;
	}
}