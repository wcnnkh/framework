package run.soeasy.framework.core.domain;

/**
 * 可变数值容器抽象类，继承自{@link NumberValue}，提供数值的增量修改、重置等操作。
 * 该类专为需要频繁修改数值的场景设计（如计数器、累加器），通过提供递增、递减、批量添加等方法， 简化可变数值的操作逻辑。
 *
 * <p>
 * 核心特性：
 * <ul>
 * <li>支持数值的增量修改（通过{@link #add(long)}方法）</li>
 * <li>提供便捷的递增（{@link #increment()}）和递减（{@link #decrement()}）操作</li>
 * <li>支持数值重置为初始状态（通过{@link #reset()}方法）</li>
 * <li>继承{@link NumberValue}的所有高精度计算和类型转换能力</li>
 * </ul>
 *
 * <p>
 * 使用场景：
 * <ul>
 * <li>计数器：如请求次数统计、循环迭代计数</li>
 * <li>累加器：如金额汇总、数据求和</li>
 * <li>需要频繁修改数值的业务逻辑</li>
 * </ul>
 */
public abstract class NumberAdder extends NumberValue {
	private static final long serialVersionUID = 1L;

	/**
	 * 将数值重置为初始状态（通常为0）。 子类需实现此方法，定义具体的重置逻辑。
	 */
	public abstract void reset();
	
	/**
	 * 向当前数值添加一个long类型的值。 子类需实现此方法，定义具体的增量操作逻辑。
	 *
	 * @param value 要添加的long类型数值
	 */
	public abstract void add(long value);

	/**
	 * 数值递增1（等价于{@code add(1L)}）。
	 */
	public void increment() {
		add(1L);
	}

	/**
	 * 数值递减1（等价于{@code add(-1L)}）。
	 */
	public void decrement() {
		add(1L);
	}
}
