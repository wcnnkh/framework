package run.soeasy.framework.core.collection;

import java.util.Enumeration;

/**
 * 函数式接口，定义元素枚举能力的规范。
 * 与{@link Iterable}接口类似，但返回{@link Enumeration}类型的枚举器，
 * 适用于需要兼容传统Java枚举接口的场景。
 *
 * @author soeasy.run
 * @param <E> 枚举元素的类型
 * @see Iterable
 * @see Enumeration
 */
@FunctionalInterface
public interface Enumerable<E> {
    
    /**
     * 返回一个枚举器，用于遍历元素集合。
     * 枚举器提供{@link Enumeration#hasMoreElements()}和{@link Enumeration#nextElement()}
     * 方法进行元素遍历，不支持元素移除操作。
     *
     * @return 元素枚举器实例
     */
    Enumeration<E> enumeration();
}