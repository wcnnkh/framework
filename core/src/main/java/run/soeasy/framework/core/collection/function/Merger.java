package run.soeasy.framework.core.collection.function;

/**
 * 合并器函数式接口，用于将多个元素合并为一个结果。
 * 该接口扩展自Selector接口，允许将合并操作视为一种特殊的选择操作，
 * 支持多种合并策略，如扁平化合并、Map合并和Properties合并等。
 *
 * @author soeasy.run
 * 
 * @param <E> 待合并的元素类型
 * @see Selector
 * @see FlatMerger
 * @see MapMerger
 * @see PropertiesMerger
 */
@FunctionalInterface
public interface Merger<E> extends Selector<E> {

    /**
     * 返回一个扁平化合并器，用于将嵌套的元素集合展开并合并为单层集合。
     * 该合并器会过滤掉null元素，并将所有非空元素集合中的元素提取出来，
     * 最终合并成一个包含所有内层元素的单层集合。
     *
     * @param <T> 元素类型
     * @return 扁平化合并器实例
     */
    @SuppressWarnings("unchecked")
    public static <T> Merger<T> flat() {
        return (Merger<T>) FlatMerger.INSTANCE;
    }

    /**
     * 返回一个Map合并器，用于将多个Map实例合并为一个Map。
     * 该合并器会按顺序处理输入的Map集合，将每个Map中的键值对依次添加到结果Map中，
     * 如果存在相同的键，后出现的Map中的值会覆盖先出现的Map中的值。
     *
     * @param <K> 键类型
     * @param <V> 值类型
     * @return Map合并器实例
     */
    @SuppressWarnings("unchecked")
    public static <K, V> MapMerger<K, V> map() {
        return (MapMerger<K, V>) MapMerger.INSTANCE;
    }

    /**
     * 返回一个Properties合并器，用于将多个Properties实例合并为一个。
     * 该合并器会按顺序处理输入的Properties集合，将每个Properties中的键值对依次添加到结果中，
     * 如果存在相同的键，后出现的Properties中的值会覆盖先出现的Properties中的值。
     *
     * @return Properties合并器实例
     */
    public static PropertiesMerger properties() {
        return PropertiesMerger.INSTANCE;
    }
}