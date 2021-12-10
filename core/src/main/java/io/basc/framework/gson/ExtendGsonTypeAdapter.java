package io.basc.framework.gson;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import io.basc.framework.aop.support.ProxyUtils;
import io.basc.framework.json.JSONAware;
import io.basc.framework.value.Value;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ExtendGsonTypeAdapter extends TypeAdapter<Object> {
	public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {

		public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
			if (JSONAware.class.isAssignableFrom(typeToken.getRawType())
					|| Value.class.isAssignableFrom(typeToken.getRawType())
					|| ProxyUtils.getFactory().isProxy(typeToken.getRawType())) {
				return (TypeAdapter) new ExtendGsonTypeAdapter(gson);
			}
			return null;
		}
	};

	private final Gson context;

	private ExtendGsonTypeAdapter(Gson context) {
		this.context = context;
	}

	private <T> TypeAdapter<T> getTypeAdapter(Class<T> type) {
		TypeAdapter<T> typeAdapter = context.getAdapter(type);
		if (typeAdapter == null) {
			throw new UnsupportedOperationException(
					"Attempted to serialize java.lang.Class: " + type + ". Forgot to register a type adapter?");
		}
		return typeAdapter;
	}

	@Override
	public void write(JsonWriter out, Object value) throws IOException {
		if (value == null) {
			out.nullValue();
			return;
		}

		if (value instanceof JSONAware) {
			out.jsonValue(((JSONAware) value).toJSONString());
			return;
		}

		if (value instanceof Value) {
			Object valueToUse = ((Value) value).getSourceValue();
			if (valueToUse == null) {
				out.nullValue();
				return;
			}

			TypeAdapter<Object> typeAdapter = (TypeAdapter<Object>) getTypeAdapter(valueToUse.getClass());
			typeAdapter.write(out, valueToUse);
			return;
		}

		Class clazz = ProxyUtils.getFactory().getUserClass(value.getClass());
		TypeAdapter<Object> typeAdapter = getTypeAdapter(clazz);
		typeAdapter.write(out, value);
	}

	@Override
	public Object read(JsonReader in) throws IOException {
		throw new UnsupportedOperationException("read");
	}

}
