package run.soeasy.framework.sequences;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Range;

/**
 * 长整数计数器接口，继承自通用计数器接口 {@link Counter}，专门用于处理 {@link Long} 类型的计数操作。
 *
 * <p>
 * 该接口提供了以下特性：
 * <ul>
 * <li>默认使用 {@link Long} 类型的完整取值范围（{@link Long#MIN_VALUE} 到 {@link Long#MAX_VALUE}）。</li>
 * <li>默认步长为 1（{@link #DEFAULT_STEP}）。</li>
 * <li>默认支持无限计数（{@link #hasNext(Long)} 始终返回 {@code true}）。</li>
 * <li>提供了一个高效的 {@link #snapshot(long)} 方法，用于获取一个指定大小的连续长整数序列快照。</li>
 * </ul>
 *
 * <p>
 * 由于该接口被标记为 {@link FunctionalInterface}，实现类只需要关注并实现核心方法 {@link Counter#next(Number)} 即可。
 *
 * @author soeasy.run
 * @see Counter
 * @see Sequence
 * @see AtomicLongCounter
 */
@FunctionalInterface
public interface LongCounter extends Counter<Long> {

    /**
     * 长整数计数器的默认最大范围，覆盖了 {@link Long} 类型的所有可能值。
     */
    public static final Range<Long> MAX_RANGE = Range.closed(Long.MIN_VALUE, Long.MAX_VALUE);

    /**
     * 长整数计数器的默认步长，值为 1。
     */
    public static final long DEFAULT_STEP = 1L;

    /**
     * 获取此计数器的有效数值范围。
     * <p>
     * 默认实现返回 {@link #MAX_RANGE}，即 {@link Long} 类型的完整取值范围。
     *
     * @return 一个不可为 {@code null} 的 {@link Range} 对象，包含了计数器的最小值和最大值。
     */
    @Override
    default @NonNull Range<Long> getRange() {
        return MAX_RANGE;
    }

    /**
     * 返回当前的默认步长。
     * <p>
     * 默认实现返回 {@link #DEFAULT_STEP}。
     *
     * @return 默认步长，值为 1。
     */
    @Override
    default @NonNull Long getStep() {
        return DEFAULT_STEP;
    }

    /**
     * 判断在使用指定步长的情况下，是否还有下一个有效数值。
     * <p>
     * 默认实现始终返回 {@code true}，因为 {@link Long} 类型的范围足够大，且溢出后会自然回绕，
     * 所以认为它永远不会耗尽。
     *
     * @param step 用于判断的步长，不可为 {@code null}。
     * @return 总是返回 {@code true}。
     */
    @Override
    default boolean hasNext(@NonNull Long step) {
        return true;
    }

    /**
     * 获取当前计数器的快照，返回一个包含指定大小的连续长整数的序列。
     * <p>
     * 该方法通过原子操作获取一个范围为 {@code [min, max]} 的连续序列，并将原计数器的值原子地增加 {@code size}。
     * 具体行为如下：
     * <ol>
     * <li> 调用 {@link #next(long)} 方法，以 {@code size} 为步长获取下一个值，这个值将作为新序列的 {@code max}。</li>
     * <li> 计算 {@code min = max - size}。</li>
     * <li> 返回一个新的 {@link Sequence} 对象，该对象包含从 {@code min} 到 {@code max} 的所有整数。
     *      新序列的 {@link Sequence#next()} 方法将从 {@code min} 开始，依次返回每个整数，直到 {@code max}。</li>
     * </ol>
     *
     * <p>
     * <strong>示例：</strong>
     * <ul>
     * <li>假设当前计数器的值为 1。</li>
     * <li>调用 {@code snapshot(3)}。</li>
     * <li>原计数器的值会原子地增加 3，变为 4。</li>
     * <li>方法返回一个包含 [2, 3, 4] 的序列快照。</li>
     * </ul>
     *
     * <p>
     * <strong>注意：</strong> 如果计数器启用了循环模式（{@link #isCycle()} 返回 {@code true}），
     * 则此方法的行为将委托给 {@link Counter#snapshot(long)} 的默认实现，该实现可能会返回一个跨越边界的序列。
     * 否则，将返回一个基于当前值的连续序列。
     *
     * @param size 所需的序列大小，必须为非负长整数（{@code size >= 0}）。
     * @return 包含连续长整数序列的 {@link Sequence} 对象，序列中的元素从 {@code min} 到 {@code max}。
     * @throws IllegalArgumentException 如果 {@code size} 为负数。
     */
    @Override
    default Sequence<Long> snapshot(long size) {
        if (isCycle()) {
            return Counter.super.snapshot(size);
        }
        Long max = next(size);
        Long min = max - size;
        return new AtomicLongCounter(min, min, max);
    }
}