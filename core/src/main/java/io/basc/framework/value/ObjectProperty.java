package io.basc.framework.value;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.StringUtils;
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
		this(name, value.getSource(), value.getTypeDescriptor());
	}

	public ObjectProperty(String name, Object value, TypeDescriptor typeDescriptor) {
		super(value, typeDescriptor);
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
}
