package io.basc.framework.util.comparator;

import java.util.Comparator;

import io.basc.framework.util.Assert;

public class ClassLoaderComparator<T extends ClassLoader> implements Comparator<T> {
	private static final ClassLoaderComparator<?> GLOBAL = new ClassLoaderComparator<>();

	@SuppressWarnings("unchecked")
	public static <T extends ClassLoader> ClassLoaderComparator<T> global() {
		return (ClassLoaderComparator<T>) GLOBAL;
	}

	/**
	 * 判断parentClassLoader是否是classLoader的父加载器
	 * 
	 * @param classLoader
	 * @param parentClassLoader
	 * @return
	 */
	public static boolean isParent(ClassLoader classLoader, ClassLoader parentClassLoader) {
		Assert.requiredArgument(classLoader != null, "classLoader");
		ClassLoader current = parentClassLoader;
		while (current != null) {
			ClassLoader parent = classLoader.getParent();
			while (parent != null) {
				if (parent == current) {
					return true;
				}
				parent = parent.getParent();
			}
			current = current.getParent();
		}
		return false;
	}

	@Override
	public int compare(T o1, T o2) {
		if (o1 == null) {
			if (o2 == null) {
				return 0;
			} else {
				return -1;
			}
		} else {
			if (o2 == null) {
				return 1;
			} else {
				if (o1 == o2 || o1.equals(o2)) {
					return 0;
				}

				/**
				 * 父加载器(应用范围小)应该放在最后
				 */
				return isParent(o2, o1) ? -1 : 1;
			}
		}

	}

}
