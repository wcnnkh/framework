package run.soeasy.framework.core.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.collection.Elements;

/**
 * 范围模型，用于表示由下界和上界定义的连续区间，支持包含性检查、范围合并和类型转换等操作。
 * 该模型通过{@link Bound}实现边界的包含性控制，可处理有界、无界、包含或不包含边界值的各种场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>边界控制：通过{@link Bound}实现包含({@code []})或不包含({@code ()})边界值的精确控制</li>
 *   <li>无界支持：通过{@link #unbounded()}支持无限范围（如x &gt; 10或x &lt; 20）</li>
 *   <li>范围操作：提供包含检查、范围合并({@link #union})和范围包含判断({@link #contains(Range, Comparator)})</li>
 *   <li>类型安全：通过泛型确保范围值的类型一致性，支持类型转换({@link #convert})</li>
 *   <li>构建便捷：通过{@link RangeBuilder}和多种静态工厂方法创建不同类型的范围</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>数值范围校验（如年龄在18-60岁之间）</li>
 *   <li>时间区间表示（如2023-01-01至2023-12-31）</li>
 *   <li>数据过滤（如ID在1000-2000之间的记录）</li>
 *   <li>资源分配（如端口号在1024-65535之间）</li>
 *   <li>算法边界控制（如数组索引范围检查）</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 创建包含10到20的闭区间 [10,20]
 * Range&lt;Integer&gt; range = Range.closed(10, 20);
 * 
 * // 检查15是否在范围内
 * boolean contains = range.contains(15, Integer::compareTo);
 * 
 * // 创建左开右闭区间 (10,20]
 * Range&lt;Integer&gt; leftOpen = Range.leftOpen(10, 20);
 * 
 * // 合并两个范围
 * Range&lt;Integer&gt; union = range.union(leftOpen, Integer::compareTo);
 * </pre>
 *
 * @param <T> 范围值的类型，需支持通过比较器进行比较
 * @see Bound
 * @see RangeBuilder
 */
public final class Range<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 无界范围实例，等价于所有值都包含在范围内。
     * 该实例的下界和上界均为{@link Bound#unbounded()}。
     */
    private static final Range<?> UNBOUNDED = Range.of(Bound.unbounded(), Bound.unbounded());

    /**
     * 范围的下界，不可为null。
     * 下界决定范围的起始边界（包含或不包含）。
     */
    private final Bound<T> lowerBound;

    /**
     * 范围的上界，不可为null。
     * 上界决定范围的结束边界（包含或不包含）。
     */
    private final Bound<T> upperBound;

    /**
     * 构造指定上下界的范围实例。
     *
     * @param lowerBound 下界，不可为null
     * @param upperBound 上界，不可为null
     */
    public Range(@NonNull Bound<T> lowerBound, @NonNull Bound<T> upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    /**
     * 转换范围值的类型，创建新的范围实例。
     * <p>
     * 该方法通过给定的转换器将原范围的上下界值转换为新类型，
     * 新范围的包含性标志与原范围一致。
     *
     * @param converter 类型转换函数，不可为null
     * @param <U>       目标类型
     * @return 转换后的新范围实例
     */
    public <U, E> Range<U> convert(Function<? super T, ? extends U> converter) {
        return new Range<U>(lowerBound.convert(converter), upperBound.convert(converter));
    }

    /**
     * 获取范围的下界。
     *
     * @return 下界实例，不会为null
     */
    public Bound<T> getLowerBound() {
        return lowerBound;
    }

    /**
     * 获取范围的上界。
     *
     * @return 上界实例，不会为null
     */
    public Bound<T> getUpperBound() {
        return upperBound;
    }

    /**
     * 创建无界范围实例，等价于所有值都包含在范围内。
     * <p>
     * 无界范围的下界和上界均为{@link Bound#unbounded()}，
     * 因此{@link #contains(Object, Comparator)}始终返回true。
     *
     * @param <T> 范围值类型
     * @return 无界范围实例
     */
    @SuppressWarnings("unchecked")
    public static <T> Range<T> unbounded() {
        return (Range<T>) UNBOUNDED;
    }

    /**
     * 创建闭区间范围（上下界均包含），即[from, to]。
     *
     * @param <T> 范围值类型
     * @param from 下界值，不可为null
     * @param to   上界值，不可为null
     * @return 闭区间范围实例
     */
    public static <T> Range<T> closed(T from, T to) {
        return new Range<>(Bound.inclusive(from), Bound.inclusive(to));
    }

    /**
     * 创建开区间范围（上下界均不包含），即(from, to)。
     *
     * @param <T> 范围值类型
     * @param from 下界值，不可为null
     * @param to   上界值，不可为null
     * @return 开区间范围实例
     */
    public static <T> Range<T> open(T from, T to) {
        return new Range<>(Bound.exclusive(from), Bound.exclusive(to));
    }

    /**
     * 创建左开右闭区间范围（下界不包含，上界包含），即(from, to]。
     *
     * @param <T> 范围值类型
     * @param from 下界值，不可为null
     * @param to   上界值，不可为null
     * @return 左开右闭区间范围实例
     */
    public static <T> Range<T> leftOpen(T from, T to) {
        return new Range<>(Bound.exclusive(from), Bound.inclusive(to));
    }

    /**
     * 创建左闭右开区间范围（下界包含，上界不包含），即[from, to)。
     *
     * @param <T> 范围值类型
     * @param from 下界值，不可为null
     * @param to   上界值，不可为null
     * @return 左闭右开区间范围实例
     */
    public static <T> Range<T> rightOpen(T from, T to) {
        return new Range<>(Bound.inclusive(from), Bound.exclusive(to));
    }

    /**
     * 创建左无界右有界范围（下界无界，上界有界），即(-∞, to]或(-∞, to)。
     *
     * @param <T> 范围值类型
     * @param to  上界实例，不可为null
     * @return 左无界右有界范围实例
     */
    public static <T> Range<T> leftUnbounded(Bound<T> to) {
        return new Range<>(Bound.unbounded(), to);
    }

    /**
     * 创建右无界左有界范围（下界有界，上界无界），即[from, +∞)或(from, +∞)。
     *
     * @param <T> 范围值类型
     * @param from 下界实例，不可为null
     * @return 右无界左有界范围实例
     */
    public static <T> Range<T> rightUnbounded(Bound<T> from) {
        return new Range<>(from, Bound.unbounded());
    }

    /**
     * 创建范围构建器，从指定下界开始构建范围。
     *
     * @param lower 下界实例，不可为null
     * @param <T>   范围值类型
     * @return 范围构建器实例
     */
    public static <T> RangeBuilder<T> from(@NonNull Bound<T> lower) {
        return new RangeBuilder<>(lower);
    }

    /**
     * 根据给定的上下界创建范围实例。
     * <p>
     * 推荐使用{@link #from(Bound)}构建器风格API以提高可读性。
     *
     * @param lowerBound 下界实例，不可为null
     * @param upperBound 上界实例，不可为null
     * @param <T>        范围值类型
     * @return 范围实例
     * @see #from(Bound)
     */
    public static <T> Range<T> of(Bound<T> lowerBound, Bound<T> upperBound) {
        return new Range<>(lowerBound, upperBound);
    }

    /**
     * 创建仅包含单个值的范围实例，等价于闭区间[value, value]。
     *
     * @param <T>   范围值类型
     * @param value 单个值，不可为null
     * @return 仅包含该值的范围实例
     */
    public static <T> Range<T> just(T value) {
        return Range.closed(value, value);
    }

    /**
     * 检查值是否包含在当前范围内。
     * <p>
     * 包含性判断逻辑：
     * <ol>
     *   <li>值必须大于等于下界（根据下界的包含性）</li>
     *   <li>值必须小于等于上界（根据上界的包含性）</li>
     * </ol>
     * 无界范围将始终返回true。
     *
     * @param value      待检查的值，不可为null
     * @param comparator 比较器，不可为null
     * @return true如果值在范围内，false否则
     */
    public boolean contains(@NonNull T value, @NonNull Comparator<T> comparator) {
        return lowerBound.rightContains(value, comparator) && upperBound.leftContains(value, comparator);
    }

    /**
     * 合并多个范围为一个范围，返回包含所有输入范围的最小范围。
     * <p>
     * 合并逻辑：
     * <ol>
     *   <li>找到所有范围的最左下界作为新下界</li>
     *   <li>找到所有范围的最右上界作为新上界</li>
     * </ol>
     * 输入集合不可为空，否则抛出异常。
     *
     * @param elements   待合并的范围集合，不可为null且不可为空
     * @param comparator 比较器，不可为null
     * @param <R>        范围值类型
     * @return 合并后的范围实例
     * @throws IllegalArgumentException 如果输入集合为空
     */
    public static <R> Range<R> unionAll(@NonNull Elements<? extends Range<R>> elements,
                                        @NonNull Comparator<R> comparator) {
        Assert.isTrue(!elements.isEmpty(), "element cannot be empty");
        Bound<R> lower = elements.map((e) -> e.getLowerBound())
                .sorted((b1, b2) -> b1.compare(b2, comparator))
                .first();
        Bound<R> upper = elements.map((e) -> e.getUpperBound())
                .sorted((b1, b2) -> b1.compare(b2, comparator))
                .last();
        return new Range<>(lower, upper);
    }

    /**
     * 合并当前范围与另一个范围，返回包含两个范围的最小范围。
     *
     * @param range      待合并的范围，不可为null
     * @param comparator 比较器，不可为null
     * @return 合并后的范围实例
     */
    public Range<T> union(@NonNull Range<T> range, @NonNull Comparator<T> comparator) {
        return unionAll(Elements.forArray(this, range), comparator);
    }

    /**
     * 检查当前范围是否包含另一个范围。
     * <p>
     * 包含性判断逻辑：
     * <ol>
     *   <li>当前范围的下界必须小于等于另一个范围的下界（根据包含性）</li>
     *   <li>当前范围的上界必须大于等于另一个范围的上界（根据包含性）</li>
     * </ol>
     * 若当前范围有界而另一个范围无界，则返回false。
     *
     * @param range      待检查的范围，不可为null
     * @param comparator 比较器，不可为null
     * @return true如果当前范围包含另一个范围，false否则
     */
    public boolean contains(@NonNull Range<T> range, @NonNull Comparator<T> comparator) {
        if (lowerBound.isBounded()) {
            if (range.getLowerBound().isBounded()) {
                // 都有边界时，当前下界必须小于等于目标下界（根据包含性）
                if (!lowerBound.rightContains(range.getLowerBound().get(), comparator)) {
                    return false;
                }
            } else {
                // 当前有界而目标无界时，无法包含
                return false;
            }
        }

        if (upperBound.isBounded()) {
            if (range.getUpperBound().isBounded()) {
                // 都有边界时，当前上界必须大于等于目标上界（根据包含性）
                if (!upperBound.leftContains(range.getUpperBound().get(), comparator)) {
                    return false;
                }
            } else {
                // 当前有界而目标无界时，无法包含
                return false;
            }
        }
        return true;
    }

    /**
     * 生成范围左边界的字符串前缀（包含边界符号）。
     * <p>
     * 格式规则：
     * <ul>
     *   <li>无界：返回"unbounded"</li>
     *   <li>包含下界：返回"["+值</li>
     *   <li>不包含下界：返回"("+值</li>
     * </ul>
     *
     * @return 左边界前缀字符串
     */
    String toPrefixString() {
        return lowerBound.map(Object::toString)
                .map(it -> lowerBound.isInclusive() ? "[" + it : "(" + it)
                .orElse("unbounded");
    }

    /**
     * 生成范围右边界的字符串后缀（包含边界符号）。
     * <p>
     * 格式规则：
     * <ul>
     *   <li>无界：返回"unbounded"</li>
     *   <li>包含上界：返回值+"]"</li>
     *   <li>不包含上界：返回值+")"</li>
     * </ul>
     *
     * @return 右边界后缀字符串
     */
    String toSuffixString() {
        return upperBound.map(Object::toString)
                .map(it -> upperBound.isInclusive() ? it + "]" : it + ")")
                .orElse("unbounded");
    }

    /**
     * 返回范围的字符串表示，格式为"前缀-后缀"。
     * <p>
     * 示例：
     * <ul>
     *   <li>闭区间[10,20]："[10-20]"</li>
     *   <li>开区间(10,20)："(10-20)"</li>
     *   <li>左无界右闭区间(-∞,20]："unbounded-20]"</li>
     * </ul>
     *
     * @return 范围的字符串表示
     */
    @Override
    public String toString() {
        return String.format("%s-%s", toPrefixString(), toSuffixString());
    }

    /**
     * 计算范围的哈希值，基于上下界的哈希值。
     *
     * @return 哈希值
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{lowerBound, upperBound});
    }

    /**
     * 判断两个范围是否相等，基于上下界的相等性。
     *
     * @param obj 待比较对象
     * @return true如果对象是范围且上下界都相等，false否则
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Range) {
            Range<?> other = (Range<?>) obj;
            return ObjectUtils.equals(lowerBound, other.lowerBound) && ObjectUtils.equals(upperBound, other.upperBound);
        }
        return false;
    }

    /**
     * 范围构建器，支持从下界开始构建完整范围。
     * <p>
     * 使用示例：
     * <pre class="code">
     * Range&lt;Integer&gt; range = Range.from(Bound.inclusive(10)).to(Bound.exclusive(20));
     * </pre>
     *
     * @param <T> 范围值类型
     */
    public static class RangeBuilder<T> {
        private final Bound<T> lower;

        /**
         * 构造范围构建器，指定下界。
         *
         * @param lower 下界实例，不可为null
         */
        RangeBuilder(Bound<T> lower) {
            this.lower = lower;
        }

        /**
         * 构建完整范围，指定上界。
         *
         * @param upper 上界实例，不可为null
         * @return 完整范围实例
         */
        public Range<T> to(@NonNull Bound<T> upper) {
            return new Range<>(lower, upper);
        }
    }
}