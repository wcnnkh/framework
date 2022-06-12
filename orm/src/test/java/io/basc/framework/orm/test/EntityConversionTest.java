package io.basc.framework.orm.test;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.basc.framework.orm.support.OrmUtils;

public class EntityConversionTest {
	@Test
	public void test() {
		Map<String, Object> map = new HashMap<>();
		map.put("a", 1);
		map.put("b", 2);
		TestnEntity entity = new TestnEntity();
		OrmUtils.getMapper().transform(map, entity);
		assertTrue(entity.getA() == 1);
		assertTrue(entity.getB() == 2);
	}
}
