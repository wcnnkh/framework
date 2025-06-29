package run.soeasy.framework.core.exchange.future;

/**
 * 承诺接口
 * 结合了确认(Confirm)和可监听未来(ListenableFuture)的功能，用于表示一个可完成的异步操作
 * 
 * @author shuchaowen
 * @param <V> 承诺结果的类型
 */
public interface Promise<V> extends Confirm, ListenableFuture<V> {
    
    /**
     * 将此承诺标记为失败并通知所有监听器
     * 
     * 如果已成功或失败，将抛出IllegalStateException
     * 
     * @param cause 失败原因
     * @return 当前Promise实例
     * @throws IllegalStateException 如果已完成(成功或失败)
     */
    default Promise<V> setFailure(Throwable cause) {
        if (tryFailure(cause)) {
            return this;
        }
        throw new IllegalStateException();
    }

    /**
     * 将此承诺标记为成功并通知所有监听器
     * 
     * 如果已成功或失败，将抛出IllegalStateException
     * 
     * @param result 成功结果
     * @return 当前Promise实例
     * @throws IllegalStateException 如果已完成(成功或失败)
     */
    default Promise<V> setSuccess(V result) {
        if (trySuccess(result)) {
            return this;
        }
        throw new IllegalStateException();
    }

    /**
     * 将此承诺标记为不可取消
     * 
     * @return true 如果成功标记为不可取消，或已完成且未被取消；false 如果已被取消
     */
    boolean setUncancellable();

    /**
     * 尝试将此承诺标记为失败并通知所有监听器
     * 
     * @param cause 失败原因
     * @return true 如果成功标记为失败；false 如果已完成(成功或失败)
     */
    boolean tryFailure(Throwable cause);

    /**
     * 尝试将此承诺标记为成功并通知所有监听器（结果为null）
     * 
     * @return true 如果成功标记为成功；false 如果已完成(成功或失败)
     */
    @Override
    default boolean trySuccess() {
        return trySuccess(null);
    }

    /**
     * 尝试将此承诺标记为成功并通知所有监听器
     * 
     * @param result 成功结果
     * @return true 如果成功标记为成功；false 如果已完成(成功或失败)
     */
    boolean trySuccess(V result);

    /**
     * 取消此承诺
     * 
     * @return true 如果取消成功；false 如果已完成或取消失败
     */
    @Override
    default boolean cancel() {
        return ListenableFuture.super.cancel();
    }
}