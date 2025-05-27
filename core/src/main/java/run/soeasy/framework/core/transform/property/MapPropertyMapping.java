package run.soeasy.framework.core.transform.property;

import lombok.NonNull;

public class MapPropertyMapping<V extends PropertyAccessor, W extends PropertyMapping<V>>
		extends MapPropertyTemplate<V, W> implements PropertyMappingWrapper<V, W> {

	public MapPropertyMapping(@NonNull W source) {
		super(source);
	}

	@Override
	public PropertyMapping<V> asMap() {
		return this;
	}

	@Override
	public PropertyMapping<V> asArray() {
		return getSource();
	}
}
