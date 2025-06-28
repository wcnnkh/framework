package run.soeasy.framework.core.type;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ResolvableTypeTest {
	@Test
	public void test() {
		Map<String, Object> map = new HashMap<>();
		ResolvableType resolvableType = ResolvableType.forType(map.getClass());
		resolvableType = resolvableType.as(Map.class);
		System.out.println(resolvableType);
		
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
}
