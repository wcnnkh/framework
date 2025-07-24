package run.soeasy.framework.core.exchange.future;

import lombok.NonNull;

/**
 * 承诺接口，整合了操作确认和可监听未来结果的功能。
 * 该接口继承自{@link Confirm}和{@link ListenableFuture}，
 * 用于表示一个可手动完成状态的异步操作，支持主动设置成功/失败结果。
 *
 * <p>状态转换模型：
 * <ul>
 *   <li>初始状态：未完成（pending）</li>
 *   <li>成功状态：通过setSuccess/trySuccess设置结果后进入</li>
 *   <li>失败状态：通过setFailure/tryFailure设置异常后进入</li>
 *   <li>取消状态：通过cancel方法主动取消操作</li>
 *   <li>状态不可逆：一旦进入成功/失败/取消状态，无法再转换</li>
 * </ul>
 *
 * <p>核心特性：
 * <ul>
 *   <li>手动状态控制：支持主动设置操作的成功/失败状态</li>
 *   <li>事件监听：继承ListenableFuture的完成/成功/失败监听机制</li>
 *   <li>取消机制：支持取消操作并标记为不可取消</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>异步操作的结果回调实现</li>
 *   <li>自定义Future模式的异步任务</li>
 *   <li>需要手动控制完成状态的异步操作</li>
 *   <li>异步操作的状态转换控制</li>
 * </ul>
 *
 * @param <V> 承诺结果的类型
 * 
 * @author soeasy.run
 * @see Confirm
 * @see ListenableFuture
 */
public interface Promise<V> extends Confirm, ListenableFuture<V> {

    /**
     * 将此承诺标记为失败状态并通知所有监听器
     * <p>
     * 该方法具有以下特性：
     * <ul>
     *   <li>若当前处于未完成状态，设置失败原因并触发失败事件</li>
     *   <li>若已处于成功/失败/取消状态，抛出IllegalStateException</li>
     * </ul>
     * 
     * @param cause 失败原因，不可为null
     * @return 当前Promise实例，支持链式调用
     * @throws IllegalStateException 若操作已完成（成功/失败/取消）
     */
    default Promise<V> setFailure(Throwable cause) {
        if (tryFailure(cause)) {
            return this;
        }
        throw new IllegalStateException();
    }

    /**
     * 将此承诺标记为成功状态并通知所有监听器
     * <p>
     * 该方法具有以下特性：
     * <ul>
     *   <li>若当前处于未完成状态，设置成功结果并触发成功事件</li>
     *   <li>若已处于成功/失败/取消状态，抛出IllegalStateException</li>
     * </ul>
     * 
     * @param result 成功结果，可为null
     * @return 当前Promise实例，支持链式调用
     * @throws IllegalStateException 若操作已完成（成功/失败/取消）
     */
    default Promise<V> setSuccess(V result) {
        if (trySuccess(result)) {
            return this;
        }
        throw new IllegalStateException();
    }

    /**
     * 将此承诺标记为不可取消状态
     * <p>
     * 该方法具有以下特性：
     * <ul>
     *   <li>若当前未被取消，标记为不可取消并返回true</li>
     *   <li>若已被取消，返回false</li>
     *   <li>已完成的承诺（成功/失败）始终视为不可取消</li>
     * </ul>
     * 
     * @return true 若成功标记为不可取消或已完成且未被取消；false 若已被取消
     */
    boolean setUncancellable();

    /**
     * 尝试将此承诺标记为失败状态并通知监听器
     * <p>
     * 该方法具有幂等性：
     * <ul>
     *   <li>未完成时：设置失败原因并返回true</li>
     *   <li>已完成时：不改变状态并返回false</li>
     * </ul>
     * 
     * @param cause 失败原因，不可为null
     * @return true 若成功标记为失败；false 若已完成（成功/失败/取消）
     */
    boolean tryFailure(@NonNull Throwable cause);

    /**
     * 尝试将此承诺标记为成功状态并通知监听器（结果为null）
     * <p>
     * 该方法是trySuccess(null)的便捷调用
     * 
     * @return true 若成功标记为成功；false 若已完成（成功/失败/取消）
     */
    @Override
    default boolean trySuccess() {
        return trySuccess(null);
    }

    /**
     * 尝试将此承诺标记为成功状态并通知监听器
     * <p>
     * 该方法具有幂等性：
     * <ul>
     *   <li>未完成时：设置成功结果并返回true</li>
     *   <li>已完成时：不改变状态并返回false</li>
     * </ul>
     * 
     * @param result 成功结果，可为null
     * @return true 若成功标记为成功；false 若已完成（成功/失败/取消）
     */
    boolean trySuccess(V result);

    /**
     * 取消此承诺
     * <p>
     * 该方法的默认实现等价于调用cancel(false)，即不中断运行中的任务
     * 
     * @return true 若取消成功；false 若已完成或取消失败
     */
    @Override
    default boolean cancel() {
        return ListenableFuture.super.cancel();
    }
}