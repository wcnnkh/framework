package run.soeasy.framework.core.execute.reflect;

import java.io.Serializable;
import java.lang.reflect.Method;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.property.Property;

@Getter
@ToString(callSuper = true)
public class ReflectionProperty extends ReflectionField implements Property, Serializable {
	public static enum InvokeType {
		DEFAULT, FIELD, METHOD
	}

	private static final long serialVersionUID = 1L;

	private ReflectionMethod readMethod;
	private ReflectionMethod writeMethod;
	@NonNull
	@Setter
	private InvokeType readType = InvokeType.DEFAULT;
	@NonNull
	@Setter
	private InvokeType writeType = InvokeType.DEFAULT;

	public ReflectionProperty(@NonNull Class<?> declaringClass, @NonNull String propertyName) {
		super(declaringClass, propertyName);
	}

	public void setReadMethod(Method method) {
		this.readMethod = method == null ? null : new ReflectionMethod(method);
	}

	public void setWriteMethod(Method method) {
		this.writeMethod = method == null ? null : new ReflectionMethod(method);
	}

	@Override
	public TypeDescriptor getRequiredTypeDescriptor() {
		if (writeType == InvokeType.FIELD || writeMethod == null) {
			return super.getRequiredTypeDescriptor();
		} else {
			return new TypeDescriptor(writeMethod.getRequiredTypeDescriptor().getResolvableType(), null, writeMethod,
					getField());
		}
	}

	@Override
	public TypeDescriptor getReturnTypeDescriptor() {
		if (readType == InvokeType.FIELD || readMethod == null) {
			return super.getReturnTypeDescriptor();
		} else {
			return new TypeDescriptor(readMethod.getReturnTypeDescriptor().getResolvableType(), null, readMethod,
					getField());
		}
	}

	@Override
	public boolean isReadable() {
		return isReadable(this.readType);
	}

	private boolean isReadable(InvokeType invokeType) {
		if (invokeType == InvokeType.FIELD) {
			return super.isReadable();
		} else if (invokeType == InvokeType.METHOD) {
			return readMethod != null && readMethod.isReadable();
		}
		return isReadable(InvokeType.FIELD) || isReadable(InvokeType.METHOD);
	}

	@Override
	public boolean isWriteable() {
		return isWriteable(this.writeType);
	}

	private boolean isWriteable(InvokeType invokeType) {
		if (invokeType == InvokeType.FIELD) {
			return super.isWriteable();
		} else if (invokeType == InvokeType.METHOD) {
			return writeMethod != null && writeMethod.isWriteable();
		}
		return isWriteable(InvokeType.FIELD) || isWriteable(InvokeType.METHOD);
	}

	@Override
	public Object readFrom(Object target) {
		if (readType == InvokeType.FIELD || readMethod == null) {
			return super.readFrom(target);
		} else {
			return readMethod.readFrom(target);
		}
	}

	@Override
	public void writeTo(Object target, Object value) {
		if (writeType == InvokeType.FIELD || writeMethod == null) {
			super.writeTo(target, value);
		} else {
			writeMethod.writeTo(target, value);
		}
	}
}
