package io.basc.framework.fastjson;

import com.alibaba.fastjson.JSON;

import io.basc.framework.json.AbstractJsonSupport;
import io.basc.framework.json.JsonElement;

public final class FastJsonSupport extends AbstractJsonSupport {
	public static final FastJsonSupport INSTANCE = new FastJsonSupport();

	@Override
	protected String toJsonStringInternal(Object obj) {
		return JSON.toJSONString(obj);
	}

	public JsonElement parseJson(String text) {
		return new FastJsonElement(text);
	}
}
