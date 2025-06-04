package run.soeasy.framework.core.transform.property;

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.collection.NoUniqueElementException;
import run.soeasy.framework.core.transform.templates.TemplateProperties;

public class PropertyTemplateProperties<S extends PropertyDescriptor, T extends PropertyTemplate<S>, V extends PropertyAccessor>
		extends TemplateProperties<S, T, V> implements PropertyMapping<V> {

	public PropertyTemplateProperties(@NonNull T template, @NonNull Function<? super S, ? extends V> mapper) {
		super(template, mapper);
	}

	@Override
	public Iterator<V> iterator() {
		Stream<V> stream = CollectionUtils.unknownSizeStream(getTemplate().iterator()).map(getMapper());
		return stream.iterator();
	}

	@Override
	public Stream<V> stream() {
		return getTemplate().stream().map(getMapper());
	}

	@Override
	public V get(int index) throws IndexOutOfBoundsException {
		S value = getTemplate().get(index);
		return value == null ? null : getMapper().apply(value);
	}

	@Override
	public V get(String name) throws NoUniqueElementException {
		S value = getTemplate().get(name);
		return value == null ? null : getMapper().apply(value);
	}

	@Override
	public Class<?>[] getTypes(Function<? super V, Class<?>> typeMapper) {
		return getTemplate().getTypes((e) -> typeMapper.apply(getMapper().apply(e)));
	}
}
