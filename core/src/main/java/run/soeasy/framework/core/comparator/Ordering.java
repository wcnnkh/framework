package run.soeasy.framework.core.comparator;

/**
 * 带顺序信息的比较结果。
 *
 * <p>
 * 继承 {@link Ordered} 表示"顺序"这一抽象概念的统一—— 无论是组件优先级还是比较结果，本质都是用一个整数表达相对位置。 在
 * {@code Ordering} 的上下文中，{@link #getOrder()} 返回的是 两个对象比较后的结果码：
 * <ul>
 * <li>负数：左对象小于右对象</li>
 * <li>零：两对象相等</li>
 * <li>正数：左对象大于右对象</li>
 * </ul>
 *
 * <p>
 * 这与 {@link Ordered} 的优先级语义（数值越小优先级越高） 在具体含义上不同，但在抽象层面共享同一个 {@code getOrder()}
 * 契约。
 *
 * @author soeasy.run
 */
@FunctionalInterface
public interface Ordering extends Comparison, Ordered {

	/**
	 * 返回一个始终表示"相等"的顺序比较结果。
	 *
	 * <p>
	 * 返回的实例 {@link #getOrder()} 始终返回 {@code 0}， 多次调用返回不同的实例。
	 *
	 * @return 表示相等的 {@code Ordering} 实例
	 */
	static Ordering equal() {
		return () -> 0;
	}

	/**
	 * 返回一个始终表示"大于"的顺序比较结果。
	 *
	 * <p>
	 * 返回的实例 {@link #getOrder()} 始终返回 {@code 1}。
	 *
	 * @return 表示大于的 {@code Ordering} 实例
	 */
	static Ordering greater() {
		return () -> 1;
	}

	/**
	 * 返回一个始终表示"小于"的顺序比较结果。
	 *
	 * <p>
	 * 返回的实例 {@link #getOrder()} 始终返回 {@code -1}。
	 *
	 * @return 表示小于的 {@code Ordering} 实例
	 */
	static Ordering less() {
		return () -> -1;
	}

	/**
	 * 基于比较结果码创建 {@code Ordering} 实例。
	 *
	 * @param order 比较结果码（负数表示小于，零表示相等，正数表示大于）
	 * @return 对应的 {@code Ordering} 实例
	 */
	static Ordering of(int order) {
		int value = order;
		return () -> value;
	}

	/**
	 * 判断被比较的两个对象是否相等。
	 *
	 * <p>
	 * 基于 {@link #getOrder()} 的返回值进行判断： 当顺序值为 {@code 0} 时认为两个对象相等。
	 *
	 * @return 如果被比较的对象相等则返回 {@code true}，否则返回 {@code false}
	 */
	@Override
	default boolean isEqual() {
		return getOrder() == 0;
	}

	/**
	 * 判断左对象是否大于右对象。
	 *
	 * <p>
	 * 基于 {@link #getOrder()} 的返回值进行判断： 当顺序值大于 {@code 0} 时认为左对象大于右对象。
	 *
	 * @return 如果左对象大于右对象则返回 {@code true}，否则返回 {@code false}
	 */
	default boolean isGreater() {
		return getOrder() > 0;
	}

	/**
	 * 判断左对象是否大于或等于右对象。
	 *
	 * <p>
	 * 基于 {@link #getOrder()} 的返回值进行判断： 当顺序值大于或等于 {@code 0} 时认为左对象大于或等于右对象。
	 *
	 * @return 如果左对象大于或等于右对象则返回 {@code true}，否则返回 {@code false}
	 */
	default boolean isGreaterOrEqual() {
		return getOrder() >= 0;
	}

	/**
	 * 判断左对象是否小于右对象。
	 *
	 * <p>
	 * 基于 {@link #getOrder()} 的返回值进行判断： 当顺序值小于 {@code 0} 时认为左对象小于右对象。
	 *
	 * @return 如果左对象小于右对象则返回 {@code true}，否则返回 {@code false}
	 */
	default boolean isLess() {
		return getOrder() < 0;
	}

	/**
	 * 判断左对象是否小于或等于右对象。
	 *
	 * <p>
	 * 基于 {@link #getOrder()} 的返回值进行判断： 当顺序值小于或等于 {@code 0} 时认为左对象小于或等于右对象。
	 *
	 * @return 如果左对象小于或等于右对象则返回 {@code true}，否则返回 {@code false}
	 */
	default boolean isLessOrEqual() {
		return getOrder() <= 0;
	}
}