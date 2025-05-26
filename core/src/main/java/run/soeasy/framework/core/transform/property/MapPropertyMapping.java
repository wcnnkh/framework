package run.soeasy.framework.core.transform.property;

import lombok.NonNull;

public class MapPropertyMapping<W extends PropertyMapping> extends MapPropertyTemplate<PropertyAccessor, W>
		implements PropertyMappingWrapper<W> {

	public MapPropertyMapping(@NonNull W source) {
		super(source);
	}

	@Override
	public PropertyMapping asMap() {
		return this;
	}

	@Override
	public PropertyMapping asArray() {
		return getSource();
	}
}
