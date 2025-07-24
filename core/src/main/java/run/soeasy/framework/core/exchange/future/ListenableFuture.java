package run.soeasy.framework.core.exchange.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 可监听的异步未来结果接口，扩展了Java标准Future并支持事件监听。
 * 该接口继承自{@link ListenableReceipt}和{@link Future}，
 * 允许对异步操作的完成状态进行监听，并提供非阻塞结果获取能力。
 *
 * <p>核心特性：
 * <ul>
 *   <li>事件监听：支持注册完成、成功和失败事件监听器</li>
 *   <li>非阻塞获取：通过getNow()方法立即获取当前结果</li>
 *   <li>超时等待：提供带超时参数的await()方法</li>
 *   <li>兼容Java Future：完全实现标准Future接口</li>
 * </ul>
 *
 * <p>状态转换模型：
 * <ul>
 *   <li>初始状态：未完成（pending）</li>
 *   <li>完成状态：操作正常完成，结果可用</li>
 *   <li>失败状态：操作因异常终止</li>
 *   <li>取消状态：操作被主动取消</li>
 * </ul>
 *
 * @param <V> 异步操作返回的结果类型
 * 
 * @author soeasy.run
 * @see ListenableReceipt
 * @see Future
 */
public interface ListenableFuture<V> extends ListenableReceipt<ListenableFuture<V>>, Future<V> {

    /**
     * 尝试取消异步操作
     * 默认实现等价于调用cancel(false)，即不中断正在执行的任务
     * 
     * @return 如果操作成功取消返回true，否则返回false
     */
    @Override
    default boolean cancel() {
        return cancel(false);
    }

    /**
     * 立即获取当前结果（如果可用）
     * <p>
     * 该方法不会阻塞，直接返回当前状态下的结果：
     * <ul>
     *   <li>如果操作已成功完成，返回实际结果</li>
     *   <li>如果操作未完成或已失败，返回null</li>
     * </ul>
     * 
     * <p>注意：
     * <ul>
     *   <li>成功的结果集也可能为null，需结合isSuccess()判断</li>
     *   <li>该方法不抛出异常，所有异常状态通过isSuccess()反映</li>
     * </ul>
     * 
     * @return 当前结果，如果操作未完成或失败则返回null
     */
    V getNow();

    /**
     * 等待异步操作完成，最多等待指定时间
     * <p>
     * 该方法会阻塞当前线程直到：
     * <ul>
     *   <li>操作成功完成</li>
     *   <li>操作失败</li>
     *   <li>操作被取消</li>
     *   <li>超时时间到达</li>
     *   <li>当前线程被中断</li>
     * </ul>
     * 
     * <p>注意：
     * <ul>
     *   <li>超时或异常发生时返回false，而非抛出异常</li>
     *   <li>该方法等价于调用get(timeout, unit)并捕获异常</li>
     * </ul>
     * 
     * @param timeout 最大等待时间
     * @param unit 时间单位
     * @return 如果操作在超时前完成返回true，否则返回false
     * @throws InterruptedException 如果当前线程在等待时被中断
     */
    @Override
    default boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        try {
            get(timeout, unit);
            return true;
        } catch (ExecutionException | TimeoutException e) {
            return false;
        }
    }
}