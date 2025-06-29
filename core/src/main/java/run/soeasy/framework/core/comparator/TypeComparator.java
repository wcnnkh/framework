package run.soeasy.framework.core.comparator;

import java.util.Comparator;

import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.type.ClassUtils;

/**
 * 类型比较器 用于比较两个Java类的继承或实现关系，确定它们的顺序
 * 
 * 比较规则： 1. 相等的类返回0 2. 若o1是o2的子类或实现类，o1"大于"o2，返回1 3. 若o2是o1的子类或实现类，o1"小于"o2，返回-1
 * 4. 没有继承关系时返回unknown参数指定的值
 * 
 * @author soeasy.run
 */
@RequiredArgsConstructor
public class TypeComparator implements Comparator<Class<?>> {
	public static final TypeComparator DEFAULT = new TypeComparator(-1);

	/**
	 * 当两个类没有继承关系时返回的值
	 */
	private final int unknown;

	/**
	 * 比较两个Java类的顺序
	 * 
	 * @param o1 第一个类
	 * @param o2 第二个类
	 * @return 负整数表示o1小于o2，零表示相等，正整数表示o1大于o2
	 */
	@Override
	public int compare(Class<?> o1, Class<?> o2) {
		if (o1 == o2 || o1.equals(o2)) {
			return 0; // 相同类返回0
		}

		if (ClassUtils.isAssignable(o1, o2)) {
			return 1; // o1是o2的子类或实现类，o1"大于"o2
		} else if (ClassUtils.isAssignable(o2, o1)) {
			return -1; // o2是o1的子类或实现类，o1"小于"o2
		}

		return unknown; // 没有继承关系时返回指定值
	}
}