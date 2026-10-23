package run.soeasy.framework.commons.sequences;

import java.util.function.LongSupplier;
import lombok.NonNull;
import run.soeasy.framework.sequences.LongCounter;

/**
 * 序列生成器增强接口，支持步长递增和初始值自定义。
 *
 * <p>
 * 该接口继承自 {@link LongCounter} 和 {@link LongSupplier}，提供了一种灵活的方式来获取一系列递增的长整型数字（序列）。
 * 实现类通常用于生成全局唯一ID、订单号、流水号等场景。
 *
 * <p>
 * <strong>核心行为规则：</strong>
 * <ul>
 *     <li><strong>序列存在</strong>：调用方法后，内部序列值会增加指定步长，并返回增加后的新值。</li>
 *     <li><strong>序列不存在</strong>：调用方法时若提供了默认值，则使用该默认值初始化序列，并直接返回此默认值。</li>
 * </ul>
 *
 * @author soeasy.run
 */
public interface PersistentSequence extends LongCounter, LongSupplier {

    /**
     * 从底层资源（如数据库、Redis）获取一个原始的基准值。
     *
     * <p>
     * 此方法通常用于在序列首次初始化时，提供一个基础数值。它的具体行为由底层实现决定。
     *
     * @return 从底层资源获取的原始基准值。
     */
    @Override
    long getAsLong();

    /**
     * 获取下一个序列值，并自动处理初始值情况。
     *
     * <p>
     * 此方法首先尝试不带默认值地调用 {@link #next(Long, Long)}。
     * <ul>
     *     <li>如果序列已存在，该调用会成功并返回递增后的新值。</li>
     *     <li>如果序列不存在（返回{@code null}），它会使用 {@link #getAsLong()} 的结果加上步长作为默认值，再次调用 {@link #next(Long, Long)}。
     *     根据规则，这次调用会用该默认值初始化序列并返回它。</li>
     * </ul>
     *
     * <p>
     * <strong>示例:</strong>
     * <pre>
     * // 假设初始时序列不存在，且 getAsLong() 返回 100
     * SequenceAdder counter = ...; // 一个 SequenceAdder 的实现
     *
     * // 第一次调用：
     * // 1. 调用 next(10L, null)，序列不存在，返回 null。
     * // 2. 计算默认值：100 + 10 = 110。
     * // 3. 调用 next(10L, 110L)，序列不存在，用 110 初始化并返回 110。
     * Long firstValue = counter.next(10L); // firstValue = 110
     *
     * // 第二次调用：
     * // 1. 调用 next(10L, null)，序列已存在。
     * // 2. 内部值从 110 增加 10 变为 120，并返回 120。
     * Long secondValue = counter.next(10L); // secondValue = 120
     * </pre>
     *
     * @param step 步长，即每次序列值增加的量。
     * @return 下一个序列值。
     * @throws NullPointerException 如果 {@code step} 为 {@code null}。
     */
    @Override
    default Long next(@NonNull Long step) {
        Long value = next(step, null);
        if (value != null) {
            return value;
        }
        // 如果序列不存在，则使用 getAsLong() + step 作为默认值来初始化
        return next(step, getAsLong() + step);
    }

    /**
     * 获取下一个序列值，是接口的核心方法。
     *
     * <p>
     * <strong>行为细则：</strong>
     * <ol>
     *     <li>
     *         <strong>当序列已存在时：</strong>
     *         <p> 方法会尝试原子地将当前序列值增加指定的步长 {@code step}。
     *         <p> <strong>返回值：</strong> 返回增加步长 <strong>之后</strong> 的新值。
     *         <p> <strong>示例：</strong> 当前值为 120，步长为 10，调用后内部值变为 130，方法返回 130。
     *     </li>
     *     <li>
     *         <strong>当序列不存在时：</strong>
     *         <p> 方法会检查是否提供了 {@code defaultValue}。
     *         <ul>
     *             <li>
     *                 <strong>如果提供了 {@code defaultValue}：</strong>
     *                 <p> 方法会使用这个 {@code defaultValue} 来初始化序列。
     *                 <p> <strong>返回值：</strong> <strong>直接返回</strong> 这个 {@code defaultValue}。
     *                 <p> <strong>示例：</strong> 序列不存在，默认值为 110，调用后序列被初始化为 110，方法返回 110。
     *             </li>
     *             <li>
     *                 <strong>如果未提供 {@code defaultValue}（即 {@code null}）：</strong>
     *                 <p> 方法不会初始化序列。
     *                 <p> <strong>返回值：</strong> 返回 {@code null}。
     *             </li>
     *         </ul>
     *     </li>
     * </ol>
     *
     * @param step         步长，即每次序列值增加的量。
     * @param defaultValue 如果序列不存在，则使用此值作为初始值并返回它。如果为 {@code null}，表示不提供默认值。
     * @return 下一个序列值。如果序列不存在且未提供默认值，则返回 {@code null}。
     * @throws NullPointerException 如果 {@code step} 为 {@code null}。
     */
    Long next(@NonNull Long step, Long defaultValue);
}