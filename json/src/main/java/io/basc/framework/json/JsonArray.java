package io.basc.framework.json;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.basc.framework.transform.Properties;
import io.basc.framework.transform.Property;
import io.basc.framework.transform.ReadOnlyProperty;
import io.basc.framework.util.collection.Elements;
import io.basc.framework.util.collection.Streams;
import io.basc.framework.util.function.Function;
import io.basc.framework.util.reflect.ReflectionUtils;

public interface JsonArray extends Json<Integer>, Iterable<JsonElement>, Properties {
	static final String PREFIX = "[";
	static final String SUFFIX = "]";

	boolean add(Object element);

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

	default <T, E extends Throwable> List<T> convert(Function<JsonElement, T, E> converter) throws E {
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
	default <T> List<T> convert(Type type) {
		if (type instanceof Class) {
			return (List<T>) convert((Class<?>) type);
		}

		return convert((o) -> (T) o.getAsObject(type));
	}

	JsonElement get(Integer index);

	@Override
	default Elements<Property> getElements() {
		return Elements.of(() -> IntStream.range(0, size()).mapToObj((index) -> {
			JsonElement jsonElement = get(index);
			return new ReadOnlyProperty(index, jsonElement);
		}));
	}

	boolean remove(int index);

	default Stream<JsonElement> stream() {
		return Streams.stream(this.iterator());
	}
}