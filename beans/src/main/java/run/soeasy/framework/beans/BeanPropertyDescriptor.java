package run.soeasy.framework.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.NonNull;
import run.soeasy.framework.core.MergedAnnotatedElement;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.execution.Getter;
import run.soeasy.framework.core.execution.Getter.MergedGetter;
import run.soeasy.framework.core.execution.Setter;
import run.soeasy.framework.core.execution.Setter.MergedSetter;
import run.soeasy.framework.core.execution.reflect.ReflectionFieldGetter;
import run.soeasy.framework.core.execution.reflect.ReflectionFieldSetter;
import run.soeasy.framework.core.execution.reflect.ReflectionMethodGetter;
import run.soeasy.framework.core.execution.reflect.ReflectionMethodSetter;
import run.soeasy.framework.core.transform.mapping.FieldDescriptor;
import run.soeasy.framework.util.ResolvableType;
import run.soeasy.framework.util.collection.Elements;
import run.soeasy.framework.util.reflect.ReflectionUtils;

public class BeanPropertyDescriptor implements FieldDescriptor {
	@lombok.Getter
	private final Class<?> beanClass;
	private volatile Field field;

	private volatile MergedGetter<?> getter;

	@lombok.Getter
	private final PropertyDescriptor propertyDescriptor;

	private volatile MergedSetter<?> setter;

	public BeanPropertyDescriptor(@NonNull Class<?> beanClass, @NonNull PropertyDescriptor propertyDescriptor) {
		this.beanClass = beanClass;
		this.propertyDescriptor = propertyDescriptor;
	}

	private volatile TypeDescriptor typeDescriptor;

	@Override
	public TypeDescriptor getTypeDescriptor() {
		if (typeDescriptor == null) {
			synchronized (this) {
				if (typeDescriptor == null) {
					MergedAnnotatedElement mergedAnnotatedElement = new MergedAnnotatedElement(
							Arrays.asList(getReader().getTypeDescriptor(), getWriter().getTypeDescriptor()));
					ResolvableType resolvableType = ResolvableType.forType(propertyDescriptor.getPropertyType());
					this.typeDescriptor = new TypeDescriptor(resolvableType, null, mergedAnnotatedElement);
				}
			}
		}
		return typeDescriptor;
	}

	public Field getField() {
		if (field == null) {
			synchronized (this) {
				if (field == null) {
					field = ReflectionUtils.getDeclaredField(beanClass, propertyDescriptor.getName());
				}
			}
		}
		return field;
	}

	@Override
	public String getName() {
		return propertyDescriptor.getName();
	}

	public boolean hasReadMethod() {
		return propertyDescriptor.getReadMethod() != null;
	}

	public boolean hasWriteMethod() {
		return propertyDescriptor.getWriteMethod() != null;
	}

	@Override
	public MergedGetter<?> getReader() {
		if (getter == null) {
			synchronized (this) {
				if (getter == null) {
					List<Getter> list = new ArrayList<>(4);
					Method method = propertyDescriptor.getReadMethod();
					if (method != null) {
						ReflectionMethodGetter methodGetter = new ReflectionMethodGetter(method);
						list.add(methodGetter);
					}

					Field field = getField();
					if (field != null) {
						ReflectionFieldGetter fieldGetter = new ReflectionFieldGetter(field);
						list.add(fieldGetter);
					}

					Elements<Getter> elements = list.isEmpty() ? Elements.empty()
							: Elements.forArray(list.toArray(new Getter[0]));
					getter = new MergedGetter<>(elements);
				}
			}
		}
		return getter;
	}

	public void reset() {
		synchronized (this) {
			this.field = null;
			this.setter = null;
			this.getter = null;
			this.typeDescriptor = null;
		}
	}

	@Override
	public boolean isReadable() {
		return !getReader().getElements().isEmpty();
	}

	@Override
	public boolean isWritable() {
		return !getWriter().getElements().isEmpty();
	}

	@Override
	public MergedSetter<?> getWriter() {
		if (setter == null) {
			synchronized (this) {
				if (setter == null) {
					List<Setter> list = new ArrayList<>(2);
					Method method = propertyDescriptor.getWriteMethod();
					if (method != null) {
						list.add(new ReflectionMethodSetter(method));
					}

					Field field = getField();
					if (field != null) {
						list.add(new ReflectionFieldSetter(field));
					}
					Elements<Setter> elements = list.isEmpty() ? Elements.empty()
							: Elements.forArray(list.toArray(new Setter[0]));
					this.setter = new MergedSetter<>(elements);
				}
			}
		}
		return setter;
	}
}
