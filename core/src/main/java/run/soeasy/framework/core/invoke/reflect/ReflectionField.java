package run.soeasy.framework.core.invoke.reflect;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collections;

import lombok.NonNull;
import run.soeasy.framework.core.annotation.AnnotatedElementWrapper;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.invoke.field.FieldDescriptor;
import run.soeasy.framework.core.reflect.ReflectionUtils;
import run.soeasy.framework.core.transform.indexed.IndexedDescriptor;
import run.soeasy.framework.core.transform.indexed.PropertyTemplate;

public class ReflectionField extends AbstractReflectionExecutable<Field>
		implements AnnotatedElementWrapper<Field>, FieldDescriptor, Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private Class<?> declaringClass;

	public ReflectionField(@NonNull Field member) {
		super(member);
	}

	@Override
	public synchronized void setSource(Field source) {
		this.name = source.getName();
		this.declaringClass = source.getDeclaringClass();
		super.setSource(source);
	}

	@Override
	public Field getSource() {
		Field field = super.getSource();
		if (field == null) {
			synchronized (this) {
				field = super.getSource();
				if (field == null) {
					field = ReflectionUtils.findDeclaredField(declaringClass, name).withSuperclass().first();
					super.setSource(field);
				}
			}
		}
		return field;
	}

	@Override
	public final String getName() {
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
	public final PropertyTemplate getParameterTemplate() {
		return () -> Collections.singleton((IndexedDescriptor) this).iterator();
	}

	@Override
	public void writeTo(Object value, Object target) {
		ReflectionUtils.set(getSource(), target, value);
	}

	@Override
	public Object readFrom(Object target) {
		return ReflectionUtils.get(getSource(), target);
	}

	@Override
	public Object getIndex() {
		return name;
	}
}
