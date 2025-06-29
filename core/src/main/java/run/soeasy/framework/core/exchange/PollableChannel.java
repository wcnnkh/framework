package run.soeasy.framework.core.exchange;

import java.util.concurrent.TimeUnit;

/**
 * 可轮询的通道接口
 * 定义支持超时机制的数据拉取通道，继承自Pollable和Channel接口
 * 
 * @author shuchaowen
 *
 * @param <T> 通道传输的数据类型
 */
public interface PollableChannel<T> extends Pollable<T>, Channel<T> {
    /**
     * 无限期阻塞拉取数据
     * 默认实现调用带超时的poll方法，超时时间为无限大
     * 
     * @return 可用的数据对象，若线程被中断则返回null
     */
    @Override
    default T poll() {
        return poll(INDEFINITE_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    /**
     * 从通道接收数据，支持超时机制
     * 
     * @param timeout 超时时间，非负
     * @param timeUnit 超时时间单位，不可为null
     * @return 可用的数据对象，超时或中断时返回null
     */
    T poll(long timeout, TimeUnit timeUnit);
}