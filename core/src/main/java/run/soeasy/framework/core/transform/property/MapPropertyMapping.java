package run.soeasy.framework.core.transform.property;

public class MapPropertyMapping<V extends PropertyAccessor, W extends PropertyMapping<V>>
		extends MapPropertyTemplate<V, W> implements PropertyMappingWrapper<V, W> {

	public MapPropertyMapping(W source, boolean uniqueness) {
		super(source, uniqueness);
	}

	@Override
	public PropertyMapping<V> asMap(boolean uniqueness) {
		return isUniqueness() == uniqueness ? this : getSource().asMap(uniqueness);
	}

}
