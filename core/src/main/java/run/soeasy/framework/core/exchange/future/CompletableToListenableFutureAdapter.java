package run.soeasy.framework.core.exchange.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;

import run.soeasy.framework.core.exchange.Listener;
import run.soeasy.framework.core.exchange.Registration;

/**
 * CompletableFuture到ListenableFuture的适配器
 * 将Java标准的CompletableFuture适配为框架定义的ListenableFuture接口
 * 
 * @author shuchaowen
 * @param <T> 未来结果的类型
 */
public class CompletableToListenableFutureAdapter<T> implements ListenableFuture<T> {

    private final CompletableFuture<T> completableFuture;
    private Stage stage = new Stage();

    /**
     * 基于CompletionStage创建适配器
     * 
     * @param completionStage 待适配的CompletionStage，会被转换为CompletableFuture
     */
    public CompletableToListenableFutureAdapter(CompletionStage<T> completionStage) {
        this(completionStage.toCompletableFuture());
    }

    /**
     * 基于CompletableFuture创建适配器
     * 
     * @param completableFuture 待适配的CompletableFuture
     */
    public CompletableToListenableFutureAdapter(CompletableFuture<T> completableFuture) {
        this.completableFuture = completableFuture;
        this.completableFuture.handle(new BiFunction<T, Throwable, Object>() {
            @Override
            public Object apply(T result, Throwable ex) {
                if (ex != null) {
                    stage.failure(ex);
                } else {
                    stage.success(result);
                }
                return null;
            }
        });
    }

    /**
     * 注册监听器
     * 
     * @param listener 监听器，当未来结果完成时触发
     * @return 注册句柄，可用于取消监听
     */
    @Override
    public Registration registerListener(Listener<ListenableFuture<? extends T>> listener) {
        return stage.registerListener((e) -> listener.accept(this));
    }

    /**
     * 取消未来操作
     * 
     * @param mayInterruptIfRunning 是否可中断运行中的任务
     * @return 取消是否成功
     */
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (this.completableFuture.cancel(mayInterruptIfRunning)) {
            stage.cancel();
            return true;
        }
        return false;
    }

    /**
     * 判断是否已取消
     * 
     * @return 是否已取消
     */
    public boolean isCancelled() {
        return this.completableFuture.isCancelled();
    }

    /**
     * 判断是否已完成
     * 
     * @return 是否已完成
     */
    public boolean isDone() {
        return this.completableFuture.isDone();
    }

    /**
     * 获取结果（阻塞等待）
     * 
     * @return 未来结果
     * @throws InterruptedException 等待被中断
     * @throws ExecutionException 执行过程中发生异常
     */
    public T get() throws InterruptedException, ExecutionException {
        return this.completableFuture.get();
    }

    /**
     * 获取结果（带超时等待）
     * 
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return 未来结果
     * @throws InterruptedException 等待被中断
     * @throws ExecutionException 执行过程中发生异常
     * @throws TimeoutException 等待超时
     */
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.completableFuture.get(timeout, unit);
    }

    /**
     * 判断是否可取消
     * 
     * @return 是否可取消
     */
    @Override
    public boolean isCancellable() {
        return stage.isCancellable();
    }

    /**
     * 获取异常原因
     * 
     * @return 执行异常，若无异常则返回null
     */
    @Override
    public Throwable cause() {
        return stage.cause();
    }

    /**
     * 判断是否成功完成
     * 
     * @return 是否成功完成
     */
    @Override
    public boolean isSuccess() {
        return stage.isSuccess();
    }

    /**
     * 获取结果（非阻塞）
     * 
     * @return 已完成的结果，若未完成则返回null
     */
    @SuppressWarnings("unchecked")
    @Override
    public T getNow() {
        return (T) stage.getResult();
    }
}