package run.soeasy.framework.core.invoke.reflect;

import java.io.Serializable;
import java.lang.reflect.Field;

import lombok.NonNull;
import run.soeasy.framework.core.annotation.AnnotatedElementWrapper;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.property.Property;
import run.soeasy.framework.core.type.ReflectionUtils;

public class ReflectionField implements AnnotatedElementWrapper<Field>, Property, Serializable {
	private static final long serialVersionUID = 1L;
	private volatile Field source;
	private String name;
	private Class<?> declaringClass;

	public ReflectionField(Field source) {
		setSource(source);
	}

	public synchronized void setSource(@NonNull Field source) {
		this.source = source;
		this.name = source.getName();
		this.declaringClass = source.getDeclaringClass();
	}

	@Override
	public Field getSource() {
		if (source == null) {
			synchronized (this) {
				if (source == null) {
					setSource(ReflectionUtils.findDeclaredField(declaringClass, name).withSuperclass().first());
				}
			}
		}
		return source;
	}

	@Override
	public final String getName() {
		return name;
	}

	public Class<?> getDeclaringClass() {
		return declaringClass;
	}

	private volatile TypeDescriptor typeDescriptor;

	public TypeDescriptor getTypeDescriptor() {
		if (typeDescriptor == null) {
			synchronized (this) {
				if (typeDescriptor == null) {
					typeDescriptor = TypeDescriptor.forFieldType(getSource());
				}
			}
		}
		return typeDescriptor;
	}

	@Override
	public final TypeDescriptor getReturnTypeDescriptor() {
		return getTypeDescriptor();
	}

	@Override
	public final TypeDescriptor getRequiredTypeDescriptor() {
		return getTypeDescriptor();
	}

	@Override
	public boolean isReadable() {
		return true;
	}

	@Override
	public boolean isWriteable() {
		return true;
	}

	@Override
	public void writeTo(Object value, Object target) {
		ReflectionUtils.set(getSource(), target, value);
	}

	@Override
	public Object readFrom(Object target) {
		return ReflectionUtils.get(getSource(), target);
	}
}
