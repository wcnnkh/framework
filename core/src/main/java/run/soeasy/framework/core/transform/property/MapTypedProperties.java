package run.soeasy.framework.core.transform.property;

public class MapTypedProperties<W extends TypedProperties> extends MapPropertyMapping<PropertyAccessor, W>
		implements TypedPropertiesWrapper<W> {

	public MapTypedProperties(W source, boolean uniqueness) {
		super(source, uniqueness);
	}

	@Override
	public TypedProperties asMap(boolean uniqueness) {
		return isUniqueness() == uniqueness ? this : getSource().asMap(uniqueness);
	}
}
