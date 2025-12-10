package run.soeasy.framework.core.exchange;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.NonNull;

/**
 * 可设置结果的可监听未来结果实现，实现{@link Promise}接口。
 * 该类允许手动设置异步操作的结果或异常，并支持取消操作和不可取消标记。
 * <p>
 * 核心特性：
 * <ul>
 *   <li>手动设置结果：通过{@link #trySuccess(Object)}或{@link #tryFailure(Throwable)}设置结果</li>
 *   <li>取消控制：支持取消操作并可标记为不可取消</li>
 *   <li>事件监听：继承{@link ListenableFuture}的事件监听机制</li>
 *   <li>线程安全：通过内部任务实现状态同步</li>
 * </ul>
 * 
 * @param <T> 异步操作的结果类型
 * 
 * @author soeasy.run
 * @see Promise
 * @see ListenableFuture
 */
public class SettableListenableFuture<T> implements Promise<T> {

    /**
     * 内部任务类，继承自{@link ListenableFutureTask}，
     * 用于管理任务状态和完成通知。
     * <p>
     * 关键特性：
     * <ul>
     *   <li>完成线程检查：确保结果设置的线程安全性</li>
     *   <li>结果设置方法：提供线程安全的结果和异常设置</li>
     *   <li>完成回调：重写done()方法处理状态更新</li>
     * </ul>
     */
    private static class SettableTask<T> extends ListenableFutureTask<T> {

        private volatile Thread completingThread;

        @SuppressWarnings("unchecked")
        public SettableTask() {
            // 使用哑Callable初始化，实际不会被调用
            super((Callable<T>) DUMMY_CALLABLE);
        }

        /**
         * 检查当前线程是否为设置结果的线程
         * <p>
         * 用于确保结果设置的有效性，仅首次设置有效
         * 
         * @return 如果当前线程是完成线程返回true，否则false
         */
        private boolean checkCompletingThread() {
            boolean check = (this.completingThread == Thread.currentThread());
            if (check) {
                this.completingThread = null; // 仅第一个匹配有效
            }
            return check;
        }

        /**
         * 任务完成时的回调方法
         * <p>
         * 重写以存储完成线程，用于结果设置验证
         */
        @Override
        protected void done() {
            if (!isCancelled()) {
                // 存储当前线程用于确定结果是否实际触发了完成
                this.completingThread = Thread.currentThread();
            }
            super.done();
        }

        /**
         * 设置异常结果
         * 
         * @param exception 异常原因
         * @return 如果设置成功返回true，否则false
         */
        public boolean setExceptionResult(Throwable exception) {
            setException(exception);
            return checkCompletingThread();
        }

        /**
         * 设置成功结果
         * 
         * @param value 成功结果
         * @return 如果设置成功返回true，否则false
         */
        public boolean setResultValue(T value) {
            set(value);
            return checkCompletingThread();
        }
    }

    private static final Callable<Object> DUMMY_CALLABLE = new Callable<Object>() {
        public Object call() throws Exception {
            throw new IllegalStateException("Should never be called");
        }
    };
    
    /** 内部任务实例，管理任务状态和完成通知 */
    private final SettableTask<T> settableTask = new SettableTask<T>();
    
    /** 不可取消标记 */
    private boolean uncancellable = false;

    /**
     * 尝试取消异步操作
     * <p>
     * 如果已标记为不可取消，抛出IllegalStateException
     * 
     * @param mayInterruptIfRunning 是否中断运行中的任务
     * @return 如果取消成功返回true，否则false
     * @throws IllegalStateException 如果已标记为不可取消
     */
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (uncancellable) {
            throw new IllegalStateException("uncancellable");
        }

        boolean cancelled = this.settableTask.cancel(mayInterruptIfRunning);
        if (cancelled && mayInterruptIfRunning) {
            interruptTask();
        }
        return cancelled;
    }

    /**
     * 获取操作失败的原因
     * 
     * @return 失败原因，若操作成功或未失败返回null
     */
    @Override
    public Throwable cause() {
        return settableTask.cause();
    }

    /**
     * 阻塞获取操作结果
     * <p>
     * 返回通过{@link #trySuccess(Object)}设置的结果，
     * 若通过{@link #tryFailure(Throwable)}设置了异常则抛出ExecutionException，
     * 若操作被取消则抛出CancellationException
     * 
     * @return 操作结果
     * @throws InterruptedException 如果当前线程被中断
     * @throws ExecutionException 如果操作执行过程中发生异常
     */
    public T get() throws InterruptedException, ExecutionException {
        return this.settableTask.get();
    }

    /**
     * 带超时控制的阻塞获取操作结果
     * 
     * @param timeout 最大等待时间
     * @param unit 时间单位
     * @return 操作结果
     * @throws InterruptedException 如果当前线程被中断
     * @throws ExecutionException 如果操作执行过程中发生异常
     * @throws TimeoutException 如果等待超时
     */
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.settableTask.get(timeout, unit);
    }

    /**
     * 非阻塞获取当前结果（若可用）
     * 
     * @return 已完成的结果，若未完成返回null
     */
    @Override
    public T getNow() {
        return settableTask.getNow();
    }

    /**
     * 中断任务执行
     * <p>
     * 子类可重写此方法实现任务中断逻辑，
     * 默认为空实现
     */
    protected void interruptTask() {
    }

    /**
     * 检查操作是否可取消
     * 
     * @return 如果操作可取消返回true，否则false
     */
    @Override
    public boolean isCancellable() {
        return settableTask.isCancellable();
    }

    /**
     * 检查操作是否已取消
     * 
     * @return 如果已取消返回true，否则false
     */
    public boolean isCancelled() {
        return this.settableTask.isCancelled();
    }

    /**
     * 检查操作是否已完成
     * 
     * @return 如果已完成返回true，否则false
     */
    public boolean isDone() {
        return this.settableTask.isDone();
    }

    /**
     * 检查操作是否成功完成
     * 
     * @return 如果成功完成返回true，否则false
     */
    @Override
    public boolean isSuccess() {
        return settableTask.isSuccess();
    }

    /**
     * 注册操作完成状态监听器
     * 
     * @param listener 状态监听器
     * @return 注册回执，用于取消监听
     */
    @Override
    public Operation registerListener(Listener<ListenableFuture<T>> listener) {
        return settableTask.registerListener(listener);
    }

    /**
     * 将此承诺标记为不可取消
     * 
     * @return 如果成功标记为不可取消返回true，否则false（如已完成）
     */
    @Override
    public boolean setUncancellable() {
        if (isDone()) {
            return false;
        }
        uncancellable = true;
        return true;
    }

    /**
     * 尝试将此承诺标记为失败状态
     * 
     * @param cause 失败原因，不可为null
     * @return 如果成功标记为失败返回true，否则false（如已完成）
     */
    @Override
    public boolean tryFailure(@NonNull Throwable cause) {
        return this.settableTask.setExceptionResult(cause);
    }

    /**
     * 尝试将此承诺标记为成功状态
     * 
     * @param result 成功结果，可为null
     * @return 如果成功标记为成功返回true，否则false（如已完成）
     */
    @Override
    public boolean trySuccess(T result) {
        return this.settableTask.setResultValue(result);
    }
}