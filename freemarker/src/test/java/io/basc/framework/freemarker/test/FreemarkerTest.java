package io.basc.framework.freemarker.test;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import freemarker.template.Configuration;
import freemarker.template.Template;
import io.basc.framework.freemarker.EnvConfiguration;
import io.basc.framework.json.JSONUtils;
import io.basc.framework.json.JsonObject;

public class FreemarkerTest {
	@Test
	public void test() throws Exception {
		Configuration configuration = new EnvConfiguration();
		Template template = configuration.getTemplate("test.ftl");
		Map<String, Object> map = new HashMap<>();
		map.put("a", "aaa");
		map.put("b", "bbb");

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		template.process(map, new OutputStreamWriter(output, configuration.getDefaultEncoding()));
		String content = new String(output.toByteArray(), configuration.getDefaultEncoding());
		System.out.println(content);

		JsonObject jsonObject = JSONUtils.getJsonSupport().parseObject(content);
		assertTrue(jsonObject.size() == 2);
	}
}
