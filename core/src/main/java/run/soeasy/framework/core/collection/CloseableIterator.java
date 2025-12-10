package run.soeasy.framework.core.collection;

import java.util.Iterator;

/**
 * 扩展了{@link Iterator}接口，支持资源关闭的迭代器。
 * 实现此接口的类需要在迭代器不再使用时释放相关资源，
 * 通常通过try-with-resources语句自动调用{@link #close()}方法。
 *
 * @param <E> 迭代元素的类型
 *
 * @author soeasy.run
 * @see java.util.Iterator
 * @see java.lang.AutoCloseable
 */
public interface CloseableIterator<E> extends AutoCloseable, Iterator<E> {

    /**
     * 关闭迭代器并释放相关资源。
     * 如果迭代器已关闭，则调用此方法无效。
     *
     * @throws Exception 如果无法成功释放资源
     */
    @Override
    void close();
}