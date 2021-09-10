package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import java.util.TreeSet;

import org.junit.Test;

import io.basc.framework.util.comparator.TypeComparator;

public class TypeComparatorTest {
	@Test
	public void test() {
		TreeSet<Class<?>> set = new TreeSet<Class<?>>(new TypeComparator());
		set.add(Object.class);
		set.add(Integer.class);
		set.add(Number.class);
		System.out.println(set);
		assertTrue(set.first() == Integer.class);
		assertTrue(set.last() == Object.class);
	}
}
