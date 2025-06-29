package run.soeasy.framework.core.comparator;

import java.util.Comparator;
import java.util.Iterator;

import run.soeasy.framework.core.collection.ArrayUtils;

/**
 * 有序对象比较器 用于对实现了Ordered接口的对象进行排序，支持优先级排序和源对象排序
 * 
 * 排序规则： 1. PriorityOrdered对象优先于普通Ordered对象 2. 按order值升序排列（order值越小优先级越高） 3.
 * 非Ordered对象默认排在最后（order值为DEFAULT_PRECEDENCE）
 *
 * @author soeasy.run
 */
public class OrderComparator<T> implements Comparator<T> {

	/**
	 * 共享的默认实例
	 */
	public static final OrderComparator<Object> DEFAULT = new OrderComparator<>();

	/**
	 * 创建带有源提供者的适配比较器
	 * 
	 * @param sourceProvider 排序源提供者
	 * @return 适配后的比较器
	 */
	public Comparator<Object> withSourceProvider(OrderSourceProvider sourceProvider) {
		return (o1, o2) -> doCompare(o1, o2, sourceProvider);
	}

	/**
	 * 比较两个对象的顺序
	 */
	@Override
	public int compare(T o1, T o2) {
		return doCompare(o1, o2, null);
	}

	/**
	 * 执行实际的比较逻辑
	 * 
	 * @param o1             第一个对象
	 * @param o2             第二个对象
	 * @param sourceProvider 排序源提供者
	 * @return 比较结果
	 */
	private int doCompare(Object o1, Object o2, OrderSourceProvider sourceProvider) {
		int i1 = getOrder(o1, sourceProvider);
		int i2 = getOrder(o2, sourceProvider);
		return Integer.compare(i1, i2);
	}

	/**
	 * 获取对象的顺序值，支持从源提供者获取
	 * 
	 * @param obj            对象
	 * @param sourceProvider 排序源提供者
	 * @return 顺序值，默认为DEFAULT_PRECEDENCE
	 */
	private int getOrder(Object obj, OrderSourceProvider sourceProvider) {
		Integer order = null;
		if (obj != null && sourceProvider != null) {
			Object orderSource = sourceProvider.getOrderSource(obj);
			if (orderSource != null) {
				if (orderSource.getClass().isArray()) {
					// 处理数组类型的排序源
					Iterator<Object> iterator = ArrayUtils.stream(orderSource).iterator();
					while (iterator.hasNext()) {
						Object source = iterator.next();
						order = findOrder(source);
						if (order != null) {
							break;
						}
					}
				} else {
					// 处理单一排序源
					order = findOrder(orderSource);
				}
			}
		}
		return (order != null ? order : getOrder(obj));
	}

	/**
	 * 获取对象的顺序值（默认实现）
	 * 
	 * @param obj 对象
	 * @return 顺序值，默认为DEFAULT_PRECEDENCE
	 */
	protected int getOrder(Object obj) {
		if (obj != null) {
			Integer order = findOrder(obj);
			if (order != null) {
				return order;
			}
		}
		return Ordered.DEFAULT_PRECEDENCE;
	}

	/**
	 * 查找对象的顺序值
	 * 
	 * @param obj 对象
	 * @return 顺序值，若对象实现Ordered接口则返回其order值
	 */
	protected Integer findOrder(Object obj) {
		return (obj instanceof Ordered ? ((Ordered) obj).getOrder() : null);
	}

	/**
	 * 获取对象的优先级值（默认返回null）
	 * 
	 * @param obj 对象
	 * @return 优先级值
	 */
	public Integer getPriority(Object obj) {
		return null;
	}
}