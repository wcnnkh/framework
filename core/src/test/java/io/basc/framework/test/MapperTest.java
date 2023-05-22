package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.json.JsonUtils;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.support.DefaultObjectMapper;
import io.basc.framework.util.XUtils;

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

		Mapping<? extends Field> structure = mapper.getMapping(A.class);
		Field firstField = structure.getElements().first();
		structure = structure.setParentField(firstField);
		structure = structure.setNameNestingDepth(1);
		structure.getElements().convert((e) -> e.limit(1))
				.forEach((e) -> assertTrue(e.getName().startsWith(firstField.getName())));
	}

	public static class A extends B {
		private int v;
		private String k;
		private B b;
		private Map<String, String> s;

		public int getV() {
			return v;
		}

		public void setV(int v) {
			this.v = v;
		}

		public String getK() {
			return k;
		}

		public void setK(String k) {
			this.k = k;
		}

		public B getB() {
			return b;
		}

		public void setB(B b) {
			this.b = b;
		}

		public Map<String, String> getS() {
			return s;
		}

		public void setS(Map<String, String> s) {
			this.s = s;
		}

		@Override
		public String toString() {
			return ReflectionUtils.toString(this);
		}
	}

	public static class B {
		private String bk;

		public String getBk() {
			return bk;
		}

		public void setBk(String bk) {
			this.bk = bk;
		}

		@Override
		public String toString() {
			return ReflectionUtils.toString(this);
		}
	}
}
