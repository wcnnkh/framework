package scw.test;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import scw.convert.TypeDescriptor;
import scw.convert.support.CollectionToMapConversionService;
import scw.env.SystemEnvironment;
import scw.io.Resource;

public class XmlResolverTest {
	@SuppressWarnings("unchecked")
	@Test
	public void test() {
		CollectionToMapConversionService conversionService = new CollectionToMapConversionService(
				SystemEnvironment.getInstance(),
				CollectionToMapConversionService.FIRST_FIELD);
		;
		Resource resource = SystemEnvironment.getInstance().getResource(
				"test.xml");
		TypeDescriptor mapType = TypeDescriptor.map(Map.class, String.class,
				TestBean.class);
		Map<String, TestBean> map = (Map<String, TestBean>) conversionService
				.convert(resource, TypeDescriptor.forObject(resource), mapType);
		System.out.println(map);

		List<TestBean> list = (List<TestBean>) SystemEnvironment.getInstance()
				.resolveResource(resource,
						TypeDescriptor.collection(List.class, TestBean.class));
		System.out.println(list);
	}

	private static final class TestBean {
		private String id;
		private String name;

		@Override
		public String toString() {
			return "[id=" + id + ", name=" + name + "]";
		}
	}
}
