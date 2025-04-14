package run.soeasy.framework.test;

import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Map;

import org.junit.Test;

import lombok.ToString;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.reflect.ReflectionUtils;
import run.soeasy.framework.sequences.uuid.UUIDSequences;

public class ReflectionTest {
	@Test
	public void test() {
		TestBean bean = new TestBean();
		bean.a = UUIDSequences.global().next();
		bean.b = new String[] { UUIDSequences.global().next() };
		bean.c = "sss";
		bean.d = bean;
		String a = bean.toString();
		System.out.println(a);
		TestBean clone = ReflectionUtils.clone(bean, true);
		String b = clone.toString();
		System.out.println(b);
		assertTrue(a.equals(b));
		assertTrue(ReflectionUtils.getDeclaredFields(a.getClass()).equals(a, b));
		assertTrue(ReflectionUtils.getDeclaredFields(a.getClass()).hashCode(a) == ReflectionUtils
				.getDeclaredFields(b.getClass()).hashCode(b));

		Map<String, Object> map = Collections.singletonMap(UUIDSequences.global().next(),
				UUIDSequences.global().next());
		Map<String, Object> cloneMap = CollectionUtils.clone(map, true);
		assertTrue(map.equals(cloneMap));
	}

	@Test
	public void cloneA() {
		CloneA cloneA = new CloneA();
		cloneA.a = UUIDSequences.global().next();

		CloneA c = cloneA.clone();
		assertTrue(StringUtils.equals(cloneA.a, c.a));
	}

	@ToString
	public static class ParentBean {
		public String pa;
		public transient String pc;
	}

	public static class TestBean extends ParentBean {
		public String a;
		public Object b;
		public transient String c;
		public TestBean d;

		@Override
		public String toString() {
			// 测试死循环的处理
			return ReflectionUtils.getDeclaredFields(TestBean.class).all().toString(this);
		}
	}

	@ToString
	private static class CloneA implements Cloneable {
		private String a;

		@Override
		public CloneA clone() {
			return ReflectionUtils.getDeclaredFields(CloneA.class).all().clone(this);
		}
	}
}
