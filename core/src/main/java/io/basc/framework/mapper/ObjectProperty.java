package io.basc.framework.mapper;

import io.basc.framework.convert.Converter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.StringUtils;
import io.basc.framework.value.ObjectValue;
import io.basc.framework.value.Value;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObjectProperty extends ObjectValue implements Property, Cloneable {
	private static final long serialVersionUID = 1L;
	private String name;

	public ObjectProperty(String name, Object value) {
		this(name, value, null);
	}

	public ObjectProperty(String name, Value value) {
		this(name, value.getSource(), value.getTypeDescriptor(), value.getConverter());
	}

	public ObjectProperty(String name, Object value, TypeDescriptor typeDescriptor) {
		this(name, value, typeDescriptor, null);
	}

	public ObjectProperty(String name, Object value, TypeDescriptor typeDescriptor,
			Converter<? super Object, ? super Object, ? extends RuntimeException> converter) {
		super(value, typeDescriptor, converter);
		this.name = name;
	}

	@Override
	public ObjectProperty clone() {
		return new ObjectProperty(name, this);
	}

	@Override
	public Property rename(String name) {
		return new ObjectProperty(name, this);
	}

	@Override
	public boolean isValid() {
		return StringUtils.isNotEmpty(name);
	}

	@Override
	public Value createRelative(Object value, TypeDescriptor type) {
		return new ObjectProperty(name, value, type, getConverter());
	}
}
