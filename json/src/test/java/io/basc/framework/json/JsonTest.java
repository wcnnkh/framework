package io.basc.framework.json;

import io.basc.framework.json.JsonUtils;
import io.basc.framework.json.JsonArray;
import io.basc.framework.json.JsonObject;
import io.basc.framework.json.JsonObjectWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class JsonTest {

	@Test
	public void test() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("key1", "value1");
		map.put("key2", "asss");

		List<Object> list = new ArrayList<Object>();
		list.add(map);
		list.add(map);

		String content = JsonUtils.getSupport().toJsonString(list);
		System.out.println(content);

		JsonArray jsonArray = JsonUtils.getSupport().parseArray(content);
		List<TestJsonObjectWrapper> wrappers = jsonArray.convert(TestJsonObjectWrapper.class);
		System.out.println(wrappers);

		List<TestInfo> testInfo = JsonUtils.getSupport().parseArray(content).convert(TestInfo.class);
		System.out.println(testInfo);
	}

	public static class TestInfo {
		private String id;
		private String value;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	public static class TestJsonObjectWrapper extends JsonObjectWrapper {

		public TestJsonObjectWrapper(JsonObject target) {
			super(target);
		}

	}
}
