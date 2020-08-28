package scw.json;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;

public interface JSONSupport {
	String toJSONString(Object obj);

	JsonArray parseArray(String text);

	JsonObject parseObject(String text);

	<T> T parseObject(String text, Class<T> type);

	<T> T parseObject(String text, Type type);

	JsonArray parseArray(Reader reader) throws IOException;

	JsonObject parseObject(Reader reader) throws IOException;

	<T> T parseObject(Reader reader, Class<T> type) throws IOException;

	<T> T parseObject(Reader reader, Type type) throws IOException;
}
