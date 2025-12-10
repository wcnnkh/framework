package run.soeasy.framework.core.exchange;

import lombok.NonNull;

/**
 * 可监听的接口
 * 定义注册监听器的标准行为，支持单条监听和批量监听模式
 * 
 * @author soeasy.run
 *
 * @param <T> 监听事件的类型
 */
public interface Listenable<T> {

    /**
     * 将当前可监听对象转换为批量监听模式
     * 默认实现返回假批量监听器（实际仍为单条监听）
     * 
     * @return 批量监听接口实现
     */
    default BatchListenable<T> batch() {
        return (FakeBatchListenable<T, Listenable<T>>) (() -> this);
    }

    /**
     * 注册一个监听器
     * 
     * @param listener 待注册的监听器
     * @return 注册操作的句柄，用于后续取消注册
     */
    Operation registerListener(@NonNull Listener<T> listener);
}