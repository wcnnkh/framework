package run.soeasy.framework.core.transform.property;

import lombok.NonNull;

public class MapTypedProperties<W extends TypedProperties> extends MapPropertyMapping<PropertyAccessor, W>
		implements TypedPropertiesWrapper<W> {

	public MapTypedProperties(@NonNull W source) {
		super(source);
	}

	@Override
	public TypedProperties asMap() {
		return this;
	}

	@Override
	public TypedProperties asArray() {
		return getSource();
	}
}
