package run.soeasy.framework.core.type;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ResolvableTypeTest {

	@Test
	public void testB() {
		GenericSuper genericSuper = new GenericSuper();
		ResolvableType resolvableType = ResolvableType.forType(genericSuper.getClass());
		System.out.println("M:" + resolvableType);
		resolvableType = resolvableType.as(B.class);
		System.out.println("A: " + resolvableType.getTypeName());
		resolvableType = resolvableType.as(A.class);
		assert resolvableType.getActualTypeArgument(0).getRawType() == Integer.class;
		System.out.println("B: " + resolvableType.getTypeName());
		assert resolvableType.getActualTypeArgument(0).getRawType() == Integer.class;
		assert resolvableType.getActualTypeArgument(1).getRawType() == String.class;
	}

	@Test
	public void test() {
		Map<String, Object> map = new HashMap<>();
		ResolvableType resolvableType = ResolvableType.forType(map.getClass());
		resolvableType = resolvableType.as(Map.class);
		System.out.println(resolvableType);
		assert "java.util.Map<K, V>".equals(resolvableType.getTypeName());

		ResolvableType mapType = ResolvableType.forClassWithGenerics(LinkedHashMap.class, String.class,
				ResolvableType.forClassWithGenerics(List.class, String.class));
		System.out.println(mapType);
		ResolvableType asType = mapType.as(Map.class);
		System.out.println(asType);
		assert "java.util.Map<java.lang.String, java.util.List<java.lang.String>>".equals(asType.getTypeName());
		System.out.println(asType.getActualTypeArgument(1));
		assert "java.util.List<java.lang.String>".equals(asType.getActualTypeArgument(1).getTypeName());
		System.out.println(asType.getActualTypeArgument(1, 0));
		assert "java.lang.String".equals(asType.getActualTypeArgument(1, 0).getTypeName());
	}

	private static class A<K, V> {
	}

	private static class B<K> extends A<K, String> {
	}

	private static class GenericSuper extends B<Integer> {
	}
}
