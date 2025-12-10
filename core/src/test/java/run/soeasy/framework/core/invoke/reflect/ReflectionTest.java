package run.soeasy.framework.core.invoke.reflect;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import lombok.ToString;
import run.soeasy.framework.core.RandomUtils;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.mapping.property.Cloner;

public class ReflectionTest {
	@Test
	public void test() {
		Map<String, Object> map = new HashMap<>();
		map.put(RandomUtils.uuid(), RandomUtils.uuid());
		Map<String, Object> cloneMap = Cloner.clone(map, true);
		System.out.println(cloneMap);
		assertTrue(map.equals(cloneMap));
	}

	@Test
	public void cloneA() {
		CloneA cloneA = new CloneA();
		cloneA.a = RandomUtils.uuid();
		CloneA c = cloneA.clone();
		assertTrue(StringUtils.equals(cloneA.a, c.a));
	}

	@ToString
	public static class ParentBean {
		public String pa;
		public transient String pc;
	}

	@ToString
	private static class CloneA implements Cloneable {
		private String a;

		@Override
		public CloneA clone() {
			return Cloner.clone(this, false);
		}
	}
}
