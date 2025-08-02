package run.soeasy.framework.core.collection;

import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

/**
 * 元素包装器接口，扩展Elements和StreamableWrapper，用于封装元素集合操作。
 * 实现此接口的类可对元素集合进行包装，提供流式操作和集合转换功能。
 *
 * @param <E> 元素类型
 * @param <W> 被包装的Elements类型
 * 
 * @author soeasy.run
 */
public interface ElementsWrapper<E, W extends Elements<E>> extends Elements<E>, StreamableWrapper<E, W> {
    
    /**
     * 返回一个可缓存的Provider实例。
     * 缓存后再次获取元素时将直接返回缓存结果，避免重复计算。
     * 
     * @return 可缓存的Provider实例
     */
    @Override
    default Provider<E> cacheable() {
        return getSource().cacheable();
    }

    /**
     * 连接当前元素集合与另一个元素集合。
     * 新集合包含当前集合与参数集合的所有元素，顺序为当前集合在前。
     * 
     * @param elements 要连接的元素集合
     * @return 连接后的新元素集合
     */
    @Override
    default Elements<E> concat(Elements<? extends E> elements) {
        return getSource().concat(elements);
    }

    /**
     * 使用转换器映射元素流，支持调整流的大小特性。
     * 
     * @param resize 是否调整结果流的大小特性
     * @param converter 元素流转换函数，输入为当前元素流，输出为新元素流
     * @param <U> 转换后的元素类型
     * @return 映射后的新元素集合
     */
    @Override
    default <U> Elements<U> map(boolean resize, Function<? super Stream<E>, ? extends Stream<U>> converter) {
        return getSource().map(resize, converter);
    }

    /**
     * 返回去重后的元素集合，保留首次出现的元素。
     * 
     * @return 去重后的新元素集合
     */
    @Override
    default Elements<E> distinct() {
        return getSource().distinct();
    }

    /**
     * 返回元素的Enumeration视图，支持枚举遍历。
     * 
     * @return 元素的Enumeration实例
     */
    @Override
    default Enumeration<E> enumeration() {
        return getSource().enumeration();
    }

    /**
     * 排除满足指定谓词的元素，保留不满足条件的元素。
     * 
     * @param predicate 排除元素的判断条件
     * @return 排除后的新元素集合
     */
    @Override
    default Elements<E> exclude(Predicate<? super E> predicate) {
        return getSource().exclude(predicate);
    }

    /**
     * 过滤元素集合，保留满足指定谓词的元素。
     * 
     * @param predicate 过滤元素的判断条件
     * @return 过滤后的新元素集合
     */
    @Override
    default Elements<E> filter(Predicate<? super E> predicate) {
        return getSource().filter(predicate);
    }

    /**
     * 扁平映射元素，将每个元素转换为Streamable后合并为一个新集合。
     * 
     * @param mapper 元素到Streamable的映射函数
     * @param <U> 映射后的元素类型
     * @return 扁平映射后的新元素集合
     */
    @Override
    default <U> Elements<U> flatMap(Function<? super E, ? extends Streamable<U>> mapper) {
        return getSource().flatMap(mapper);
    }

    /**
     * 对每个元素执行指定的消费操作。
     * 
     * @param action 元素消费函数
     */
    @Override
    default void forEach(Consumer<? super E> action) {
        getSource().forEach(action);
    }

    /**
     * 返回元素的顺序访问视图，保证元素按顺序处理。
     * 
     * @return 顺序访问的元素集合
     */
    @Override
    default Elements<Sequential<E>> sequential() {
        return getSource().sequential();
    }

    /**
     * 返回元素的迭代器，支持遍历元素。
     * 
     * @return 元素迭代器
     */
    @Override
    default Iterator<E> iterator() {
        return getSource().iterator();
    }

    /**
     * 限制元素集合的最大大小，截断超出部分的元素。
     * 
     * @param maxSize 最大元素数量
     * @return 限制大小后的新元素集合
     */
    @Override
    default Elements<E> limit(long maxSize) {
        return getSource().limit(maxSize);
    }

    /**
     * 映射元素类型，将每个元素转换为新类型。
     * 
     * @param mapper 元素映射函数
     * @param <U> 转换后的元素类型
     * @return 映射后的新元素集合
     */
    @Override
    default <U> Elements<U> map(Function<? super E, ? extends U> mapper) {
        return getSource().map(mapper);
    }

    /**
     * 反转元素顺序，返回逆序的元素集合。
     * 
     * @return 反转顺序后的新元素集合
     */
    @Override
    default Elements<E> reverse() {
        return getSource().reverse();
    }

    /**
     * 跳过前n个元素，返回剩余元素的集合。
     * 
     * @param n 跳过的元素数量
     * @return 跳过指定数量后的新元素集合
     */
    @Override
    default Elements<E> skip(long n) {
        return getSource().skip(n);
    }

    /**
     * 按自然顺序排序元素集合。
     * 元素需实现Comparable接口。
     * 
     * @return 排序后的新元素集合
     */
    @Override
    default Elements<E> sorted() {
        return getSource().sorted();
    }

    /**
     * 按指定比较器排序元素集合。
     * 
     * @param comparator 元素比较器
     * @return 排序后的新元素集合
     */
    @Override
    default Elements<E> sorted(Comparator<? super E> comparator) {
        return getSource().sorted(comparator);
    }

    /**
     * 返回元素的Spliterator，支持并行遍历和批量操作。
     * 
     * @return Spliterator实例
     */
    @Override
    default Spliterator<E> spliterator() {
        return getSource().spliterator();
    }

    /**
     * 将元素集合转换为List包装器。
     * 
     * @return ListElementsWrapper实例
     */
    @Override
    default ListElementsWrapper<E, ? extends List<E>> toList() {
        return getSource().toList();
    }

    /**
     * 将元素集合转换为Set包装器。
     * 
     * @return SetElementsWrapper实例
     */
    @Override
    default SetElementsWrapper<E, ? extends Set<E>> toSet() {
        return getSource().toSet();
    }

    /**
     * 返回无序的元素集合，忽略元素的顺序特性。
     * 
     * @return 无序的元素集合
     */
    @Override
    default Elements<E> unordered() {
        return getSource().unordered();
    }

    /**
     * 为元素集合设置已知大小特性，提升流操作性能。
     * 
     * @param statisticsSize 大小计算函数
     * @return 设置大小后的新元素集合
     */
    default Elements<E> knownSize(ToLongFunction<? super Elements<E>> statisticsSize) {
        return getSource().knownSize(statisticsSize);
    }
}