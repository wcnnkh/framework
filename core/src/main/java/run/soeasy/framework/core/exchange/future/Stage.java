package run.soeasy.framework.core.exchange.future;

import run.soeasy.framework.core.exchange.Listener;
import run.soeasy.framework.core.exchange.Receipt;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.event.DisposableDispatcher;

/**
 * 操作阶段状态管理器，实现可监听的注册操作和回执功能。
 * 该类负责管理异步操作的状态转换（新建、成功、失败、取消），
 * 并提供事件分发机制以通知监听器状态变化。
 *
 * <p>状态转换模型：
 * <ul>
 *   <li>初始状态：NEW</li>
 *   <li>成功状态：调用success(Object)后转换为SUCCESS</li>
 *   <li>失败状态：调用failure(Throwable)后转换为FAILURE</li>
 *   <li>取消状态：调用cancel()后转换为CANCELLED</li>
 *   <li>状态不可逆：一旦离开NEW状态，无法回退</li>
 * </ul>
 *
 * <p>线程安全特性：
 * <ul>
 *   <li>大部分状态访问通过synchronized块保护</li>
 *   <li>事件分发使用DisposableDispatcher保证线程安全</li>
 * </ul>
 *
 * @param <T> 操作结果的类型
 * @author soeasy.run
 * @see ListenableRegistration
 * @see Receipt
 */
public class Stage<T> implements ListenableRegistration<Stage<T>>, Receipt {
    /** 操作状态枚举，定义操作的四种状态 */
    private enum State {
        /** 新建状态，操作尚未开始或完成 */
        NEW,
        /** 成功状态，操作已成功完成 */
        SUCCESS,
        /** 失败状态，操作因异常终止 */
        FAILURE,
        /** 取消状态，操作被主动取消 */
        CANCELLED
    }

    /** 操作失败的原因，仅在FAILURE状态有效 */
    private Throwable cause;

    /** 事件分发器，用于通知监听器状态变化 */
    private final DisposableDispatcher<Stage<T>> exchange = new DisposableDispatcher<>();

    /** 操作成功的结果，仅在SUCCESS状态有效 */
    private T result = null;

    /** 当前操作状态，初始为NEW */
    private State state = State.NEW;

    /**
     * 尝试取消操作
     * <p>
     * 仅在状态为NEW时可取消，取消后状态变为CANCELLED并通知所有监听器
     * 
     * @return 若取消成功返回true，否则返回false
     */
    @Override
    public synchronized boolean cancel() {
        if (state != State.NEW) {
            return false;
        }
        state = State.CANCELLED;
        exchange.publish(this);
        return true;
    }

    /**
     * 获取操作失败的原因
     * 
     * @return 失败原因，若操作成功或未失败返回null
     */
    @Override
    public synchronized Throwable cause() {
        return cause;
    }

    /**
     * 标记操作失败并设置失败原因
     * <p>
     * 仅在状态为NEW时可调用，调用后状态变为FAILURE并通知所有监听器
     * <p>
     * <b>注意：</b>未校验ex参数是否为null，可能导致NullPointerException
     * 
     * @param ex 失败原因
     */
    public synchronized void failure(Throwable ex) {
        this.state = State.FAILURE;
        this.cause = ex;
        exchange.publish(this);
    }

    /**
     * 获取操作结果
     * 
     * @return 操作结果，若操作未成功或失败返回null
     */
    public synchronized T getResult() {
        return result;
    }

    /**
     * 检查操作是否可取消
     * <p>
     * <b>注意：</b>当前逻辑为!isDone()，但根据状态模型应检查是否为NEW状态
     * 
     * @return 若操作未完成返回true，否则返回false
     */
    @Override
    public synchronized boolean isCancellable() {
        return !isDone();
    }

    /**
     * 检查操作是否已取消
     * 
     * @return 若状态为CANCELLED返回true，否则返回false
     */
    @Override
    public synchronized boolean isCancelled() {
        return state == State.CANCELLED;
    }

    /**
     * 检查操作是否已完成
     * 
     * @return 若状态非NEW返回true，否则返回false
     */
    @Override
    public synchronized boolean isDone() {
        return state != State.NEW;
    }

    /**
     * 检查操作是否成功完成
     * 
     * @return 若状态为SUCCESS返回true，否则返回false
     */
    @Override
    public synchronized boolean isSuccess() {
        return state == State.SUCCESS;
    }

    /**
     * 注册状态变化监听器
     * <p>
     * 若操作已完成，立即触发监听器；否则注册监听器等待状态变化
     * <p>
     * <b>注意：</b>已完成时返回Receipt.SUCCESS，不符合Registration契约
     * 
     * @param listener 状态变化监听器
     * @return 注册回执，用于取消监听
     * @throws NullPointerException 若listener为null
     */
    @Override
    public synchronized Registration registerListener(Listener<Stage<T>> listener) {
        if (isDone()) {
            // 已完成时直接触发事件
            exchange.publish(this);
            listener.accept(this);
            return Receipt.SUCCESS;
        }
        return exchange.registerListener(listener);
    }

    /**
     * 重置操作状态为初始状态
     * <p>
     * 此操作将：
     * <ul>
     *   <li>清空结果和失败原因</li>
     *   <li>重置状态为NEW</li>
     *   <li>清除所有注册的监听器</li>
     * </ul>
     * <b>警告：</b>重置后之前的监听器将失效，使用时需谨慎
     */
    public synchronized void reset() {
        result = null;
        cause = null;
        state = State.NEW;
        exchange.clear();
    }

    /**
     * 标记操作成功并设置结果
     * <p>
     * 仅在状态为NEW时可调用，调用后状态变为SUCCESS并通知所有监听器
     * <p>
     * <b>注意：</b>未校验状态是否为NEW，可能导致状态混乱
     * 
     * @param result 操作结果，可为null
     */
    public synchronized void success(T result) {
        this.state = State.SUCCESS;
        this.result = result;
        exchange.publish(this);
    }
}