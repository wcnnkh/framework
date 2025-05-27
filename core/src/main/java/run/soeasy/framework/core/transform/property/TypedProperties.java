package run.soeasy.framework.core.transform.property;

@FunctionalInterface
public interface TypedProperties extends PropertyMapping<PropertyAccessor> {
	@Override
	default TypedProperties asMap() {
		return new MapTypedProperties<>(this);
	}

	@Override
	default TypedProperties asArray() {
		return this;
	}
}
