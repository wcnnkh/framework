package run.soeasy.framework.core.domain;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.Assert;

/**
 * 边界模型，用于表示一个范围的单侧边界。
 * 每个边界可以是包含性的（inclusive）或排他性的（exclusive），也可以是无界的（unbounded）。
 *
 * @param <T> 边界值的类型
 */
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Bound<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表示无界的单例实例。
     */
    private static final Bound<?> UNBOUNDED = new Bound<>(null, null);

    /**
     * 边界的值。对于无界边界，此值为 null。
     */
    private final T value;

    /**
     * 边界的类型（包含或排他）。对于无界边界，此值为 null。
     */
    private final BoundType type;

    /**
     * 创建一个包含性的边界。
     *
     * @param value 边界值，不能为空
     * @param <T>   边界值的类型
     * @return 包含性边界实例
     */
    public static <T> Bound<T> inclusive(@NonNull T value) {
        return new Bound<>(value, BoundType.INCLUSIVE);
    }

    /**
     * 创建一个排他性的边界。
     *
     * @param value 边界值，不能为空
     * @param <T>   边界值的类型
     * @return 排他性边界实例
     */
    public static <T> Bound<T> exclusive(@NonNull T value) {
        return new Bound<>(value, BoundType.EXCLUSIVE);
    }

    /**
     * 获取无界边界的实例。
     *
     * @param <T> 边界值的类型
     * @return 无界边界实例
     */
    @SuppressWarnings("unchecked")
    public static <T> Bound<T> unbounded() {
        return (Bound<T>) UNBOUNDED;
    }

    /**
     * 判断此边界是否为无界。
     *
     * @return 如果是无界则返回 true，否则返回 false
     */
    public boolean isUnbounded() {
        return type == null;
    }

    /**
     * 判断此边界是否为有界。
     *
     * @return 如果是有界则返回 true，否则返回 false
     */
    public boolean isBounded() {
        return !isUnbounded();
    }

    /**
     * 判断此边界是否为包含性。
     *
     * @return 如果是包含性则返回 true，否则返回 false
     */
    public boolean isInclusive() {
        Assert.isTrue(isBounded(), "Cannot check inclusivity of an unbounded bound.");
        return type == BoundType.INCLUSIVE;
    }

    /**
     * 判断此边界是否为排他性。
     *
     * @return 如果是排他性则返回 true，否则返回 false
     */
    public boolean isExclusive() {
        Assert.isTrue(isBounded(), "Cannot check exclusivity of an unbounded bound.");
        return type == BoundType.EXCLUSIVE;
    }

    /**
     * 获取边界的值。
     *
     * @return 边界值的 Optional 对象，如果是无界则返回 empty
     */
    public Optional<T> getValue() {
        return Optional.ofNullable(value);
    }

    /**
     * 将当前边界的值转换为另一种类型，并返回一个新的边界。
     * 新边界的类型（包含/排他）与当前边界相同。无界边界转换后仍然是无界的。
     *
     * @param converter 转换函数
     * @param <U>       目标类型
     * @return 转换后的新边界
     */
    public <U> Bound<U> convert(Function<? super T, ? extends U> converter) {
        if (isUnbounded()) {
            return Bound.unbounded();
        }
        U newValue = converter.apply(this.value);
        return this.isInclusive() ? Bound.inclusive(newValue) : Bound.exclusive(newValue);
    }

    /**
     * 比较当前边界与另一个边界的位置。
     * <p>
     * 比较规则：
     * 1. 任何有界边界都大于无界边界 ({@link Bound#unbounded()})。
     * 2. 两个有界边界根据其值和类型进行比较：
     *    a. 值不同时，直接比较值。
     *    b. 值相同时，排他性边界大于包含性边界。
     *       例如：[5] &lt; (5)
     *
     * @param other      另一个要比较的边界
     * @param comparator 用于比较边界值的比较器
     * @return 一个负整数、零或正整数，表示当前边界小于、等于或大于另一个边界
     */
    public int compare(Bound<T> other, @NonNull Comparator<T> comparator) {
        if (this.isUnbounded()) {
            return other.isUnbounded() ? 0 : -1; // Unbounded is less than any bounded
        }
        if (other.isUnbounded()) {
            return 1; // Any bounded is greater than unbounded
        }

        // Both are bounded
        int valueComparison = comparator.compare(this.value, other.value);
        if (valueComparison != 0) {
            return valueComparison;
        }

        // Values are equal, check bound type: INCLUSIVE (-1) < EXCLUSIVE (1)
        if (this.isInclusive() && other.isExclusive()) return -1;
        if (this.isExclusive() && other.isInclusive()) return 1;
        return 0;
    }

    /**
     * 辅助方法：判断当前边界是否严格大于另一个边界。
     * 主要用于交集计算中，判断两个边界是否重叠。
     *
     * @param other      另一个边界
     * @param comparator 比较器
     * @return 如果当前边界严格大于 other，则返回 true
     */
    boolean isGreaterThan(Bound<T> other, @NonNull Comparator<T> comparator) {
        return this.compare(other, comparator) > 0;
    }

    /**
     * 判断给定的值是否位于当前边界的内侧（对于上界，是小于等于；对于下界，是大于等于）。
     * 此方法主要由 Range 内部使用。
     *
     * @param value      要检查的值
     * @param comparator 比较器
     * @return 如果值在边界内侧则返回 true
     */
    boolean leftContains(@NonNull T value, @NonNull Comparator<T> comparator) {
        if (isUnbounded()) return true;
        int cmp = comparator.compare(value, this.value);
        return isInclusive() ? cmp <= 0 : cmp < 0;
    }

    /**
     * 判断给定的值是否位于当前边界的外侧（对于上界，是大于等于；对于下界，是小于等于）。
     * 此方法主要由 Range 内部使用。
     *
     * @param value      要检查的值
     * @param comparator 比较器
     * @return 如果值在边界外侧则返回 true
     */
    boolean rightContains(@NonNull T value, @NonNull Comparator<T> comparator) {
        if (isUnbounded()) return true;
        int cmp = comparator.compare(value, this.value);
        return isInclusive() ? cmp >= 0 : cmp > 0;
    }

    /**
     * 返回边界的字符串表示。
     * 格式为：[value] 表示包含, (value) 表示排他, (-∞) 表示下界无界, (+∞) 表示上界无界。
     * 注意：单独使用时无法区分上下界，在 Range 中使用时会自动处理
     *
     * @return 边界的字符串表示
     */
    @Override
    public String toString() {
        if (isUnbounded()) {
            return "(-∞)"; // 在 Range 中会根据上下界位置转换为 (-∞ 或 +∞)
        }
        String prefix = isInclusive() ? "[" : "(";
        String suffix = isInclusive() ? "]" : ")";
        return prefix + value + suffix;
    }

    /**
     * 边界类型枚举。
     */
    private enum BoundType {
        INCLUSIVE,
        EXCLUSIVE
    }
}