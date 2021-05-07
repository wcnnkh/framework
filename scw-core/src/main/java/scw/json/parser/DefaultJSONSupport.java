package scw.json.parser;

import scw.json.AbstractJSONSupport;
import scw.json.JsonElement;

public class DefaultJSONSupport extends AbstractJSONSupport {

	public JsonElement parseJson(String text) {
		return new DefaultJsonElement(text);
	}

	@Override
	protected String toJsonStringInternal(Object obj) {
		return JSONValue.toJSONString(obj);
	}

}
