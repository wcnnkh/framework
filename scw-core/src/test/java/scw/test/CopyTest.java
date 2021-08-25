package scw.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import scw.mapper.Copy;
import scw.mapper.MapperUtils;

public class CopyTest {
	@Test
	public void copyTest() {
		System.out.println(Object.class.isAssignableFrom(String.class));
		
		List<String> list = new ArrayList<>();
		list.add("a");
		Target target = new Target();
		Source source = new Source();
		source.setList(list);
		System.out.println(source);
		Copy.copy(target, source);
		System.out.println(target);
	}

	public static class Target {
		private List<Object> list;

		public List<Object> getList() {
			return list;
		}

		public void setList(List<Object> list) {
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
