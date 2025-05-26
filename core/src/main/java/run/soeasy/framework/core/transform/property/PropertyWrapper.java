package run.soeasy.framework.core.transform.property;

public interface PropertyWrapper<W extends Property> extends Property, PropertyDescriptorWrapper<W> {
	@Override
	default Object readFrom(Object target) {
		return getSource().readFrom(target);
	}

	@Override
	default void writeTo(Object value, Object target) {
		getSource().writeTo(value, target);
	}

	@Override
	default boolean isReadable() {
		return getSource().isReadable();
	}

	@Override
	default boolean isWriteable() {
		return getSource().isWriteable();
	}

	@Override
	default PropertyAccessor accessor(Object target) {
		return getSource().accessor(target);
	}
}
