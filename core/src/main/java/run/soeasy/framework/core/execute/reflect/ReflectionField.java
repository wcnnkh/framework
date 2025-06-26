package run.soeasy.framework.core.execute.reflect;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.function.Supplier;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.property.Property;
import run.soeasy.framework.core.type.ReflectionUtils;

public class ReflectionField implements Property, Serializable {
	private static final long serialVersionUID = 1L;
	private final Class<?> declaringClass;
	private volatile String name;
	private transient volatile Supplier<Field> fieldSupplier;

	public ReflectionField(@NonNull Class<?> declaringClass, @NonNull String name) {
		this.declaringClass = declaringClass;
		this.name = name;
	}

	public ReflectionField(@NonNull Field field) {
		this(field.getDeclaringClass(), field.getName());
		this.fieldSupplier = () -> field;
	}

	public synchronized void setField(@NonNull Field field) {
		this.fieldSupplier = () -> field;
		this.name = field.getName();
		this.typeDescriptor = null;
	}

	public synchronized void setName(@NonNull String name) {
		this.name = name;
		this.fieldSupplier = null;
		this.typeDescriptor = null;
	}

	public Field getField() {
		if (fieldSupplier == null) {
			synchronized (this) {
				if (fieldSupplier == null) {
					Field field = name == null ? null
							: ReflectionUtils.findDeclaredField(declaringClass, name).withAll().first();
					fieldSupplier = () -> field;
				}
			}
		}
		return fieldSupplier.get();
	}

	@Override
	public String getName() {
		return name;
	}

	public final Class<?> getDeclaringClass() {
		return declaringClass;
	}

	private transient volatile TypeDescriptor typeDescriptor;

	public TypeDescriptor getTypeDescriptor() {
		if (typeDescriptor == null) {
			synchronized (this) {
				if (typeDescriptor == null) {
					Field field = getField();
					typeDescriptor = field == null ? TypeDescriptor.valueOf(Object.class)
							: TypeDescriptor.forFieldType(getField());
				}
			}
		}
		return typeDescriptor;
	}

	@Override
	public TypeDescriptor getReturnTypeDescriptor() {
		return getTypeDescriptor();
	}

	@Override
	public TypeDescriptor getRequiredTypeDescriptor() {
		return getTypeDescriptor();
	}

	@Override
	public boolean isReadable() {
		return getField() != null;
	}

	@Override
	public boolean isWriteable() {
		return getField() != null;
	}

	@Override
	public void writeTo(Object target, Object value) {
		ReflectionUtils.set(getField(), target, value);
	}

	@Override
	public Object readFrom(Object target) {
		return ReflectionUtils.get(getField(), target);
	}

	@Override
	public String toString() {
		Field field = getField();
		return field == null ? null : field.toString();
	}
}
