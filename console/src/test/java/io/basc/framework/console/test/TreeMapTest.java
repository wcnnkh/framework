package io.basc.framework.console.test;

import static org.junit.Assert.assertTrue;

import java.util.TreeMap;

import org.junit.Test;

import io.basc.framework.util.XUtils;
import io.basc.framework.util.collect.CollectionUtils;

public class TreeMapTest {
	@Test
	public void test() {
		TreeMap<String, String> map = new TreeMap<String, String>((e1, e2) -> e2.compareTo(e1));
		for (int i = 0; i < 100; i++) {
			String value = XUtils.getUUID();
			map.put(value, value);
		}
		assertTrue(CollectionUtils.equals(map.keySet(), map.values()));
	}
}
