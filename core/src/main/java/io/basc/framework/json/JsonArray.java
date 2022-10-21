package io.basc.framework.json;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.util.stream.Processor;

public interface JsonArray extends Json<Integer>, Iterable<JsonElement> {
	static final String PREFIX = "[";
	static final String SUFFIX = "]";

	JsonElement get(Integer index);

	boolean add(Object element);

	boolean remove(int index);

	default Stream<JsonElement> stream() {
		return XUtils.stream(this.iterator());
	}

	default <T, E extends Throwable> List<T> convert(Processor<JsonElement, T, E> converter) throws E {
		if (isEmpty()) {
			return Collections.emptyList();
		}

		List<T> list = new ArrayList<T>(size());
		for (JsonElement jsonElement : this) {
			list.add(converter.process(jsonElement));
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	default <T> List<T> convert(Class<? extends T> type) {
		return convert((o) -> {
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
		});
	}

	@SuppressWarnings("unchecked")
	default <T> List<T> convert(Type type) {
		if (type instanceof Class) {
			return (List<T>) convert((Class<?>) type);
		}

		return convert((o) -> (T) o.getAsObject(type));
	}
}