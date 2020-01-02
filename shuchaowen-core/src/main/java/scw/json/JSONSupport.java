package scw.json;

import java.lang.reflect.Type;

public interface JSONSupport {
	String toJSONString(Object obj);

	JsonArray parseArray(String text);
	
	JsonObject parseObject(String text);

	<T> T parseObject(String text, Class<T> type);
	
	<T> T parseObject(String text, Type type);
}
