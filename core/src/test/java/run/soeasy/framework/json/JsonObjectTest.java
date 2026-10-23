package run.soeasy.framework.json;

import org.junit.Test;

public class JsonObjectTest {
	@Test
	public void test() {
		JsonObjectContainer json = new JsonObjectContainer();
		json.register("name", new JsonValue("soeasy.run", JsonToken.STRING));
		System.out.println(json.toJsonString());
	}
}
