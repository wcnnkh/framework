package scw.json;

public interface JSONParseSupport {
	JSONArray parseArray(String text);

	JSONObject parseObject(String text);

	String toJSONString(Object obj);
}
