package run.soeasy.framework.core.exchange.future;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import run.soeasy.framework.core.exchange.Listener;
import run.soeasy.framework.core.exchange.Registration;

/**
 * 可监听的FutureTask实现，扩展标准FutureTask以支持事件监听功能。
 * 该类继承自Java标准库的{@link FutureTask}，并实现{@link ListenableFuture}接口，
 * 允许对异步任务的完成状态进行监听，适用于需要异步任务状态通知的场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>异步任务执行：继承FutureTask的所有功能，支持Callable和Runnable任务</li>
 *   <li>事件监听：实现ListenableFuture接口，支持注册完成事件监听器</li>
 *   <li>状态管理：通过Stage组件维护任务的成功/失败/取消状态</li>
 *   <li>异常处理：自动将任务执行异常转换为Stage的失败状态</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要异步执行并监听完成状态的任务</li>
 *   <li>将标准FutureTask融入框架的事件监听体系</li>
 *   <li>需要统一处理异步任务完成事件的场景</li>
 * </ul>
 *
 * @param <T> 任务执行结果的类型
 * 
 * @author soeasy.run
 * @see FutureTask
 * @see ListenableFuture
 */
public class ListenableFutureTask<T> extends FutureTask<T> implements ListenableFuture<T> {
    private Stage<T> stage = new Stage<T>();

    /**
     * 创建一个新的ListenableFutureTask，执行给定的Callable任务。
     * <p>
     * 任务执行结果可通过{@link #get()}获取，任务完成状态可通过{@link #registerListener}监听。
     * 
     * @param callable 待执行的Callable任务
     * @throws NullPointerException 若callable为null
     */
    public ListenableFutureTask(Callable<T> callable) {
        super(callable);
    }

    /**
     * 创建一个新的ListenableFutureTask，执行给定的Runnable任务，并指定成功时的返回结果。
     * <p>
     * 任务执行完成后，{@link #get()}将返回指定的result，状态可通过监听器查询。
     * 
     * @param runnable 待执行的Runnable任务
     * @param result 任务成功时的返回结果
     * @throws NullPointerException 若runnable为null
     */
    public ListenableFutureTask(Runnable runnable, T result) {
        super(runnable, result);
    }

    /**
     * 注册任务完成状态监听器
     * <p>
     * 监听器将在任务完成（成功/失败/取消）时被触发，支持通过返回的Registration取消监听。
     * 
     * @param listener 状态监听器，接收当前ListenableFutureTask实例
     * @return 注册回执，用于取消监听
     * @throws NullPointerException 若listener为null
     */
    @Override
    public Registration registerListener(Listener<ListenableFuture<T>> listener) {
        return stage.registerListener((e) -> listener.accept(ListenableFutureTask.this));
    }

    /**
     * 检查任务是否可取消
     * <p>
     * 该方法返回Stage组件的可取消状态，即任务是否处于未完成状态。
     * 
     * @return 若任务可取消返回true，否则返回false
     */
    @Override
    public boolean isCancellable() {
        return stage.isCancellable();
    }

    /**
     * 尝试取消任务执行
     * <p>
     * 该方法会调用父类FutureTask的cancel方法，并同步更新Stage状态。
     * 
     * @param mayInterruptIfRunning 是否中断正在执行的任务
     * @return 若取消成功返回true，否则返回false
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (super.cancel(mayInterruptIfRunning)) {
            stage.cancel();
            return true;
        }
        return false;
    }

    /**
     * 获取任务失败的原因
     * <p>
     * 若任务成功完成或未失败，返回null；否则返回失败原因。
     * 
     * @return 失败原因，若无异常返回null
     */
    @Override
    public Throwable cause() {
        return stage.cause();
    }

    /**
     * 检查任务是否成功完成
     * 
     * @return 若任务成功完成返回true，否则返回false
     */
    @Override
    public boolean isSuccess() {
        return stage.isSuccess();
    }

    /**
     * 非阻塞获取任务结果（若可用）
     * <p>
     * 若任务未完成，返回null；否则返回任务结果。
     * 
     * @return 已完成的结果，若未完成返回null
     */
    @Override
    public T getNow() {
        return stage.getResult();
    }

    /**
     * 任务完成时的回调方法
     * <p>
     * 该方法在任务完成（正常结束、异常或取消）时被调用，负责更新Stage状态：
     * <ul>
     *   <li>成功时：调用stage.success(result)</li>
     *   <li>失败时：调用stage.failure(cause)</li>
     * </ul>
     */
    @Override
    protected void done() {
        Throwable cause;
        try {
            T result = get();
            this.stage.success(result);
            return;
        } catch (InterruptedException ex) {
            // 恢复中断状态并直接返回，不视为失败
            Thread.currentThread().interrupt();
            return;
        } catch (ExecutionException ex) {
            // 解包ExecutionException获取原始异常
            cause = ex.getCause();
            if (cause == null) {
                cause = ex;
            }
        } catch (Throwable ex) {
            // 捕获所有未预期的Throwable
            cause = ex;
        }
        this.stage.failure(cause);
    }
}