package io.basc.framework.yaml.test;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;

import io.basc.framework.lang.Constants;
import io.basc.framework.util.io.Resource;
import io.basc.framework.util.io.ResourceUtils;
import io.basc.framework.yaml.YamlProcessor;

public class YamlTest {
	@Test
	public void test() {
		Resource resource = ResourceUtils.getSystemResource("test.yaml");
		YamlProcessor yamlProcessor = new YamlProcessor();
		Properties properties = new Properties();
		yamlProcessor.resolveProperties(properties, resource, Constants.UTF_8);
		System.out.println(properties);
		// 结果一致
		assertEquals("hello", properties.getProperty("test2.str"));
		// 类型一致
		assertEquals(111, properties.get("test.number"));
	}
}
