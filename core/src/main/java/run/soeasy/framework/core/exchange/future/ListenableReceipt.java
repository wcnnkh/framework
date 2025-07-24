package run.soeasy.framework.core.exchange.future;

import java.util.concurrent.TimeUnit;

import run.soeasy.framework.core.exchange.Receipt;

/**
 * 可监听的操作回执接口，定义支持事件监听的异步操作回执。
 * 该接口继承自{@link ListenableRegistration}和{@link Receipt}，
 * 允许对异步操作的完成状态进行监听，并提供同步等待功能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>状态监听：支持监听操作的完成、成功和失败状态</li>
 *   <li>同步等待：提供带超时和不带超时的同步等待机制</li>
 *   <li>中断处理：支持可中断和不可中断的同步等待模式</li>
 * </ul>
 *
 * <p>状态转换模型：
 * <ul>
 *   <li>初始状态：未完成（pending）</li>
 *   <li>完成状态：操作正常完成或异常终止</li>
 *   <li>可通过isSuccess()判断操作是否成功完成</li>
 * </ul>
 *
 * @param <T> 操作回执类型，需实现{@link Receipt}接口
 * 
 * @author soeasy.run
 * @see ListenableRegistration
 * @see Receipt
 */
public interface ListenableReceipt<T extends Receipt> extends ListenableRegistration<T>, Receipt {

    /**
     * 等待操作完成，最多等待指定时间
     * <p>
     * 该方法会阻塞当前线程直到：
     * <ul>
     *   <li>操作成功完成</li>
     *   <li>操作失败</li>
     *   <li>超时时间到达</li>
     *   <li>当前线程被中断</li>
     * </ul>
     * 
     * @param timeout 最大等待时间
     * @param unit 时间单位
     * @return 如果操作在超时前完成返回true，否则返回false
     * @throws InterruptedException 如果当前线程在等待时被中断
     */
    boolean await(long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * 同步等待操作完成（不可中断模式）
     * <p>
     * 该方法会一直等待直到操作完成，忽略所有中断请求。
     * 等效于调用await(Long.MAX_VALUE, TimeUnit.MILLISECONDS)并忽略中断异常。
     * 
     * @return 当前可监听回执实例，支持链式调用
     */
    @Override
    default ListenableReceipt<T> sync() {
        while (true) {
            try {
                if (await(Long.MAX_VALUE, TimeUnit.MILLISECONDS)) {
                    break;
                }
            } catch (InterruptedException e) {
                // 忽略此异常一直等
            }
        }
        return this;
    }

    /**
     * 同步等待操作完成（可中断模式）
     * <p>
     * 该方法会一直等待直到操作完成，若当前线程被中断则抛出异常。
     * 等效于调用await(Long.MAX_VALUE, TimeUnit.MILLISECONDS)并传播中断异常。
     * 
     * @return 当前可监听回执实例，支持链式调用
     * @throws InterruptedException 如果当前线程在等待时被中断
     */
    default ListenableReceipt<T> syncInterruptibly() throws InterruptedException {
        while (await(Long.MAX_VALUE, TimeUnit.MILLISECONDS)) {
            break;
        }
        return this;
    }
}