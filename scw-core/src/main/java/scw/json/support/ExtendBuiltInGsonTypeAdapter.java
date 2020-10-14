package scw.json.support;

import java.io.IOException;

import scw.aop.ProxyUtils;
import scw.json.JsonAware;
import scw.json.gson.Gson;
import scw.json.gson.TypeAdapter;
import scw.json.gson.TypeAdapterFactory;
import scw.json.gson.reflect.TypeToken;
import scw.json.gson.stream.JsonReader;
import scw.json.gson.stream.JsonWriter;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ExtendBuiltInGsonTypeAdapter extends TypeAdapter<Object> {
	public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {

		public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
			if (JsonAware.class.isAssignableFrom(typeToken.getRawType())
					|| ProxyUtils.getProxyFactory().isProxy(typeToken.getRawType())) {
				return (TypeAdapter) new ExtendBuiltInGsonTypeAdapter(gson);
			}
			return null;
		}
	};

	private final Gson context;

	private ExtendBuiltInGsonTypeAdapter(Gson context) {
		this.context = context;
	}

	@Override
	public void write(JsonWriter out, Object value) throws IOException {
		if (value == null) {
			out.nullValue();
			return;
		}

		if (value instanceof JsonAware) {
			out.jsonValue(((JsonAware) value).toJsonString());
			return;
		}

		Class clazz = ProxyUtils.getProxyFactory().getUserClass(value.getClass());
		TypeAdapter<Object> typeAdapter = context.getAdapter(clazz);
		if (typeAdapter == null) {
			throw new UnsupportedOperationException(
					"Attempted to serialize java.lang.Class: " + clazz + ". Forgot to register a type adapter?");
		}
		typeAdapter.write(out, value);
	}

	@Override
	public Object read(JsonReader in) throws IOException {
		throw new UnsupportedOperationException("read");
	}

}
