package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.mapper.Copy;
import io.basc.framework.util.StopWatch;

public class CopyTest {
	@Test
	public void copyTest() {
		System.out.println(ResolvableType.forClass(List.class)
				.isAssignableFrom(ResolvableType.forClassWithGenerics(List.class, String.class)));
		System.out.println(Object.class.isAssignableFrom(String.class));

		List<String> list = new ArrayList<>();
		list.add("a");
		Target target = new Target();
		Source source = new Source();
		source.setList(list);
		System.out.println(source);
		StopWatch stopWatch = new StopWatch("copy test");
		stopWatch.start("first copy");
		Copy.copy(source, target);
		stopWatch.stop();
		System.out.println(stopWatch.toString());
		
		System.out.println(target);
		assertTrue(target.getList().size() == source.getList().size());
		target.getList().clear();
		// 浅拷贝的会一起清空
		assertTrue(source.getList().size() == 0);
		List<String> deepList = new ArrayList<>();
		deepList.add("b");
		source.setList(deepList);
		stopWatch.start();
		Copy.DEEP.copy(Source.class, source, null, Target.class, target, null);
		stopWatch.stop();
		System.out.println(target);
		assertTrue(target.getList().size() == source.getList().size());
		target.getList().clear();
		System.out.println(source);
		assertTrue(target.getList().size() != source.getList().size());
		System.out.println(stopWatch.prettyPrint());
	}

	public static class Target {
		private List<?> list;

		public List<?> getList() {
			return list;
		}

		public void setList(List<?> list) {
			this.list = list;
		}

		@Override
		public String toString() {
			return ReflectionUtils.toString(this);
		}
	}

	public static class Source {
		private List<String> list;

		public List<String> getList() {
			return list;
		}

		public void setList(List<String> list) {
			this.list = list;
		}

		@Override
		public String toString() {
			return ReflectionUtils.toString(this);
		}
	}
}
