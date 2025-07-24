package run.soeasy.framework.core.collection;

import java.io.Serializable;
import java.util.stream.Stream;

/**
 * 空流实现类，提供无元素的Streamable接口实现。
 * 该类为单例模式，通过{@link #EMPTY_STREAMABLE}静态实例获取空流对象。
 * 
 * @author soeasy.run
 * @param <E> 流元素类型（实际无元素）
 * @see Streamable
 */
public class EmptyStreamable<E> implements Streamable<E>, Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 空流的单例实例，用于表示不包含任何元素的流。
     * 推荐通过该实例获取空流，避免重复创建对象。
     */
    static final EmptyStreamable<Object> EMPTY_STREAMABLE = new EmptyStreamable<>();

    /**
     * 返回一个空的Stream实例，不包含任何元素。
     * 该方法始终返回{@link Stream#empty()}。
     * 
     * @return 空的Stream实例
     */
    @Override
    public Stream<E> stream() {
        return Stream.empty();
    }
}