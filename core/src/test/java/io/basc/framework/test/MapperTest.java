package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.basc.framework.json.JsonUtils;
import io.basc.framework.mapper.stereotype.DefaultObjectMapper;
import io.basc.framework.util.XUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

public class MapperTest {
	@Test
	public void util() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("k", XUtils.getUUID());
		map.put("b.bk", XUtils.getUUID());
		map.put("bk", "bk");
		map.put("s.a", XUtils.getUUID());
		map.put("s.b", XUtils.getUUID());
		DefaultObjectMapper mapper = new DefaultObjectMapper();
		A a = mapper.convert(map, A.class);
		System.out.println(JsonUtils.getSupport().toJsonString(map));
		System.out.println(JsonUtils.getSupport().toJsonString(a));
		assertTrue(map.get("k").equals(a.getK()));
		assertTrue(map.get("b.bk").equals(a.getB().getBk()));
		System.out.println(a);
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	public static class A extends B {
		private int v;
		private String k;
		private B b;
		private Map<String, String> s;
	}

	@Data
	public static class B {
		private String bk;
	}
}
