package run.soeasy.framework.core.transform;

import lombok.NonNull;
import run.soeasy.framework.core.collection.MapDictionary;
import run.soeasy.framework.core.convert.value.AccessibleDescriptor;
import run.soeasy.framework.core.domain.KeyValue;

public class MapTemplate<E extends AccessibleDescriptor, W extends Template<E>>
		extends MapDictionary<Object, E, KeyValue<Object, E>, W> implements TemplateWrapper<E, W> {

	public MapTemplate(@NonNull W source) {
		super(source);
	}

	@Override
	public Template<E> asArray() {
		return getSource();
	}

	@Override
	public Template<E> asMap() {
		return this;
	}
}
