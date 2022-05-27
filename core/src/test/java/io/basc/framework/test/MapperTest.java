package io.basc.framework.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Fields;
import io.basc.framework.mapper.MapperUtils;

public class MapperTest {
	@Test
	public void util() {
		A entity = new A();
		Field field = Fields.getFields(A.class).byName("v").first();
		assertTrue(MapperUtils.isExistDefaultValue(field, entity));
		assertFalse(MapperUtils.isExistValue(field, entity));
	}
	
	public static class A{
		private int v;

		public int getV() {
			return v;
		}

		public void setV(int v) {
			this.v = v;
		}
	}
}
