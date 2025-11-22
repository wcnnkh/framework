// Range.java
package run.soeasy.framework.core.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.collection.Elements;

/**
 * 范围模型，用于表示由下界和上界定义的连续区间。
 * 支持包含性检查、范围合并和交集计算等操作。
 * 采用"构造时不检查，使用时检查"的策略。
 *
 * @param <T> 范围值的类型，需支持通过比较器进行比较
 */
public final class Range<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 无界范围实例，表示所有值都在范围内。
     * 使用静态初始化块进行初始化，以避免编译器错误。
     */
    private static final Range<?> UNBOUNDED = Range.of(Bound.unbounded(), Bound.unbounded());

    /**
     * 范围的下界。
     */
    private final Bound<T> lowerBound;

    /**
     * 范围的上界。
     */
    private final Bound<T> upperBound;

    /**
     * 私有构造函数。
     *
     * @param lowerBound 下界
     * @param upperBound 上界
     */
    private Range(@NonNull Bound<T> lowerBound, @NonNull Bound<T> upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    /**
     * 检查范围的有效性，即下界不大于上界。
     * 如果无效，将抛出 IllegalArgumentException。
     *
     * @param comparator 比较器
     */
    private void checkValidity(@NonNull Comparator<T> comparator) {
        if (lowerBound.isGreaterThan(upperBound, comparator)) {
            throw new IllegalArgumentException(
                    String.format("Invalid range: lower bound (%s) is greater than upper bound (%s)",
                            lowerBound, upperBound)
            );
        }
    }

    /**
     * 主动检查范围有效性
     * @param comparator 比较器
     * @throws IllegalArgumentException 如果范围无效
     */
    public void validate(@NonNull Comparator<T> comparator) {
        checkValidity(comparator);
    }

    // --- Static Factory Methods ---

    /**
     * 获取一个无界范围。
     *
     * @param <T> 范围值类型
     * @return 无界范围实例
     */
    @SuppressWarnings("unchecked")
    public static <T> Range<T> unbounded() {
        return (Range<T>) UNBOUNDED;
    }

    /**
     * 创建一个闭区间 [from, to]。
     * <strong>注意：此方法不检查 from 是否小于等于 to。</strong>
     *
     * @param from 下界值
     * @param to   上界值
     * @param <T>  范围值类型
     * @return 闭区间范围
     */
    public static <T> Range<T> closed(T from, T to) {
        return new Range<>(Bound.inclusive(from), Bound.inclusive(to));
    }

    /**
     * 创建一个开区间 (from, to)。
     * <strong>注意：此方法不检查 from 是否小于等于 to。</strong>
     *
     * @param from 下界值
     * @param to   上界值
     * @param <T>  范围值类型
     * @return 开区间范围
     */
    public static <T> Range<T> open(T from, T to) {
        return new Range<>(Bound.exclusive(from), Bound.exclusive(to));
    }

    /**
     * 创建一个左开右闭区间 (from, to]。
     * <strong>注意：此方法不检查 from 是否小于等于 to。</strong>
     *
     * @param from 下界值
     * @param to   上界值
     * @param <T>  范围值类型
     * @return 左开右闭区间范围
     */
    public static <T> Range<T> leftOpen(T from, T to) {
        return new Range<>(Bound.exclusive(from), Bound.inclusive(to));
    }

    /**
     * 创建一个左闭右开区间 [from, to)。
     * <strong>注意：此方法不检查 from 是否小于等于 to。</strong>
     *
     * @param from 下界值
     * @param to   上界值
     * @param <T>  范围值类型
     * @return 左闭右开区间范围
     */
    public static <T> Range<T> rightOpen(T from, T to) {
        return new Range<>(Bound.inclusive(from), Bound.exclusive(to));
    }

    /**
     * 创建一个左无界的范围 (-∞, to] 或 (-∞, to)。
     *
     * @param upperBound 上界
     * @param <T>        范围值类型
     * @return 左无界范围
     */
    public static <T> Range<T> leftUnbounded(@NonNull Bound<T> upperBound) {
        return new Range<>(Bound.unbounded(), upperBound);
    }

    /**
     * 创建一个右无界的范围 [from, +∞) 或 (from, +∞)。
     *
     * @param lowerBound 下界
     * @param <T>        范围值类型
     * @return 右无界范围
     */
    public static <T> Range<T> rightUnbounded(@NonNull Bound<T> lowerBound) {
        return new Range<>(lowerBound, Bound.unbounded());
    }

    /**
     * 根据给定的上下界创建一个范围。
     * <strong>注意：此方法不检查下界是否小于等于上界。</strong>
     *
     * @param lowerBound 下界
     * @param upperBound 上界
     * @param <T>        范围值类型
     * @return 范围实例
     */
    public static <T> Range<T> of(@NonNull Bound<T> lowerBound, @NonNull Bound<T> upperBound) {
        return new Range<>(lowerBound, upperBound);
    }

    /**
     * 创建一个只包含单个值的范围，即 [value, value]。
     *
     * @param value 单个值
     * @param <T>   范围值类型
     * @return 单值范围
     */
    public static <T> Range<T> just(T value) {
        return closed(value, value);
    }

    // --- Public Methods ---

    /**
     * 获取下界。
     *
     * @return 下界
     */
    public Bound<T> getLowerBound() {
        return lowerBound;
    }

    /**
     * 获取上界。
     *
     * @return 上界
     */
    public Bound<T> getUpperBound() {
        return upperBound;
    }

    /**
     * 将当前范围转换为另一种类型。
     * <strong>注意：此方法不检查转换后范围的有效性。</strong>
     *
     * @param converter 转换函数
     * @param <U>       目标类型
     * @return 转换后的范围
     */
    public <U> Range<U> convert(Function<? super T, ? extends U> converter) {
        return new Range<>(lowerBound.convert(converter), upperBound.convert(converter));
    }

    /**
     * 检查一个值是否包含在当前范围内。
     * 在此操作前会先检查范围的有效性。
     *
     * @param value      待检查的值
     * @param comparator 比较器
     * @return 如果包含则返回 true
     * @throws IllegalArgumentException 如果范围无效
     */
    public boolean contains(@NonNull T value, @NonNull Comparator<T> comparator) {
        checkValidity(comparator);
        return lowerBound.rightContains(value, comparator) && upperBound.leftContains(value, comparator);
    }

    /**
     * 检查另一个范围是否完全包含在当前范围内。
     * 在此操作前会先检查两个范围的有效性。
     *
     * @param other      另一个范围
     * @param comparator 比较器
     * @return 如果完全包含则返回 true
     * @throws IllegalArgumentException 如果任何一个范围无效
     */
    public boolean contains(@NonNull Range<T> other, @NonNull Comparator<T> comparator) {
        this.checkValidity(comparator);
        other.checkValidity(comparator);

        boolean lowerOk = this.lowerBound.compare(other.lowerBound, comparator) <= 0;
        boolean upperOk = this.upperBound.compare(other.upperBound, comparator) >= 0;
        return lowerOk && upperOk;
    }

    /**
     * 合并当前范围与另一个范围，返回包含两者的最小范围。
     * 在此操作前会先检查两个范围的有效性。
     *
     * @param other      另一个范围
     * @param comparator 比较器
     * @return 合并后的范围
     * @throws IllegalArgumentException 如果任何一个输入范围无效
     */
    public Range<T> union(@NonNull Range<T> other, @NonNull Comparator<T> comparator) {
        this.checkValidity(comparator);
        other.checkValidity(comparator);
        return unionAll(Elements.forArray(this, other), comparator);
    }

    /**
     * 合并多个范围，返回包含所有范围的最小范围。
     * 此方法不检查输入范围的有效性，合并后的范围有效性在使用时检查。
     *
     * @param ranges     待合并的范围集合
     * @param comparator 比较器
     * @param <R>        范围值类型
     * @return 合并后的范围
     */
    public static <R> Range<R> unionAll(@NonNull Elements<? extends Range<R>> ranges, @NonNull Comparator<R> comparator) {
        Assert.isTrue(!ranges.isEmpty(), "Cannot union an empty collection of ranges.");

        Bound<R> mergedLower = ranges.stream()
                .map(Range::getLowerBound)
                .min((b1, b2) -> b1.compare(b2, comparator))
                .orElseThrow(() -> new IllegalArgumentException("No ranges to merge"));

        Bound<R> mergedUpper = ranges.stream()
                .map(Range::getUpperBound)
                .max((b1, b2) -> b1.compare(b2, comparator))
                .orElseThrow(() -> new IllegalArgumentException("No ranges to merge"));

        return new Range<>(mergedLower, mergedUpper);
    }

    /**
     * 计算当前范围与另一个范围的交集。
     * 在此操作前会先检查两个范围的有效性。
     *
     * @param other      另一个范围
     * @param comparator 比较器
     * @return 一个包含交集范围的 Optional，如果无交集则为空
     * @throws IllegalArgumentException 如果任何一个输入范围无效
     */
    public Optional<Range<T>> intersection(@NonNull Range<T> other, @NonNull Comparator<T> comparator) {
        this.checkValidity(comparator);
        other.checkValidity(comparator);

        Bound<T> newLower = this.lowerBound.compare(other.lowerBound, comparator) >= 0 ? this.lowerBound : other.lowerBound;
        Bound<T> newUpper = this.upperBound.compare(other.upperBound, comparator) <= 0 ? this.upperBound : other.upperBound;

        if (newLower.isGreaterThan(newUpper, comparator)) {
            return Optional.empty();
        }

        return Optional.of(new Range<>(newLower, newUpper));
    }

    /**
     * 返回范围的字符串表示。
     *
     * @return 范围的字符串表示
     */
    @Override
    public String toString() {
        String lowerStr = lowerBound.isUnbounded() ? "(-∞" : 
                          (lowerBound.isInclusive() ? "[" + lowerBound.getValue().get() : "(" + lowerBound.getValue().get());
                          
        String upperStr = upperBound.isUnbounded() ? "+∞)" : 
                          (upperBound.isInclusive() ? upperBound.getValue().get() + "]" : upperBound.getValue().get() + ")");
                          
        return String.format("%s, %s", lowerStr, upperStr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Range<?> range = (Range<?>) o;
        return ObjectUtils.equals(lowerBound, range.lowerBound) && ObjectUtils.equals(upperBound, range.upperBound);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{lowerBound, upperBound});
    }
}