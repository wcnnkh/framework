package run.soeasy.framework.core.comparator;

import java.util.function.BiPredicate;

import lombok.NonNull;

/**
 * 双参数比较器（基类）。
 *
 * <p>
 * 该接口作为比较体系的顶层抽象，定义两个对象（类型可不同）之间的
 * 比较契约。实现类通过 {@link #check(Object, Object)} 方法
 * 封装具体的比较逻辑，返回 {@link Comparison} 结果。
 *
 * <p>
 * 左操作数类型为 {@code L}，右操作数类型为 {@code R}，
 * 两者可以相同也可以不同。当类型相同时（{@code L = R}），
 * 退化为对称比较；当类型不同时，支持非对称比较场景
 * （如"期望格式 vs 实际值"、"模式 vs 目标"等）。
 *
 * <p>
 * {@link Comparison} 是二态的比较结果（相等/不相等），
 * 子接口可在此基础上扩展为带顺序信息的 {@link Ordering}（三态）。
 * 因此 {@code Evaluator} 本身不限定比较的语义粒度——
 * 实现类可以只关注相等性，也可以返回 {@link Ordering} 提供排序能力。
 *
 * <p>
 * 该接口继承 {@link BiPredicate}，天然具备谓词语义——
 * 可通过 {@link #test(Object, Object)} 直接获取布尔结果，
 * 也可作为 {@code Stream}、{@code Optional} 等 API 的谓词参数使用。
 * 同时覆写了 {@link #negate()}、{@link #and(BiPredicate)}、
 * {@link #or(BiPredicate)} 方法，使组合操作返回 {@code Evaluator} 类型，
 * 保持类型保真。
 *
 * <p>
 * 该接口为函数式接口，支持通过 lambda 表达式快速创建实例：
 *
 * <pre>{@code
 * // 同类型比较
 * Evaluator<String, String> c1 = (a, b) -> Comparison.of(a.equals(b));
 * Evaluator<String, String> c2 = Evaluator.of(Objects::equals);
 *
 * // 不同类型比较
 * Evaluator<Pattern, String> c3 = (pattern, text) ->
 *     Comparison.of(pattern.matcher(text).matches());
 * }</pre>
 *
 * @param <L> 左操作数的类型
 * @param <R> 右操作数的类型
 * @author soeasy.run
 */
@FunctionalInterface
public interface Evaluator<L, R> extends BiPredicate<L, R> {

    /**
     * 对两个对象执行比较。
     *
     * <p>
     * 实现类应在此方法中封装具体的比较逻辑，
     * 返回 {@link Comparison} 实例供调用方获取比较结论。
     * 子接口可通过协变返回类型返回 {@link Ordering} 等扩展结果。
     *
     * @param left  左操作数，可以为 {@code null}
     * @param right 右操作数，可以为 {@code null}
     * @return 表示比较结果的 {@link Comparison} 实例，不允许返回 {@code null}
     */
    @NonNull
    Comparison evaluate(L left, R right);

    /**
     * 判断两个对象是否满足比较条件，返回布尔结果。
     *
     * <p>
     * 默认实现委托给 {@link #check(Object, Object)} 并调用
     * {@link Comparison#isEqual()} 获取布尔值。
     * 对于只关心"是否匹配"而无需完整比较结果的场景，可直接使用此方法。
     *
     * @param l 左操作数
     * @param r 右操作数
     * @return 如果两个对象满足比较条件则返回 {@code true}，否则返回 {@code false}
     */
    @Override
    default boolean test(L l, R r) {
        return evaluate(l, r).isEqual();
    }

    /**
     * 对当前检查器的比较结果取反，返回一个新的检查器。
     *
     * <p>
     * 新检查器的 {@link #check(Object, Object)} 返回原检查器结果的
     * 逻辑非（相等变不相等，不相等变相等）。
     *
     * <p>
     * 覆写 {@link BiPredicate#negate()} 以返回 {@code Evaluator} 类型，
     * 保持类型保真。
     *
     * @return 取反后的 {@code Evaluator} 实例
     */
    @Override
    default Evaluator<L, R> negate() {
        return not(this);
    }

    /**
     * 将当前检查器与另一个谓词组合，返回逻辑"与"的新检查器。
     *
     * <p>
     * 当两个检查器对同一对对象的比较结果均为相等时，
     * 新检查器返回相等结果；否则返回不相等结果。
     *
     * <p>
     * 覆写 {@link BiPredicate#and(BiPredicate)} 以返回 {@code Evaluator} 类型，
     * 保持类型保真。
     *
     * @param other 另一个谓词或检查器
     * @return 逻辑"与"组合后的 {@code Evaluator} 实例
     */
    @Override
    default Evaluator<L, R> and(BiPredicate<? super L, ? super R> other) {
        return (left, right) -> Comparison.of(
        		evaluate(left, right).isEqual() && other.test(left, right)
        );
    }

    /**
     * 将当前检查器与另一个谓词组合，返回逻辑"或"的新检查器。
     *
     * <p>
     * 当两个检查器中至少有一个对同一对对象的比较结果为相等时，
     * 新检查器返回相等结果；否则返回不相等结果。
     *
     * <p>
     * 覆写 {@link BiPredicate#or(BiPredicate)} 以返回 {@code Evaluator} 类型，
     * 保持类型保真。
     *
     * @param other 另一个谓词或检查器
     * @return 逻辑"或"组合后的 {@code Evaluator} 实例
     */
    @Override
    default Evaluator<L, R> or(BiPredicate<? super L, ? super R> other) {
        return (left, right) -> Comparison.of(
        		evaluate(left, right).isEqual() || other.test(left, right)
        );
    }

    // ═══════════════════════════════════════════
    // 静态工厂方法
    // ═══════════════════════════════════════════

    /**
     * 基于 {@link BiPredicate} 创建 {@code Evaluator} 实例。
     *
     * <p>
     * 将 {@link BiPredicate#test(Object, Object)} 的布尔结果
     * 包装为 {@link Comparison} 返回。
     *
     * @param predicate 双参数谓词，用于判断两个对象的关系
     * @param <L>       左操作数的类型
     * @param <R>       右操作数的类型
     * @return 对应的 {@code Evaluator} 实例
     */
    static <L, R> Evaluator<L, R> of(BiPredicate<? super L, ? super R> predicate) {
        return (left, right) -> Comparison.of(predicate.test(left, right));
    }

    /**
     * 基于 {@link Comparison} 常量创建 {@code Evaluator} 实例。
     *
     * <p>
     * 无论传入什么参数，{@link #check(Object, Object)} 始终返回
     * 同一个 {@link Comparison} 实例。适用于需要固定比较结果的场景。
     *
     * @param comparison 固定的比较结果
     * @param <L>        左操作数的类型
     * @param <R>        右操作数的类型
     * @return 始终返回指定结果的 {@code Evaluator} 实例
     */
    static <L, R> Evaluator<L, R> of(Comparison comparison) {
        return (left, right) -> comparison;
    }

    /**
     * 返回一个始终返回"相等"结果的检查器。
     *
     * <p>
     * 无论传入什么参数，{@link #check(Object, Object)} 始终返回
     * {@link Comparison#equal()}。
     *
     * @param <L> 左操作数的类型
     * @param <R> 右操作数的类型
     * @return 始终返回相等结果的 {@code Evaluator} 实例
     */
    static <L, R> Evaluator<L, R> alwaysEqual() {
        return (left, right) -> Comparison.equal();
    }

    /**
     * 返回一个始终返回"不相等"结果的检查器。
     *
     * <p>
     * 无论传入什么参数，{@link #check(Object, Object)} 始终返回
     * {@link Comparison#notEqual()}。
     *
     * @param <L> 左操作数的类型
     * @param <R> 右操作数的类型
     * @return 始终返回不相等结果的 {@code Evaluator} 实例
     */
    static <L, R> Evaluator<L, R> alwaysNotEqual() {
        return (left, right) -> Comparison.notEqual();
    }

    /**
     * 基于 {@link Object#equals(Object)} 语义创建检查器（同类型）。
     *
     * <p>
     * 使用 {@link java.util.Objects#equals(Object, Object)} 进行比较，
     * 正确处理两个参数均为 {@code null} 或其中一个为 {@code null} 的情况。
     * 仅适用于左右操作数类型相同的场景。
     *
     * @param <T> 被比较对象的类型（左右操作数类型相同）
     * @return 基于 {@code equals} 语义的 {@code Evaluator} 实例
     */
    static <T> Evaluator<T, T> equals() {
        return (left, right) -> Comparison.of(left, right);
    }

    /**
     * 基于引用相等（{@code ==}）语义创建检查器（同类型）。
     *
     * <p>
     * 使用引用地址比较而非 {@link Object#equals(Object)}，
     * 适用于需要判断是否为同一对象实例的场景。
     * 仅适用于左右操作数类型相同的场景。
     *
     * @param <T> 被比较对象的类型（左右操作数类型相同）
     * @return 基于引用相等语义的 {@code Evaluator} 实例
     */
    static <T> Evaluator<T, T> same() {
        return (left, right) -> Comparison.of(left == right);
    }

    /**
     * 对现有检查器的比较结果取反，返回一个新的检查器。
     *
     * <p>
     * 新检查器的 {@link #check(Object, Object)} 返回原检查器结果的
     * 逻辑非（相等变不相等，不相等变相等）。
     *
     * @param evaluator 原始检查器
     * @param <L>     左操作数的类型
     * @param <R>     右操作数的类型
     * @return 取反后的 {@code Evaluator} 实例
     */
    static <L, R> Evaluator<L, R> not(Evaluator<? super L, ? super R> evaluator) {
        return (left, right) -> Comparison.of(!evaluator.evaluate(left, right).isEqual());
    }
}