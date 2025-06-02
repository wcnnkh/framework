package run.soeasy.framework.core.transform.property;

@FunctionalInterface
public interface TypedProperties extends PropertyMapping<PropertyAccessor> {
	@Override
	default TypedProperties asMap(boolean uniqueness) {
		return new MapTypedProperties<>(this, uniqueness);
	}

	@Override
	default TypedProperties asArray(boolean uniqueness) {
		return new ArrayTypedProperties<>(this, uniqueness);
	}
}
