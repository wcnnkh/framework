package run.soeasy.framework.core.transform;

import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.NoUniqueElementException;
import run.soeasy.framework.core.convert.value.AccessibleDescriptor;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.domain.KeyValue;

@RequiredArgsConstructor
@Getter
public class TemplateProperties<S extends AccessibleDescriptor, T extends Template<S>, V extends TypedValueAccessor>
		implements TemplateMapping<V> {
	@NonNull
	private final T template;
	@NonNull
	private final Function<? super S, ? extends V> mapper;

	@Override
	public Elements<KeyValue<Object, V>> getElements() {
		Elements<KeyValue<Object, S>> elements = template.getElements();
		if (elements == null) {
			return null;
		}

		return elements.map((e) -> KeyValue.of(e.getKey(), e.getValue() == null ? null : mapper.apply(e.getValue())));
	}

	@Override
	public V get(Object key) throws NoUniqueElementException {
		S value = template.get(key);
		return value == null ? null : mapper.apply(value);
	}

	@Override
	public int size() {
		return template.size();
	}

	@Override
	public boolean hasKey(Object key) {
		return template.hasKey(key);
	}

	@Override
	public boolean hasElements() {
		return template.hasElements();
	}

	@Override
	public KeyValue<Object, V> getElement(int index) {
		KeyValue<Object, S> keyValue = template.getElement(index);
		return keyValue == null ? null
				: KeyValue.of(keyValue.getKey(),
						keyValue.getValue() == null ? null : mapper.apply(keyValue.getValue()));
	}

	@Override
	public boolean isArray() {
		return template.isArray();
	}

	@Override
	public boolean isMap() {
		return template.isMap();
	}

	@Override
	public Elements<Object> keys() {
		return template.keys();
	}

	@Override
	public Elements<V> getValues(Object key) {
		Elements<S> elements = template.getValues(key);
		return elements == null ? null : elements.map(mapper);
	}
}
