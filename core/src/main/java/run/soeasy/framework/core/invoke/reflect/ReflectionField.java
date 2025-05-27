package run.soeasy.framework.core.invoke.reflect;

import java.io.Serializable;
import java.lang.reflect.Field;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.lang.ReflectionUtils;
import run.soeasy.framework.core.transform.property.Property;

public class ReflectionField implements Property, Serializable {
	private static final long serialVersionUID = 1L;
	private final Class<?> declaringClass;
	private volatile String name;
	private transient volatile Field field;

	public ReflectionField(@NonNull Class<?> declaringClass, @NonNull String name) {
		this.declaringClass = declaringClass;
		this.name = name;
	}

	public ReflectionField(@NonNull Field field) {
		this(field.getDeclaringClass(), field.getName());
		this.field = field;
	}

	public synchronized void setField(@NonNull Field field) {
		this.field = field;
		this.name = field.getName();
	}

	public synchronized void setName(@NonNull String name) {
		this.name = name;
		this.field = null;
	}

	public Field getField() {
		if (field == null) {
			synchronized (this) {
				if (field == null) {
					field = name == null ? null
							: ReflectionUtils.findDeclaredField(declaringClass, name).withAll().first();
				}
			}
		}
		return field;
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
					typeDescriptor = TypeDescriptor.forFieldType(getField());
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
	public void writeTo(Object value, Object target) {
		ReflectionUtils.set(getField(), target, value);
	}

	@Override
	public Object readFrom(Object target) {
		return ReflectionUtils.get(getField(), target);
	}
}
