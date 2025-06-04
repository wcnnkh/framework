package run.soeasy.framework.core.transform.property;

public class ArrayPropertyMapping<V extends PropertyAccessor, W extends PropertyMapping<V>>
		extends ArrayPropertyTemplate<V, W> implements PropertyMappingWrapper<V, W> {

	public ArrayPropertyMapping(W source, boolean uniqueness) {
		super(source, uniqueness);
	}

	@Override
	public PropertyMapping<V> asArray(boolean uniqueness) {
		return isUniqueness() == uniqueness ? this : getSource().asArray(uniqueness);
	}

}
