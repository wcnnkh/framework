package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import io.basc.framework.beans.BeanUtils;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.util.StopWatch;
import lombok.ToString;

public class CopyTest {
	@Test
	public void copyTest() {
		assertTrue(ResolvableType.forClass(List.class)
				.isAssignableFrom(ResolvableType.forClassWithGenerics(List.class, String.class)));
		assertTrue(Object.class.isAssignableFrom(String.class));

		List<String> list = new ArrayList<>();
		list.add("a");
		Target target = new Target();
		Source source = new Source();
		source.setList(list);
		System.out.println(source);
		StopWatch stopWatch = new StopWatch("copy test");
		stopWatch.start("first copy");
		BeanUtils.copy(source, target);
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
		BeanUtils.copy(source, target, true);
		stopWatch.stop();
		System.out.println(target);
		assertTrue(target.getList().size() == source.getList().size());
		target.getList().clear();
		System.out.println(source);
		assertTrue(target.getList().size() != source.getList().size());
		System.out.println(stopWatch.prettyPrint());
	}

	@ToString(callSuper = true)
	public static class Target {
		private List<?> list;

		public List<?> getList() {
			return list;
		}

		public void setList(List<?> list) {
			this.list = list;
		}
	}

	@ToString(callSuper = true)
	public static class Source {
		private List<String> list;

		public List<String> getList() {
			return list;
		}

		public void setList(List<String> list) {
			this.list = list;
		}
	}
}
