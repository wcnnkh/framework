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
public class FieldGetter extends AbstractGetter {
	private final Field field;
	private volatile String name;
	private volatile TypeDescriptor typeDescriptor;

	/**
	 * field和method不能同时为空
	 * 
	 * @param field
	 * @param method
	 */
	public FieldGetter(Field field) {
		Assert.requiredArgument(field != null, "field");
		this.field = field;
	}

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
	public TypeDescriptor getTypeDescriptor() {
		if (typeDescriptor == null) {
			synchronized (this) {
				if (typeDescriptor == null) {
					typeDescriptor = new TypeDescriptor(field);
				}
			}
		}
		return typeDescriptor;
	}

	@Override
	public Object get(Object source) {
		return ReflectionUtils.get(field, Modifier.isStatic(field.getModifiers())? null : source);
	}

	@Override
	public FieldGetter rename(String name) {
		FieldGetter fieldGetter = new FieldGetter(field);
		fieldGetter.name = name;
		fieldGetter.typeDescriptor = this.typeDescriptor;
		return fieldGetter;
	}
}