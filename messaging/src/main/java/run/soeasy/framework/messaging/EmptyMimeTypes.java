package run.soeasy.framework.messaging;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * 空媒体类型集合实现类，实现{@link MediaTypes}接口，用于表示不包含任何媒体类型的空集合，
 * 采用单例模式设计，避免空集合的重复创建，提升性能。
 * 
 * <p>该类的所有方法均返回空迭代器或空流，符合"空对象模式"设计思想，可安全替代null值，
 * 避免在处理媒体类型集合时出现空指针异常。
 * 
 * @author soeasy.run
 * @see MediaTypes
 * @see MediaTypes#EMPTY
 */
final class EmptyMimeTypes implements MediaTypes, Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 返回空迭代器（不包含任何{@link MediaType}元素）
     * 
     * @return 空的迭代器实例
     */
    @Override
    public Iterator<MediaType> iterator() {
        return Collections.emptyIterator();
    }

    /**
     * 返回空流（不包含任何{@link MediaType}元素）
     * 
     * @return 空的流实例
     */
    @Override
    public Stream<MediaType> stream() {
        return Stream.empty();
    }

}