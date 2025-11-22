package run.soeasy.framework.sequences;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.domain.Range;

/**
 * 基于原子操作的、具有固定步长和循环策略的整型计数器。
 * <p>
 * 该计数器继承自{@link AtomicInteger}，保证了多线程环境下的线程安全性。
 * 其步长和循环行为在构造时确定，之后不可更改。
 * </p>
 * <p>
 * 如果不指定范围，它将使用{@link Integer#MIN_VALUE}到{@link Integer#MAX_VALUE}的完整范围，
 * 并利用`int`类型的自然溢出特性。
 * </p>
 *
 * @author soeasy.run
 * @see AtomicInteger
 * @see IntegerCounter
 * @see Range
 */
@Getter
public class AtomicIntegerCounter extends AtomicInteger implements IntegerCounter {
    private static final long serialVersionUID = 1L;
    private final Range<Integer> range;
    private final int min;
    private final int max;
    private final int rangeSize;
    private final boolean cycle;

    /**
     * 默认构造方法。
     * <p>
     * 使用初始值 0，范围 {@link Integer#MIN_VALUE} 到 {@link Integer#MAX_VALUE}。
     */
    public AtomicIntegerCounter() {
        this(0);
    }

    /**
     * 使用指定初始值的构造方法。
     * <p>
     * 范围默认为 {@link Integer#MIN_VALUE} 到 {@link Integer#MAX_VALUE}。
     *
     * @param initialValue 初始值。
     */
    public AtomicIntegerCounter(int initialValue) {
        this(initialValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * 构造一个具有指定初始值、范围的原子计数器。
     * <p>
     * 步长默认为1，循环行为默认为不循环。
     *
     * @param initialValue 初始值，必须在指定的范围内。
     * @param min          范围的最小值（包含）。
     * @param max          范围的最大值（包含）。
     */
    public AtomicIntegerCounter(int initialValue, int min, int max) {
        this(initialValue, min, max, false);
    }

    /**
     * 构造一个具有指定初始值、范围和循环行为的原子计数器。
     * <p>
     * 这是最灵活的构造方法，提供了对计数器所有特性的完全控制。
     *
     * @param initialValue 初始值，必须在指定的范围内。
     * @param min          范围的最小值（包含）。
     * @param max          范围的最大值（包含）。
     * @param cycle        是否在超出范围时自动回绕（循环）。
     */
    public AtomicIntegerCounter(int initialValue, int min, int max, boolean cycle) {
        super(initialValue);
        this.range = Range.closed(min, max);
        this.min = min;
        this.max = max;
        this.cycle = cycle;
        // 计算范围大小，当范围为 [Integer.MIN_VALUE, Integer.MAX_VALUE] 时会溢出为 0
        this.rangeSize = max - min + 1;

        if (initialValue < min || initialValue > max) {
            throw new IllegalArgumentException(
                    String.format("Initial value %d is out of the specified range [%d, %d].", initialValue, min, max));
        }
    }

    /**
     * 获取此计数器的有效数值范围。
     *
     * @return 计数器的范围，是一个不可为null的{@link Range}对象。
     */
    @Override
    @NonNull
    public Range<Integer> getRange() {
        return range;
    }

    /**
     * 判断在给定步长下是否还有下一个有效元素。
     * <p>
     * 此判断基于当前计数器的值、指定的步长和循环配置，仅为瞬时快照，不保证后续{@link #next(Number)}调用一定成功（并发场景下可能被其他线程修改）。
     * <ul>
     * <li>如果构造时指定了循环（{@code cycle == true}），此方法永远返回{@code true}。</li>
     * <li>如果不循环，此方法会检查使用指定步长调用{@link #next(Number)}是否会超出范围：
     *     <ul>
     *     <li>步长为正时，判断当前值加上步长是否超过{@code max}（含溢出检查）。</li>
     *     <li>步长为负时，判断当前值加上步长是否小于{@code min}（含溢出检查）。</li>
     *     <li>步长为0时，只要当前值在范围内则返回{@code true}。</li>
     *     </ul>
     * </li>
     * </ul>
     *
     * @param step 要判断的步长，不可为null。
     * @return 如果预计使用该步长生成下一个元素是有效的，则返回 {@code true}；否则返回{@code false}。
     */
    @Override
    public boolean hasNext(@NonNull Integer step) {
        if (cycle) {
            return true;
        }

        int current = get();
        int stepValue = step.intValue();

        // 预判加法是否会溢出
        boolean willOverflow;
        if (stepValue > 0) {
            willOverflow = current > (max - stepValue);
        } else if (stepValue < 0) {
            willOverflow = current < (min - stepValue);
        } else { // step == 0
            // 如果步长为0，只要当前值在范围内，就认为有下一个（即当前值本身）
            return current >= min && current <= max;
        }

        // 如果会溢出，则下一次next()必然会超出范围
        if (willOverflow) {
            return false;
        }

        // 如果不会溢出，则安全地计算下一个值并检查是否在范围内
        int nextValue = current + stepValue;
        return nextValue >= min && nextValue <= max;
    }

    /**
     * 使用指定的步长原子地生成下一个序列值。
     * <p>
     * 此方法是线程安全的。它会忽略当前设置的步长{@link #getStep()}，直接使用传入的步长。
     * <ul>
     * <li>如果构造时指定了循环（{@code cycle == true}），当值超出范围时会自动回绕。
     *     回绕逻辑：若步长为正，超出{@code max}后从{@code min}开始计算；若步长为负，低于{@code min}后从{@code max}开始计算。
     *     特殊情况：当范围为{@link Integer#MIN_VALUE}到{@link Integer#MAX_VALUE}时，利用int的自然溢出实现循环。</li>
     * <li>如果不循环，当值超出范围时会抛出{@link IllegalStateException}。</li>
     * </ul>
     * <strong>注意：</strong> 当步长的绝对值大于范围大小时，回绕可能跳过多个周期，最终值仍保证在范围内。
     *
     * @param step 生成下一个值所用的步长，可以是正数（递增）或负数（递减）。步长为0时返回当前值（需在范围内）。
     * @return 下一个序列值，该值永远在构造时指定的范围内（如果支持循环），不可为null。
     * @throws IllegalStateException 如果不支持循环且计算出的下一个值超出了范围[{@code min}, {@code max}]。
     */
    @Override
    public @NonNull Integer next(@NonNull Integer step) {
        int current;
        int nextValue;
        int wrappedValue;

        do {
            current = get();
            nextValue = current + step;

            boolean isCycleNow = this.cycle;

            if (isCycleNow) {
                // 修复：处理 rangeSize 为 0 的极端情况（即范围是 [Integer.MIN_VALUE, Integer.MAX_VALUE]）
                if (rangeSize == 0) {
                    // 在这种情况下，int的自然溢出就是我们需要的循环行为
                    wrappedValue = nextValue;
                } else if (nextValue > max) {
                    int delta = nextValue - max;
                    wrappedValue = min + (delta - 1) % rangeSize;
                } else if (nextValue < min) {
                    int delta = min - nextValue;
                    wrappedValue = max - (delta - 1) % rangeSize;
                } else {
                    wrappedValue = nextValue;
                }
            } else {
                if (nextValue > max || nextValue < min) {
                    throw new IllegalStateException(String.format(
                            "Sequence out of range. Current: %d, Step: %d, Range: [%d, %d]", current, step, min, max));
                } else {
                    wrappedValue = nextValue;
                }
            }
        } while (!compareAndSet(current, wrappedValue));

        return wrappedValue;
    }
}