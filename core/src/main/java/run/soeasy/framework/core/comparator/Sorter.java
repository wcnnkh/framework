package run.soeasy.framework.core.comparator;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import lombok.NonNull;

/**
 * 双参数排序器。
 *
 * <p>
 * 该接口继承 {@link Evaluator} 和 {@link java.util.Comparator}，
 * 同时具备相等性检查和排序能力。
 * {@link #check(Object, Object)} 协变返回 {@link Ordering}，
 * 在判断相等性的同时提供顺序信息；
 * {@link #compare(Object, Object)} 适配 JDK 排序接口，
 * 可直接作为 {@link java.util.Comparator} 使用。
 *
 * <p>
 * 该接口<b>不覆写</b> {@link BiPredicate} 的 {@link #negate()}、
 * {@link #and(java.util.function.BiPredicate)}、
 * {@link #or(java.util.function.BiPredicate)} 方法，
 * 保持从 {@link Evaluator} 继承的默认行为（返回 {@code Evaluator} 类型）。
 * 排序相关的组合操作通过 {@link java.util.Comparator} 的
 * {@link #reversed()} 和 {@link #thenComparing} 系列方法提供。
 *
 * <p>
 * 该接口为函数式接口，支持通过 lambda 表达式快速创建实例：
 *
 * <pre>{@code
 * Sorter<String> s1 = (a, b) -> Ordering.of(a.compareTo(b));
 * Sorter<String> s2 = Sorter.of(String::compareTo);
 * }</pre>
 *
 * @param <T> 被比较对象的类型
 * @author soeasy.run
 */
@FunctionalInterface
public interface Sorter<T> extends Evaluator<T, T>, Comparator<T> {

    /**
     * 对两个对象执行比较，返回带顺序信息的比较结果。
     *
     * <p>
     * 协变覆写 {@link Evaluator#check(Object, Object)}，
     * 返回 {@link Ordering} 以在相等性判断之外提供排序能力。
     * 参数位置具有语义含义，实现类不应假定参数可交换。
     *
     * @param left  第一个对象，可以为 {@code null}
     * @param right 第二个对象，可以为 {@code null}
     * @return 表示比较结果的 {@link Ordering} 实例，不允许返回 {@code null}
     */
    @Override
    @NonNull
    Ordering evaluate(T left, T right);

    /**
     * 比较两个对象并返回顺序值。
     *
     * <p>
     * 默认实现委托给 {@link #check(Object, Object)} 并调用
     * {@link Ordering#getOrder()} 获取顺序值，
     * 兼容 {@link java.util.Comparator} 接口。
     *
     * @param o1 第一个对象
     * @param o2 第二个对象
     * @return 顺序值（负数表示 {@code o1 < o2}，零表示相等，正数表示 {@code o1 > o2}）
     */
    @Override
    default int compare(T o1, T o2) {
        return evaluate(o1, o2).getOrder();
    }

    // ═══════════════════════════════════════════
    // Comparator 覆写
    // ═══════════════════════════════════════════

    /**
     * 返回顺序反转的排序器。
     *
     * <p>
     * 保持参数位置不变，对 {@link #check(Object, Object)} 返回的顺序值取负。
     * 参数位置的语义不会被交换，仅排序方向反转。
     *
     * @return 顺序反转后的 {@code Sorter} 实例
     */
    @Override
    default Sorter<T> reversed() {
        return (left, right) -> Ordering.of(-evaluate(left, right).getOrder());
    }

    /**
     * 返回级联合成的排序器。
     *
     * <p>
     * 当当前排序器判定两个对象相等时，
     * 使用 {@code other} 排序器进行次级比较。
     *
     * @param other 次级排序器
     * @param <U>   {@code other} 的比较结果类型
     * @return 级联合成后的 {@code Sorter} 实例
     */
    @Override
    default <U extends Comparable<? super U>> Sorter<T> thenComparing(
            Function<? super T, ? extends U> keyExtractor) {
        return thenComparing(Comparator.comparing(keyExtractor));
    }

    /**
     * 返回级联合成的排序器。
     *
     * <p>
     * 当当前排序器判定两个对象相等时，
     * 使用 {@code other} 排序器进行次级比较。
     *
     * @param other 次级排序器
     * @return 级联合成后的 {@code Sorter} 实例
     */
    @Override
    default Sorter<T> thenComparing(Comparator<? super T> other) {
        return (left, right) -> {
            Ordering ordering = evaluate(left, right);
            if (!ordering.isEqual()) {
                return ordering;
            }
            int otherOrder = other.compare(left, right);
            return Ordering.of(otherOrder);
        };
    }

    /**
     * 返回级联合成的排序器（指定键提取器和键排序器）。
     *
     * @param keyExtractor 键提取函数
     * @param keyComparator 键排序器
     * @param <U> 键类型
     * @return 级联合成后的 {@code Sorter} 实例
     */
    @Override
    default <U> Sorter<T> thenComparing(
            Function<? super T, ? extends U> keyExtractor,
            Comparator<? super U> keyComparator) {
        return thenComparing(Comparator.comparing(keyExtractor, keyComparator));
    }

    /**
     * 返回级联合成的排序器（基于 int 键）。
     *
     * @param keyExtractor int 键提取函数
     * @return 级联合成后的 {@code Sorter} 实例
     */
    @Override
    default Sorter<T> thenComparingInt(ToIntFunction<? super T> keyExtractor) {
        return thenComparing(Comparator.comparingInt(keyExtractor));
    }

    /**
     * 返回级联合成的排序器（基于 long 键）。
     *
     * @param keyExtractor long 键提取函数
     * @return 级联合成后的 {@code Sorter} 实例
     */
    @Override
    default Sorter<T> thenComparingLong(ToLongFunction<? super T> keyExtractor) {
        return thenComparing(Comparator.comparingLong(keyExtractor));
    }

    /**
     * 返回级联合成的排序器（基于 double 键）。
     *
     * @param keyExtractor double 键提取函数
     * @return 级联合成后的 {@code Sorter} 实例
     */
    @Override
    default Sorter<T> thenComparingDouble(ToDoubleFunction<? super T> keyExtractor) {
        return thenComparing(Comparator.comparingDouble(keyExtractor));
    }

    // ═══════════════════════════════════════════
    // 静态工厂方法
    // ═══════════════════════════════════════════

    /**
     * 基于 {@link java.util.Comparator} 创建 {@code Sorter} 实例。
     *
     * <p>
     * 将 {@link java.util.Comparator#compare(Object, Object)} 的
     * 顺序值包装为 {@link Ordering} 返回。
     *
     * @param comparator JDK 比较器
     * @param <T>        被比较对象的类型
     * @return 对应的 {@code Sorter} 实例
     */
    static <T> Sorter<T> of(Comparator<? super T> comparator) {
        return (left, right) -> Ordering.of(comparator.compare(left, right));
    }

    /**
     * 基于 {@link Evaluator} 创建 {@code Sorter} 实例。
     *
     * <p>
     * 将 {@link Evaluator#evaluator(Object, Object)} 的 {@link Comparison}
     * 结果适配为 {@link Ordering}：相等返回 {@link Ordering#equal()}，
     * 不相等返回 {@link Ordering#of(int)} 且顺序值为 {@code 1}。
     *
     * @param evaluator 检查器
     * @param <T>     被比较对象的类型
     * @return 对应的 {@code Sorter} 实例
     */
    static <T> Sorter<T> of(Evaluator<? super T, ? super T> evaluator) {
        return (left, right) -> {
            Comparison c = evaluator.evaluate(left, right);
            return c.isEqual() ? Ordering.equal() : Ordering.of(1);
        };
    }

    /**
     * 返回一个始终返回"相等"结果的排序器。
     *
     * <p>
     * 无论传入什么参数，{@link #check(Object, Object)} 始终返回
     * {@link Ordering#equal()}。
     *
     * @param <T> 被比较对象的类型
     * @return 始终返回相等结果的 {@code Sorter} 实例
     */
    static <T> Sorter<T> alwaysEqual() {
        return (left, right) -> Ordering.equal();
    }

    /**
     * 返回一个始终返回"小于"结果的排序器。
     *
     * <p>
     * 无论传入什么参数，{@link #check(Object, Object)} 始终返回
     * {@link Ordering#less()}。
     *
     * @param <T> 被比较对象的类型
     * @return 始终返回小于结果的 {@code Sorter} 实例
     */
    static <T> Sorter<T> alwaysLess() {
        return (left, right) -> Ordering.less();
    }

    /**
     * 返回一个始终返回"大于"结果的排序器。
     *
     * <p>
     * 无论传入什么参数，{@link #check(Object, Object)} 始终返回
     * {@link Ordering#greater()}。
     *
     * @param <T> 被比较对象的类型
     * @return 始终返回大于结果的 {@code Sorter} 实例
     */
    static <T> Sorter<T> alwaysGreater() {
        return (left, right) -> Ordering.greater();
    }
}