package io.basc.framework.fastjson;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONAware;
import com.alibaba.fastjson.JSONValidator;
import com.alibaba.fastjson.JSONValidator.Type;
import com.alibaba.fastjson.parser.Feature;

import io.basc.framework.core.convert.Converter;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.json.AbstractJsonElement;
import io.basc.framework.json.JsonArray;
import io.basc.framework.json.JsonObject;
import io.basc.framework.util.StringUtils;

public final class FastJsonElement extends AbstractJsonElement implements JSONAware, Serializable {
	private static final long serialVersionUID = 1L;
	private String text;

	public FastJsonElement(String text) {
		this.text = text;
	}

	@Override
	public <E extends Throwable> Object convert(TypeDescriptor targetType,
			Converter<? super Object, ? extends Object, E> converter) throws E {
		return super.convert(targetType, (source, st, tt) -> {
			if (converter.canConvert(st, tt)) {
				return converter.convert(source, st, tt);
			}

			String text;
			if (source instanceof String) {
				text = (String) source;
			} else {
				text = JSON.toJSONString(source);
			}
			return JSON.parseObject(text, targetType.getResolvableType().getType(), Feature.SupportNonPublicField);
		});
	}

	public JsonArray getAsJsonArray() {
		return new FastJsonArray(com.alibaba.fastjson.JSONArray.parseArray(text));
	}

	public JsonObject getAsJsonObject() {
		return new FastJsonObject(com.alibaba.fastjson.JSONObject.parseObject(text));
	}

	@Override
	public Object getValue() {
		return text;
	}

	public String getAsString() {
		return text;
	}

	public String toJSONString() {
		return text;
	}

	public boolean isJsonArray() {
		return text == null ? false : JSONValidator.from(text).getType() == Type.Array;
	}

	public boolean isJsonObject() {
		return text == null ? false : JSONValidator.from(text).getType() == Type.Object;
	}

	public boolean isEmpty() {
		return StringUtils.isEmpty(text);
	}

	public String toJsonString() {
		return text;
	}

	@Override
	public int hashCode() {
		return text.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof FastJsonElement) {
			return StringUtils.equals(text, ((FastJsonElement) obj).text);
		}
		return false;
	}
}
