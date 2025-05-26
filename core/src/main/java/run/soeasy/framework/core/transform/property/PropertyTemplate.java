package run.soeasy.framework.core.transform.property;

import java.util.function.Function;
import java.util.stream.Stream;

import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.NoUniqueElementException;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.transform.Template;

@FunctionalInterface
public interface PropertyTemplate<T extends PropertyDescriptor> extends Template<T>, Elements<T> {
	@Override
	default T get(Object key) throws NoUniqueElementException {
		if (key instanceof Number) {
			return get(((Number) key).intValue());
		} else if (key instanceof String) {
			return get((String) key);
		}
		return Template.super.get(key);
	}

	default T get(String name) throws NoUniqueElementException {
		return filter((e) -> StringUtils.equals(name, e.getName())).getUnique();
	}

	@Override
	default Elements<KeyValue<Object, T>> getElements() {
		return Elements.of(() -> stream().map((e) -> KeyValue.of(e.getName(), e)));
	}

	default Class<?>[] getTypes(Function<? super T, Class<?>> typeMapper) {
		Class<?>[] types = new Class<?>[size()];
		for (int i = 0; i < types.length; i++) {
			types[i] = typeMapper.apply(get(i));
		}
		return types;
	}

	@Override
	default Stream<T> stream() {
		return CollectionUtils.unknownSizeStream(this.iterator());
	}

	@Override
	default PropertyTemplate<T> asMap() {
		return new MapPropertyTemplate<>(this);
	}

	@Override
	default PropertyTemplate<T> asArray() {
		return this;
	}
}
