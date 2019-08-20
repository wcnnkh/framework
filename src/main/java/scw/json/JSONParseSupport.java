package scw.json;

import java.lang.reflect.Type;

public interface JSONParseSupport {
	String toJSONString(Object obj);

	JSONArray parseArray(String text);
	
	JSONObject parseObject(String text);

	<T> T parseObject(String text, Class<T> type);
	
	<T> T parseObject(String text, Type type);

	JSONArray createJSONArray();

	JSONObject createJSONObject();
}
