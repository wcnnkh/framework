package io.basc.framework.orm.test;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.resolve.ResourceResolvers;
import io.basc.framework.core.convert.support.DefaultConversionService;
import io.basc.framework.io.DefaultResourceLoader;
import io.basc.framework.io.Resource;
import io.basc.framework.orm.stereotype.PrimaryKey;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;

public class XmlResolverTest {
	private static Logger logger = LogManager.getLogger(XmlResolverTest.class);

	@SuppressWarnings("unchecked")
	@Test
	public void test() {
		System.out.println(DefaultConversionService.getInstance());
		TypeDescriptor mapType = TypeDescriptor.map(Map.class, String.class, TestBean.class);
		TypeDescriptor listType = TypeDescriptor.collection(List.class, TestBean.class);
		DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
		Resource resource = resourceLoader.getResource("test.xml");
		ResourceResolvers resourceResolvers = new ResourceResolvers(DefaultConversionService.getInstance());
		Map<String, TestBean> map = (Map<String, TestBean>) resourceResolvers.resolveResource(resource, mapType);
		logger.info(map.toString());
		assertTrue(map.size() == 3);
		List<TestBean> list = (List<TestBean>) resourceResolvers.resolveResource(resource, listType);
		logger.info(list.toString());
		assertTrue(list.size() == 3);

		TypeDescriptor nestedType2 = TypeDescriptor.map(Map.class, String.class, TestBean2.class);
		TypeDescriptor mapType2 = TypeDescriptor.map(Map.class, TypeDescriptor.valueOf(String.class), nestedType2);
		Map<String, Map<String, TestBean>> map2 = (Map<String, Map<String, TestBean>>) resourceResolvers
				.resolveResource(resource, mapType2);
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
