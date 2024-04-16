package io.basc.framework.test;

import java.util.Map.Entry;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;

import io.basc.framework.util.comparator.ClassLoaderComparator;

public class ClassLoaderComparatorTest {

	@Test
	public void test() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		ClassLoader parent = classLoader.getParent();
		System.out.println(classLoader);
		System.out.println(parent);
		if (parent == null) {
			return;
		}
		Assert.assertTrue(ClassLoaderComparator.isParent(classLoader, parent));

		TreeMap<ClassLoader, ClassLoader> treeMap = new TreeMap<>(ClassLoaderComparator.global());
		treeMap.put(parent, parent);
		treeMap.put(classLoader, classLoader);

		System.out.println("开始展示順序");
		for (Entry<ClassLoader, ClassLoader> entry : treeMap.entrySet()) {
			System.out.println(entry.getKey());
		}
		System.out.println("结束展示順序");
	}
}
