package scw.mapper;

import java.lang.reflect.Method;

public final class Field extends FieldMetadata implements java.lang.Cloneable {
	private static final long serialVersionUID = 1L;
	private final Field parentField;

	public Field(Field parentField, Class<?> declaringClass, String name, java.lang.reflect.Field field, Method getter,
			Method setter) {
		this(parentField,
				(field == null && getter == null) ? null : new DefaultGetter(declaringClass, name, field, getter),
				(field == null && setter == null) ? null : new DefaultSetter(declaringClass, name, field, setter));
	}

	public Field(Field parentField, Getter getter, Setter setter) {
		super(getter, setter);
		this.parentField = parentField;
	}

	public Field getParentField() {
		return parentField;
	}

	@Override
	public String toString() {
		if (isSupportGetter() && isSupportSetter()) {
			return "getter {" + getGetter() + "} setter {" + getSetter() + "}";
		}

		if (isSupportGetter()) {
			return "getter {" + getGetter() + "}";
		}

		if (isSupportSetter()) {
			return "setter {" + getSetter() + "}";
		}
		return super.toString();
	}

	@Override
	public Field clone() {
		return new Field(parentField, getGetter(), getSetter());
	}
}
