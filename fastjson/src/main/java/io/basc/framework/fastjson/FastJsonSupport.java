package io.basc.framework.fastjson;

import io.basc.framework.json.AbstractJSONSupport;
import io.basc.framework.json.EmptyJsonElement;
import io.basc.framework.json.JsonElement;

import com.alibaba.fastjson.JSON;

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
