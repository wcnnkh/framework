package run.soeasy.framework.util.collections;

import static org.junit.Assert.assertTrue;

import java.util.Objects;
import java.util.TreeMap;

import org.junit.Test;

import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.sequences.uuid.UUIDSequences;

public class CollectionUtilsTest {
	@Test
	public void test() {
		TreeMap<String, String> map = new TreeMap<String, String>((e1, e2) -> e2.compareTo(e1));
		for (int i = 0; i < 100; i++) {
			String value = UUIDSequences.global().next();
			map.put(value, value);
		}
		assertTrue(CollectionUtils.equals(map.keySet(), map.values(), Objects::equals));
	}
}
