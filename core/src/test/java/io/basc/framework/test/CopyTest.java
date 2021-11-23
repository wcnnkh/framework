package io.basc.framework.test;

import io.basc.framework.core.ResolvableType;
import io.basc.framework.mapper.Copy;
import io.basc.framework.mapper.MapperUtils;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class CopyTest {
	@Test
	public void copyTest() {
		System.out.println(ResolvableType.forClass(List.class).isAssignableFrom(ResolvableType.forClassWithGenerics(List.class, String.class)));
		System.out.println(Object.class.isAssignableFrom(String.class));
		
		List<String> list = new ArrayList<>();
		list.add("a");
		Target target = new Target();
		Source source = new Source();
		source.setList(list);
		System.out.println(source);
		Copy.copy(source, target);
		System.out.println(target);
		assertTrue(target.getList().size() == source.getList().size());
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
			return MapperUtils.toString(this);
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
			return MapperUtils.toString(this);
		}
	}
}
