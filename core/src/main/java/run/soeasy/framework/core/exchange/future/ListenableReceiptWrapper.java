package run.soeasy.framework.core.exchange.future;

import java.util.concurrent.TimeUnit;

import run.soeasy.framework.core.exchange.Receipt;
import run.soeasy.framework.core.exchange.ReceiptWrapper;

/**
 * 可监听回执包装器接口，定义对ListenableReceipt的包装器模式实现。
 * 该接口继承自{@link ListenableReceipt}、{@link ListenableRegistrationWrapper}和{@link ReceiptWrapper}，
 * 允许在不修改原有回执实例的情况下增强其功能，如添加日志记录、结果转换或状态监控等。
 *
 * <p>核心特性：
 * <ul>
 *   <li>透明代理：默认实现将所有操作委派给源回执实例</li>
 *   <li>功能增强：子类可选择性覆盖方法以添加额外行为</li>
 *   <li>类型安全：通过泛型参数确保包装对象类型一致性</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>为异步操作回执添加日志记录</li>
 *   <li>实现回执结果的转换或增强</li>
 *   <li>添加回执状态的监控功能</li>
 *   <li>为回执操作添加事务性保障</li>
 * </ul>
 *
 * @param <T> 操作回执类型，需实现{@link Receipt}接口
 * @param <W> 包装器自身的类型，需实现当前接口
 * 
 * @author soeasy.run
 * @see ListenableReceipt
 * @see ListenableRegistrationWrapper
 * @see ReceiptWrapper
 */
public interface ListenableReceiptWrapper<T extends Receipt, W extends ListenableReceipt<T>>
        extends ListenableReceipt<T>, ListenableRegistrationWrapper<T, W>, ReceiptWrapper<W> {

    /**
     * 等待源回执的操作完成，最多等待指定时间
     * 默认实现将操作委派给源回执实例
     * 
     * @param timeout 最大等待时间
     * @param unit 时间单位
     * @return 如果操作在超时前完成返回true，否则返回false
     * @throws InterruptedException 如果当前线程在等待时被中断
     */
    @Override
    default boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return getSource().await(timeout, unit);
    }

    /**
     * 同步等待源回执的操作完成（不可中断模式）
     * 默认实现将操作委派给源回执实例
     * 
     * @return 当前可监听回执包装器实例，支持链式调用
     */
    @Override
    default ListenableReceipt<T> sync() {
        return getSource().sync();
    }

    /**
     * 同步等待源回执的操作完成（可中断模式）
     * 默认实现将操作委派给源回执实例
     * 
     * @return 当前可监听回执包装器实例，支持链式调用
     * @throws InterruptedException 如果当前线程在等待时被中断
     */
    @Override
    default ListenableReceipt<T> syncInterruptibly() throws InterruptedException {
        return getSource().syncInterruptibly();
    }
}