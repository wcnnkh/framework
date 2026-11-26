package run.soeasy.framework.core.collection;

import lombok.NonNull;

public interface ContainerKit<C> {
	/**
	 * 默认初始容量，用于创建集合实例。 此值参考了常见集合实现的默认初始容量。
	 */
	public static final int DEFAULT_INITIAL_CAPACITY = 16;

	/**
	 * 默认负载因子，用于创建集合实例。 此值参考了哈希表等实现的默认负载因子，平衡了空间和时间效率。
	 */
	public static final float DEFAULT_LOAD_FACTOR = 0.75f;

	default C create() {
		return create(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * 将源对象转换为适合对外展示的形式。 该方法通常用于将内部业务对象转换为前端展示或外部接口使用的数据格式， 可能涉及数据脱敏、格式转换或字段筛选等操作。
	 *
	 * @param source 源对象，不可为null
	 * @return 转换后的展示对象
	 */
	C display(@NonNull C source);

	/**
	 * 克隆一个对象。 该方法创建一个与源对象内容相同的新对象，通常用于对象的备份或并发处理， 实现应保证克隆对象与源对象相互独立，修改不会相互影响。
	 *
	 * @param source 源对象，不可为null
	 * @return 克隆的对象
	 */
	C clone(@NonNull C source);

	/**
	 * 使用指定的初始容量和负载因子创建集合实例。 
	 * 注意：对于某些不使用负载因子的集合实现，该参数可能被忽略。
	 *
	 * @param initialCapacity 初始容量，必须大于0
	 * @param loadFactor      负载因子，必须大于0
	 * @return 新创建的集合实例
	 */
	C create(int initialCapacity, float loadFactor);
}
