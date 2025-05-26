package io.basc.framework.beans;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import lombok.ToString;
import run.soeasy.framework.beans.BeanUtils;
import run.soeasy.framework.core.lang.ResolvableType;

public class BeanUtilsTest {
	@Test
	public void copyTest() {
		assertTrue(ResolvableType.forType(List.class)
				.isAssignableFrom(ResolvableType.forClassWithGenerics(List.class, String.class)));
		assertTrue(Object.class.isAssignableFrom(String.class));

		List<String> list = new ArrayList<>();
		list.add("a");
		Target target = new Target();
		Source source = new Source();
		source.setList(list);
		System.out.println(source);
		BeanUtils.copyProperties(source, target);

		System.out.println(target);
		assertTrue(target.getList().size() == source.getList().size());
		target.getList().clear();
		// 浅拷贝的会一起清空
		assertTrue(source.getList().size() == 0);
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
