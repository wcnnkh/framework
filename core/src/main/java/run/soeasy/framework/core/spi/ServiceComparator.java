package run.soeasy.framework.core.spi;

import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.comparator.OrderComparator;

/**
 * 服务比较器 继承自OrderComparator，用于服务组件的排序，提供自定义的无法比较时的处理逻辑
 */
@RequiredArgsConstructor
public class ServiceComparator<T> extends OrderComparator<T> {
	private static final ServiceComparator<Object> DEFAULT = new ServiceComparator<>(1);
	private final int unknown;

	/**
	 * 获取服务比较器实例
	 * 
	 * @return 服务比较器实例
	 */
	@SuppressWarnings("unchecked")
	public static <T> ServiceComparator<T> defaultServiceComparator() {
		return (ServiceComparator<T>) DEFAULT;
	}

	/**
	 * 比较两个服务对象
	 * 
	 * @param o1 第一个对象
	 * @param o2 第二个对象
	 * @return 比较结果： 负整数表示o1小于o2， 零表示相等， 正整数表示o1大于o2
	 */
	@Override
	public int compare(T o1, T o2) {
		if (ObjectUtils.equals(o1, o2)) {
			return 0; // 相等对象返回0
		}

		int order = super.compare(o1, o2); // 调用父类比较逻辑
		return order == 0 ? unknown : order; // 父类比较结果为0时返回unknown值
	}
}
