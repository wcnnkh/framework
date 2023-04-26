package io.basc.framework.mapper;

import java.lang.reflect.Field;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.value.Value;

public class FieldSetter implements Setter {
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
	public void set(Value target, Object value) {
		ReflectionUtils.set(field, target == null ? null : target.getSource(), value);
	}

	@Override
	public FieldSetter rename(String name) {
		FieldSetter fieldSetter = new FieldSetter(field);
		fieldSetter.name = name;
		fieldSetter.typeDescriptor = this.typeDescriptor;
		return fieldSetter;
	}

}
