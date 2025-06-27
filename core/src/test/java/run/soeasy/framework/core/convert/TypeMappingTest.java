package run.soeasy.framework.core.convert;

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
	}
}
