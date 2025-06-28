package run.soeasy.framework.core.convert;

import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Test;

public class TypeMappingTest {
	@Test
	public void test() {
		TreeSet<TypeMapping> mappings = new TreeSet<>();
		mappings.add(new TypeMapping(String.class, Object.class));
		mappings.add(new TypeMapping(String.class, String.class));
		mappings.add(new TypeMapping(Object.class, Object.class));
		mappings.add(new TypeMapping(Object.class, String.class));
		mappings.forEach((e) -> System.out.println(e));
		System.out.println("-----------------------");
		Map<TypeMapping, Integer> map = new TreeMap<>();
		map.put(new TypeMapping(String.class, Object.class), 1);
		map.put(new TypeMapping(String.class, String.class), 2);
		map.put(new TypeMapping(Object.class, Object.class), 3);
		map.put(new TypeMapping(Object.class, String.class), 4);
		map.forEach((k, v) -> System.out.println(k + "=" + v));
	}
}
