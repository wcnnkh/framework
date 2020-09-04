package scw.json.support;

import java.io.IOException;

import scw.aop.ProxyUtils;
import scw.json.gson.Gson;
import scw.json.gson.TypeAdapter;
import scw.json.gson.TypeAdapterFactory;
import scw.json.gson.reflect.TypeToken;
import scw.json.gson.stream.JsonReader;
import scw.json.gson.stream.JsonWriter;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class BuiltInGsonProxyTypeAdapter extends TypeAdapter<Object> {
	public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {

		public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
			boolean isProxy = ProxyUtils.getProxyFactory().isProxy(typeToken.getRawType());
			return isProxy ? (TypeAdapter) new BuiltInGsonProxyTypeAdapter(gson) : null;
		}
	};

	private final Gson context;

	private BuiltInGsonProxyTypeAdapter(Gson context) {
		this.context = context;
	}

	@Override
	public void write(JsonWriter out, Object value) throws IOException {
		if (value == null) {
			out.nullValue();
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
