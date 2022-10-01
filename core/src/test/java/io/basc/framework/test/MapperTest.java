package io.basc.framework.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.json.JSONUtils;
import io.basc.framework.mapper.DefaultObjectMapper;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Fields;
import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.mapper.Structure;
import io.basc.framework.util.XUtils;

public class MapperTest {
	@Test
	public void util() {
		A entity = new A();
		Field field = Fields.getFields(A.class).byName("v").first();
		assertTrue(MapperUtils.isExistDefaultValue(field, entity));
		assertFalse(MapperUtils.isExistValue(field, entity));

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("k", XUtils.getUUID());
		map.put("b.bk", XUtils.getUUID());
		map.put("bk", "bk");
		map.put("s.a", XUtils.getUUID());
		map.put("s.b", XUtils.getUUID());
		DefaultObjectMapper<Object, RuntimeException> mapper = new DefaultObjectMapper<>();
		A a = mapper.convert(map, A.class);
		System.out.println(JSONUtils.toJSONString(map));
		System.out.println(JSONUtils.toJSONString(a));
		assertTrue(map.get("k").equals(a.getK()));
		assertTrue(map.get("b.bk").equals(a.getB().getBk()));
		System.out.println(a);

		Structure<? extends Field> structure = mapper.getStructure(A.class);
		Field firstField = structure.first();
		structure = structure.setParentField(firstField);
		structure = structure.setNameNestingDepth(1);
		structure.forEach((e) -> assertTrue(e.getName().startsWith(firstField.getName())));
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
