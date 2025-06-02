package run.soeasy.framework.core.transform.property;

@FunctionalInterface
public interface TypedPropertiesWrapper<W extends TypedProperties>
		extends TypedProperties, PropertyMappingWrapper<PropertyAccessor, W> {
	@Override
	default TypedProperties asMap(boolean uniqueness) {
		return getSource().asMap(uniqueness);
	}
	@Override
	default TypedProperties asArray(boolean uniqueness) {
		return getSource().asArray(uniqueness);
	}
}
