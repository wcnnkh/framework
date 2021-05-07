package scw.orm.test;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import scw.convert.TypeDescriptor;
import scw.env.SystemEnvironment;
import scw.io.Resource;
import scw.orm.annotation.PrimaryKey;

public class XmlResolverTest {
	@SuppressWarnings("unchecked")
	@Test
	public void test() {
		TypeDescriptor mapType = TypeDescriptor.map(Map.class, String.class, TestBean.class);
		TypeDescriptor listType = TypeDescriptor.collection(List.class, TestBean.class);
		Resource resource = SystemEnvironment.getInstance().getResource("test.xml");
		Map<String, TestBean> map = (Map<String, TestBean>) SystemEnvironment.getInstance().resolveResource(resource,
				mapType);
		System.out.println(map);
		List<TestBean> list = (List<TestBean>) SystemEnvironment.getInstance().resolveResource(resource, listType);
		System.out.println(list);

		TypeDescriptor nestedType2 = TypeDescriptor.map(Map.class, String.class, TestBean2.class);
		TypeDescriptor mapType2 = TypeDescriptor.map(Map.class, TypeDescriptor.valueOf(String.class), nestedType2);
		Map<String, Map<String, TestBean>> map2 = (Map<String, Map<String, TestBean>>) SystemEnvironment.getInstance().resolveResource(resource,
				mapType2);
		System.out.println(map2);
	}

	private static final class TestBean {
		@PrimaryKey
		private String id;
		private String name;

		@Override
		public String toString() {
			return "[id=" + id + ", name=" + name + "]";
		}
	}

	private static final class TestBean2 {
		@PrimaryKey
		private String id;
		@PrimaryKey
		private String name;

		@Override
		public String toString() {
			return "[id=" + id + ", name=" + name + "]";
		}
	}
}
