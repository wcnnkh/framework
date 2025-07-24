package run.soeasy.framework.core.invoke.reflect;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import lombok.ToString;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.transform.property.Cloner;
import run.soeasy.framework.sequences.UUIDSequence;

public class ReflectionTest {
	@Test
	public void test() {
		Map<String, Object> map = new HashMap<>();
		map.put(UUIDSequence.random().next(), UUIDSequence.random().next());
		Map<String, Object> cloneMap = Cloner.clone(map, true);
		System.out.println(cloneMap);
		assertTrue(map.equals(cloneMap));
	}

	@Test
	public void cloneA() {
		CloneA cloneA = new CloneA();
		cloneA.a = UUIDSequence.random().next();
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
