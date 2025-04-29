package run.soeasy.framework.core.reflect;

import java.io.Serializable;
import java.lang.reflect.Field;

import lombok.NonNull;
import run.soeasy.framework.core.annotation.AnnotatedElementWrapper;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.mapping.PropertyDescriptor;

public abstract class ReflectionField extends AbstractReflectionExecutable<Field>
		implements PropertyDescriptor, AnnotatedElementWrapper<Field>, Serializable {
	private static final long serialVersionUID = 1L;
	private final String name;
	private final Class<?> declaringClass;

	public ReflectionField(@NonNull Field member) {
		super(member);
		this.name = member.getName();
		this.declaringClass = member.getDeclaringClass();
	}

	@Override
	public Field getSource() {
		Field field = super.getSource();
		if (field == null) {
			synchronized (this) {
				field = super.getSource();
				if (field == null) {
					field = ReflectionUtils.findDeclaredField(declaringClass, name).withSuperclass().first();
				}
			}
		}
		return field;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<?> getDeclaringClass() {
		return declaringClass;
	}

	@Override
	public Elements<TypeDescriptor> getExceptionTypeDescriptors() {
		return Elements.empty();
	}

	private volatile TypeDescriptor typeDescriptor;

	@Override
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
	public abstract ReflectionField rename(String name);
}
