package io.basc.framework.util.comparator;

import java.util.Comparator;

import io.basc.framework.util.ClassUtils;

public class TypeComparator implements Comparator<Class<?>> {

	public static final TypeComparator DEFAULT = new TypeComparator();

	/**
	 * 大类型在小类型之后 Integer -> Number -> Object
	 */
	@Override
	public int compare(Class<?> o1, Class<?> o2) {
		if (o1.equals(o2)) {
			return 0;
		}
		if (ClassUtils.isAssignable(o1, o2)) {
			return 1;
		} else if (ClassUtils.isAssignable(o2, o1)) {
			return -1;
		}
		return -1;
	}

}
