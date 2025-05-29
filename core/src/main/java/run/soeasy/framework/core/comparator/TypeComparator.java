package run.soeasy.framework.core.comparator;

import java.util.Comparator;

import run.soeasy.framework.core.ClassUtils;

public class TypeComparator implements Comparator<Class<?>> {

	public static final TypeComparator DEFAULT = new TypeComparator();

	/**
	 * 大类型在小类型之后
	 */
	@Override
	public int compare(Class<?> o1, Class<?> o2) {
		if (o1 == o2 || o1.equals(o2)) {
			return 0;
		}
		if (ClassUtils.isAssignable(o1, o2)) {
			return 1;
		} else if (ClassUtils.isAssignable(o2, o1)) {
			return -1;
		}
		// 注意，这里只能返回1
		return 1;
	}

}
