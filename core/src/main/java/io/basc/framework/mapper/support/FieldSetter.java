package io.basc.framework.mapper.support;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.Assert;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldSetter extends AbstractSetter {
	private final Field field;
	private volatile String name;
	private volatile TypeDescriptor typeDescriptor;

	public FieldSetter(Field field) {
		Assert.requiredArgument(field != null, "field");
		this.field = field;
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		if (typeDescriptor == null) {
			synchronized (this) {
				if (typeDescriptor == null) {
					this.typeDescriptor = new TypeDescriptor(field);
				}
			}
		}
		return typeDescriptor;
	}

	@Override
	public String getName() {
		if (name == null) {
			synchronized (this) {
				if (name == null) {
					this.name = field.getName();
				}
			}
		}
		return name;
	}

	@Override
	public void set(Object target, Object value) {
		ReflectionUtils.set(field, Modifier.isStatic(field.getModifiers())? null : target, value);
	}

	@Override
	public FieldSetter rename(String name) {
		FieldSetter fieldSetter = new FieldSetter(field);
		fieldSetter.name = name;
		fieldSetter.typeDescriptor = this.typeDescriptor;
		return fieldSetter;
	}

}
