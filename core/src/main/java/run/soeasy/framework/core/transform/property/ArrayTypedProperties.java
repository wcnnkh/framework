package run.soeasy.framework.core.transform.property;

public class ArrayTypedProperties<W extends TypedProperties> extends ArrayPropertyMapping<PropertyAccessor, W>
		implements TypedPropertiesWrapper<W> {

	public ArrayTypedProperties(W source, boolean uniqueness) {
		super(source, uniqueness);
	}

	@Override
	public TypedProperties asArray(boolean uniqueness) {
		return isUniqueness() == uniqueness ? this : getSource().asArray(uniqueness);
	}
}
