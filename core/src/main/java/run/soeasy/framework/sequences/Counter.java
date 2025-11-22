package run.soeasy.framework.sequences;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Range;

/**
 * 一个支持自定义步长和范围的序列生成器。
 *
 * <p>
 * {@code Counter} 接口扩展了 {@link Sequence} 接口，提供了更强大和灵活的数值序列生成能力。 它允许使用者：
 * <ol>
 * <li><b>按指定步长递增/递减</b>：通过 {@link #next(Number)} 方法，可以灵活地控制每次序列值变化的幅度和方向。</li>
 * <li><b>定义默认步长</b>：通过 {@link #getStep()} 方法提供一个默认步长，简化常规的、固定步长的序列生成。</li>
 * <li><b>检查序列边界</b>：通过 {@link #hasNext()} 和 {@link #hasNext(Number)}
 * 方法，可以在生成下一个值之前，预判序列是否会超出预定义的范围。</li>
 * <li><b>获取序列范围</b>：通过 {@link #getRange()} 方法，可以获取该计数器的有效数值区间。</li>
 * </ol>
 *
 * <p>
 * 该接口通过 Java 8 的默认方法（Default Method）提供了核心方法的标准实现，这带来了以下好处：
 * <ul>
 * <li><b>减少样板代码</b>：实现类无需重复编写 {@link #next()} 和 {@link #hasNext()}
 * 的逻辑，只需专注于核心的带参方法。</li>
 * <li><b>保持 API 一致性</b>：确保所有实现类都遵循 "无参方法使用默认步长" 的统一契约。</li>
 * <li><b>增强扩展性</b>：未来可以在接口中增加新的默认方法，而不会破坏现有的实现。</li>
 * </ul>
 *
 * <p>
 * <b>默认方法实现说明：</b>
 * <ul>
 * <li>{@link #next()} 的默认实现会调用 {@link #next(getStep())}，即使用当前的默认步长。</li>
 * <li>{@link #hasNext()} 的默认实现会调用
 * {@link #hasNext(getStep())}，即检查使用默认步长是否会导致序列超出范围。</li>
 * </ul>
 *
 * <p>
 * <b>典型应用场景：</b>
 * <ul>
 * <li><b>分页查询</b>：默认步长为每页记录数，通过 {@code next()} 生成下一页的起始索引。</li>
 * <li><b>双向迭代器</b>：通过调用 {@code next(1)} 向前移动，调用 {@code next(-1)} 向后移动。</li>
 * <li><b>动态步长调整</b>：实现类可以提供 {@code setStep(Number)} 方法，允许在运行时根据业务需求改变默认步长。</li>
 * <li><b>循环计数器</b>：当序列达到上界时，可以自动回绕到下界，形成一个环形缓冲区（Circular Buffer）。</li>
 * </ul>
 *
 * <p>
 * 实现类应当注意线程安全问题。如果计数器可能被多个线程并发访问，实现必须保证所有方法（特别是 {@link #next(Number)}）的原子性和可见性。
 *
 * @author soeasy.run
 * @param <T> 序列中数值的类型，必须是 {@link Number} 的子类（例如 {@link Integer}, {@link Long}）。
 * @see Sequence
 * @see Range
 */
public interface Counter<T extends Number> extends Sequence<T> {

    /**
     * 返回此计数器的有效数值范围。
     * <p>
     * 这个范围定义了计数器的活动边界。当使用 {@link #next(Number)} 生成下一个值时， 实现类可以选择：
     * <ul>
     * <li>在超出范围时抛出异常（如 {@link IllegalStateException}）。</li>
     * <li>或根据特定策略（如循环）调整值，使其保持在范围内。</li>
     * </ul>
     *
     * @return 一个不可为 {@code null} 的 {@link Range} 对象，包含了计数器的最小值和最大值。
     */
    @NonNull
    Range<T> getRange();

    /**
     * 返回当前计数器是否启用循环模式。
     * <p>
     * 当启用循环模式（{@code true}）时，当计数器的值超出 {@link #getRange()} 定义的范围时，
     * 它会自动回绕到范围的另一端。例如，如果范围是 [1, 100]，当前值是 100，步长是 1，
     * 那么下一个值将是 1。
     * <p>
     * 当禁用循环模式（{@code false}）时，计数器在超出范围时通常会抛出异常。
     * <p>
     * 此方法的默认实现返回 {@code true}，即默认支持循环。
     *
     * @return 如果计数器支持循环，则返回 {@code true}，否则返回 {@code false}。
     */
    default boolean isCycle() {
        return true;
    }

    /**
     * 返回当前的默认步长。
     * <p>
     * 这个步长会被无参的 {@link #next()} 和 {@link #hasNext()} 方法使用。
     * <p>
     * 步长可以是正数（递增）或负数（递减）。步长为零是允许的，但通常不推荐，因为它会导致 {@link #next()} 方法总是返回同一个值。
     *
     * @return 一个不可为 {@code null} 的 {@code T} 类型，表示当前的默认步长。
     */
    @NonNull
    T getStep();

    /**
     * 使用当前的默认步长生成并返回下一个序列值。
     * <p>
     * 此方法的默认实现等价于：{@code return next(getStep());}
     *
     * @return 序列中的下一个数值，不可为 {@code null}。
     * @throws IllegalStateException 如果序列已耗尽（超出范围且不支持循环）。
     */
    @Override
    default @NonNull T next() {
        return next(getStep());
    }

    /**
     * 判断在使用当前默认步长的情况下，是否还有下一个有效数值。
     * <p>
     * 此方法的默认实现等价于：{@code return hasNext(getStep());}
     * <p>
     * <strong>注意：</strong> 由于并发环境的特性，此方法的返回结果只是一个瞬时快照， 它无法 100% 保证后续调用
     * {@link #next()} 的成功。
     *
     * @return 如果预计下一个值有效（在范围内或支持循环），则返回 {@code true}，否则返回 {@code false}。
     */
    @Override
    default boolean hasNext() {
        return hasNext(getStep());
    }

    /**
     * 判断在使用指定步长的情况下，是否还有下一个有效数值。
     * <p>
     * 实现此方法时，应基于当前序列值、指定步长和 {@link #getRange()} 来进行预判。
     * <p>
     * <strong>注意：</strong> 由于并发环境的特性，此方法的返回结果只是一个瞬时快照， 它无法 100% 保证后续调用
     * {@link #next(Number)} 的成功。
     *
     * @param step 用于判断的步长，不可为 {@code null}。
     * @return 如果预计下一个值有效（在范围内或支持循环），则返回 {@code true}，否则返回 {@code false}。
     */
    boolean hasNext(@NonNull T step);

    /**
     * 使用指定的步长生成并返回下一个序列值。
     * <p>
     * 这是此接口的核心方法。实现类需要原子地执行以下操作：
     * <ol>
     * <li>获取当前的序列值。</li>
     * <li>根据指定的步长计算下一个值。</li>
     * <li>检查下一个值是否有效（根据范围和实现策略）。</li>
     * <li>如果有效，更新序列的当前值并返回新值。</li>
     * <li>如果无效，抛出异常。</li>
     * </ol>
     *
     * @param step 生成下一个值所用的步长，不可为 {@code null}。
     * @return 序列中的下一个数值，不可为 {@code null}。
     * @throws IllegalStateException 如果序列已耗尽（超出范围且不支持循环）。
     */
    @NonNull
    T next(@NonNull T step);
}