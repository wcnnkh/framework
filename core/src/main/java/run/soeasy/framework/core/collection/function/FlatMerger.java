package run.soeasy.framework.core.collection.function;

import java.util.function.Function;

import run.soeasy.framework.core.collection.Elements;

/**
 * 扁平化合并器，用于将嵌套的元素集合展开并合并为单层元素集合。
 * 该合并器会过滤掉所有null元素，并将每个非空元素集合中的元素提取出来，
 * 最终合并成一个包含所有内层元素的单层集合。
 *
 * @author soeasy.run
 * @param <E> 元素类型
 * @see Merger
 * @see Elements
 */
public class FlatMerger<E> implements Merger<Elements<? extends E>> {
    
    /**
     * 单例实例，用于全局共享的扁平化合并器。
     * 该实例为无泛型参数的原始类型，可通过类型转换安全地用于任何元素类型。
     */
    static final FlatMerger<?> INSTANCE = new FlatMerger<>();

    /**
     * 将嵌套的元素集合展开并合并为单层元素集合。
     * 该方法执行以下操作：
     * 1. 过滤掉所有null元素集合
     * 2. 将每个非空元素集合中的元素提取出来
     * 3. 合并所有提取出的元素为一个新的单层集合
     *
     * @param elements 待合并的嵌套元素集合
     * @return 合并后的单层元素集合
     */
    @Override
    public Elements<E> select(Elements<Elements<? extends E>> elements) {
        return elements.filter((e) -> e != null).flatMap((e) -> e.map(Function.identity()));
    }
}