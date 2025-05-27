package run.soeasy.framework.core.transform.property;

public interface Property extends PropertyDescriptor {

	@Override
	boolean isReadable();

	@Override
	boolean isWriteable();

	Object readFrom(Object target);

	void writeTo(Object value, Object target);

	default PropertyAccessor accessor(Object target) {
		return new ObjectPropertyAccessor<>(this, target);
	}
}
