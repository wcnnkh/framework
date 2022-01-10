package io.basc.framework.json;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.basc.framework.convert.Converter;
import io.basc.framework.core.reflect.ReflectionUtils;

public interface JsonArray extends Json<Integer>, Iterable<JsonElement> {
	static final String PREFIX = "[";
	static final String SUFFIX = "]";

	JsonElement getValue(Integer index);

	boolean add(Object element);

	boolean remove(int index);

	default <T> List<T> convert(Converter<JsonElement, T> converter) {
		if (isEmpty()) {
			return Collections.emptyList();
		}

		List<T> list = new ArrayList<T>(size());
		for (JsonElement jsonElement : this) {
			list.add(converter.convert(jsonElement));
		}
		return list;
	}

	default <T> List<T> convert(Class<? extends T> type) {
		return convert(new Converter<JsonElement, T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T convert(JsonElement o) {
				// 支持构造参数存在一个，且参数类型为JsonArray或JsonObject
				Constructor<?> constructor = null;
				if (o.isJsonArray()) {
					constructor = ReflectionUtils.getDeclaredConstructor(type, JsonArray.class);
				} else if (o.isJsonObject()) {
					constructor = ReflectionUtils.getDeclaredConstructor(type, JsonObject.class);
				}

				if (constructor != null) {
					return (T) ReflectionUtils.newInstance(constructor,
							o.isJsonArray() ? o.getAsJsonArray() : o.getAsJsonObject());
				}
				return o.getAsObject(type);
			}
		});
	}

	@SuppressWarnings("unchecked")
	default <T> List<T> convert(Type type) {
		if (type instanceof Class) {
			return (List<T>) convert((Class<?>) type);
		}

		return convert(new Converter<JsonElement, T>() {
			@Override
			public T convert(JsonElement o) {
				return (T) o.getAsObject(type);
			}
		});
	}
}