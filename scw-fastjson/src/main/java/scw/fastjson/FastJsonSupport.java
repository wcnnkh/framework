package scw.fastjson;

import com.alibaba.fastjson.JSON;

import scw.json.AbstractJSONSupport;
import scw.json.EmptyJsonElement;
import scw.json.JsonElement;

public final class FastJsonSupport extends AbstractJSONSupport {
	public static final FastJsonSupport INSTANCE = new FastJsonSupport();

	@Override
	protected String toJsonStringInternal(Object obj) {
		return JSON.toJSONString(obj, ExtendFastJsonValueFilter.INSTANCE);
	}

	public JsonElement parseJson(String text) {
		return new FastJsonElement(text, EmptyJsonElement.INSTANCE);
	}
}
