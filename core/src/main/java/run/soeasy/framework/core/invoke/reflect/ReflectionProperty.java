package run.soeasy.framework.core.invoke.reflect;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.property.Property;

@Getter
@Setter
public final class ReflectionProperty implements Property, Serializable {
	private static final long serialVersionUID = 1L;

	public static enum InvokeType {
		DEFAULT, FIELD, METHOD
	}

	private final String name;
	private final ReflectionField propertyField;
	private final ReflectionPropertyMethod propertyMethod;
	private final InvokeType readType;
	private final InvokeType writeType;

	public ReflectionProperty(@NonNull Field field) {
		this(field.getName(), field, null);
	}

	public ReflectionProperty(@NonNull Method method) {
		this(method.getName(), null, method);
	}

	public ReflectionProperty(@NonNull String name, Field field, Method method) {
		this(name, field, method, InvokeType.DEFAULT, InvokeType.DEFAULT);
	}

	public ReflectionProperty(@NonNull String name, Field field, Method method, @NonNull InvokeType readType,
			@NonNull InvokeType writeType) {
		this.name = name;
		this.propertyField = field == null ? null : new ReflectionField(field);
		this.propertyMethod = method == null ? null : new ReflectionPropertyMethod(method);
		this.readType = readType;
		this.writeType = writeType;
	}

	@Override
	public TypeDescriptor getRequiredTypeDescriptor() {
		if (readType == InvokeType.FIELD || propertyMethod == null) {
			return propertyField.getRequiredTypeDescriptor();
		} else {
			return new TypeDescriptor(propertyMethod.getRequiredTypeDescriptor().getResolvableType(), null,
					propertyMethod, propertyField);
		}
	}

	@Override
	public TypeDescriptor getReturnTypeDescriptor() {
		if (readType == InvokeType.FIELD || propertyMethod == null) {
			return propertyField.getReturnTypeDescriptor();
		} else {
			return new TypeDescriptor(propertyMethod.getReturnTypeDescriptor().getResolvableType(), null,
					propertyMethod, propertyField);
		}
	}

	@Override
	public final boolean isReadable() {
		return isReadable(readType);
	}

	public boolean isReadable(InvokeType invokeType) {
		if (invokeType == InvokeType.FIELD) {
			return propertyField != null && propertyField.isReadable();
		} else if (invokeType == InvokeType.METHOD) {
			return propertyMethod != null && propertyMethod.isReadable();
		}
		return isWriteable(InvokeType.FIELD) || isWriteable(InvokeType.METHOD);
	}

	@Override
	public final boolean isWriteable() {
		return isWriteable(this.writeType);
	}

	public boolean isWriteable(InvokeType invokeType) {
		if (invokeType == InvokeType.FIELD) {
			return propertyField != null && propertyField.isWriteable();
		} else if (invokeType == InvokeType.METHOD) {
			return propertyMethod != null && propertyMethod.isWriteable();
		}
		return isWriteable(InvokeType.FIELD) || isWriteable(InvokeType.METHOD);
	}

	@Override
	public Object readFrom(Object target) {
		if (readType == InvokeType.FIELD || propertyMethod == null) {
			return propertyField.readFrom(target);
		} else {
			return propertyMethod.readFrom(target);
		}
	}

	@Override
	public void writeTo(Object value, Object target) {
		if (writeType == InvokeType.FIELD || propertyMethod == null) {
			propertyField.writeTo(value, target);
		} else {
			propertyMethod.writeTo(value, target);
		}
	}
}
