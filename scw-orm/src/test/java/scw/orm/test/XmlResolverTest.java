package scw.orm.test;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import scw.convert.TypeDescriptor;
import scw.env.Sys;
import scw.io.Resource;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.orm.annotation.PrimaryKey;

public class XmlResolverTest {
	private static Logger logger = LoggerFactory.getLogger(XmlResolverTest.class);
	
	@SuppressWarnings("unchecked")
	@Test
	public void test() {
		TypeDescriptor mapType = TypeDescriptor.map(Map.class, String.class, TestBean.class);
		TypeDescriptor listType = TypeDescriptor.collection(List.class, TestBean.class);
		Resource resource = Sys.env.getResource("test.xml");
		Map<String, TestBean> map = (Map<String, TestBean>) Sys.env.getResourceResolver().resolveResource(resource,
				mapType);
		logger.info(map.toString());
		assertTrue(map.size() == 3);
		List<TestBean> list = (List<TestBean>) Sys.env.getResourceResolver().resolveResource(resource, listType);
		logger.info(list.toString());
		assertTrue(list.size() == 3);

		TypeDescriptor nestedType2 = TypeDescriptor.map(Map.class, String.class, TestBean2.class);
		TypeDescriptor mapType2 = TypeDescriptor.map(Map.class, TypeDescriptor.valueOf(String.class), nestedType2);
		Map<String, Map<String, TestBean>> map2 = (Map<String, Map<String, TestBean>>) Sys.env.getResourceResolver().resolveResource(resource,
				mapType2);
		logger.info(map2.toString());
		assertTrue(map2.size() == 3);
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
