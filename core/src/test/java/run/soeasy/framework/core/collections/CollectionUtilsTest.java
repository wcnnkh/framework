package run.soeasy.framework.core.collections;

import static org.junit.Assert.assertTrue;

import java.util.Objects;
import java.util.TreeMap;

import org.junit.Test;

import run.soeasy.framework.core.RandomUtils;
import run.soeasy.framework.core.collection.CollectionUtils;

public class CollectionUtilsTest {
	@Test
	public void test() {
		TreeMap<String, String> map = new TreeMap<String, String>((e1, e2) -> e2.compareTo(e1));
		for (int i = 0; i < 100; i++) {
			String value = RandomUtils.uuid();
			map.put(value, value);
		}
		assertTrue(CollectionUtils.equalsInOrder(map.keySet(), map.values(), Objects::equals));
	}
}
