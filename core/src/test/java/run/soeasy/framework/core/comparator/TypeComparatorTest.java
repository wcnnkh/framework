package run.soeasy.framework.core.comparator;

import static org.junit.Assert.assertTrue;

import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Test;

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
		
		TreeMap<Class<?>, Object> map = new TreeMap<>(new TypeComparator());
		map.put(Object.class, 1);
		map.put(Number.class, 2);
		map.put(Integer.class, 3);
		map.put(int.class, 4);
		System.out.println(map);
		Object value = map.get(int.class);
		System.out.println(value);
	}
}
