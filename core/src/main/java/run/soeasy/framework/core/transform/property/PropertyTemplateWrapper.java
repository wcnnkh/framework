package run.soeasy.framework.core.transform.property;

import java.util.function.Function;
import java.util.stream.Stream;

import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.ElementsWrapper;
import run.soeasy.framework.core.collection.NoUniqueElementException;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.transform.templates.TemplateWrapper;

public interface PropertyTemplateWrapper<T extends PropertyDescriptor, W extends PropertyTemplate<T>>
		extends PropertyTemplate<T>, TemplateWrapper<T, W>, ElementsWrapper<T, W> {
	@Override
	default T get(Object key) throws NoUniqueElementException {
		return getSource().get(key);
	}

	default T get(String name) {
		return getSource().get(name);
	}

	@Override
	default Elements<KeyValue<Object, T>> getElements() {
		return Elements.of(() -> stream().map((e) -> KeyValue.of(e.getName(), e)));
	}

	default Class<?>[] getTypes(Function<? super T, Class<?>> typeMapper) {
		return getSource().getTypes(typeMapper);
	}

	@Override
	default Stream<T> stream() {
		return CollectionUtils.unknownSizeStream(this.iterator());
	}

	@Override
	default PropertyTemplate<T> asMap(boolean uniqueness) {
		return getSource().asMap(uniqueness);
	}

	@Override
	default PropertyTemplate<T> asArray(boolean uniqueness) {
		return getSource().asArray(uniqueness);
	}
}
