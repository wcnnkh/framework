package run.soeasy.framework.core.transform.property;

@FunctionalInterface
public interface TypedPropertiesWrapper<W extends TypedProperties>
		extends TypedProperties, PropertyMappingWrapper<PropertyAccessor, W> {
	@Override
	default TypedProperties asMap() {
		return getSource().asMap();
	}

	@Override
	default TypedProperties asArray() {
		return getSource().asArray();
	}
}
