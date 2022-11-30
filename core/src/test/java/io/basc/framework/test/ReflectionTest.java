package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Map;

import org.junit.Test;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.io.ClassPathResource;
import io.basc.framework.util.CollectionFactory;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;

public class ReflectionTest {
	@Test
	public void test() {
		TestBean bean = new TestBean();
		bean.a = XUtils.getUUID();
		bean.b = new String[] { XUtils.getUUID() };
		bean.c = "sss";
		bean.d = bean;
		String a = bean.toString();
		System.out.println(a);
		TestBean clone = ReflectionUtils.clone(bean, true);
		String b = clone.toString();
		System.out.println(b);
		assertTrue(a.equals(b));
		assertTrue(ReflectionUtils.equals(a, b));
		assertTrue(ReflectionUtils.hashCode(a) == ReflectionUtils.hashCode(b));

		Map<String, Object> map = Collections.singletonMap(XUtils.getUUID(), XUtils.getUUID());
		Map<String, Object> cloneMap = CollectionFactory.clone(map, true);
		assertTrue(map.equals(cloneMap));

		assertTrue(ReflectionUtils.getConstructor("io.basc.framework.io.WatchServiceResourceEventDispatcher",
				null, ClassPathResource.class) != null);
	}

	@Test
	public void cloneA() {
		CloneA cloneA = new CloneA();
		cloneA.a = XUtils.getUUID();

		CloneA c = cloneA.clone();
		assertTrue(StringUtils.equals(cloneA.a, c.a));
	}

	public static class ParentBean {
		public String pa;
		public transient String pc;

		@Override
		public String toString() {
			return ReflectionUtils.toString(this);
		}
	}

	public static class TestBean extends ParentBean {
		public String a;
		public Object b;
		public transient String c;
		public TestBean d;

		public String toString() {
			return ReflectionUtils.toString(this);
		}
	}

	private static class CloneA implements Cloneable {
		private String a;

		@Override
		public CloneA clone() {
			return ReflectionUtils.clone(this);
		}
	}
}
