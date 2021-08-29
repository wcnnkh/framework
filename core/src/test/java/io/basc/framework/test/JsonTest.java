package io.basc.framework.test;

import io.basc.framework.json.JSONUtils;
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
		
		String content = JSONUtils.getJsonSupport().toJSONString(list);
		System.out.println(content);
		
		JsonArray jsonArray = JSONUtils.getJsonSupport().parseArray(content);
		List<TestJsonObjectWrapper> wrappers = jsonArray.convert(TestJsonObjectWrapper.class);
		System.out.println(wrappers);
		
		List<TestInfo> testInfo = JSONUtils.getJsonSupport().parseArray(content).convert(TestInfo.class);
		System.out.println(testInfo);
	}
	
	public static class TestInfo{
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
	
	public static class TestJsonObjectWrapper extends JsonObjectWrapper{

		public TestJsonObjectWrapper(JsonObject target) {
			super(target);
		}
		
	}
}
