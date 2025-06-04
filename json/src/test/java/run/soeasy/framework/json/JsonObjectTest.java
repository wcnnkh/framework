package run.soeasy.framework.json;

import org.junit.Test;

public class JsonObjectTest {
	@Test
	public void test() {
		JsonObject json = new JsonObject();
		json.put("name", new JsonPrimitive("soeasy.run"));
		System.out.println(json.toJsonString());
	}
}
