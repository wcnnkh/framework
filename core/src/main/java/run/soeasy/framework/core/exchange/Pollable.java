package run.soeasy.framework.core.exchange;

/**
 * 可拉取接口
 * 定义数据拉取的标准行为，支持阻塞式获取数据
 * 
 * @author soeasy.run
 *
 * @param <T> 拉取数据的类型
 */
public interface Pollable<T> {
    /**
     * 拉取数据对象
     * 若没有可用数据则无限阻塞
     * 线程被中断时返回null
     * 
     * @return 下一个可用的数据对象，中断时返回null
     */
    T poll();
}